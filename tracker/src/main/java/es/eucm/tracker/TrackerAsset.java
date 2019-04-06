/**
 * Copyright © 2016 e-UCM (http://www.e-ucm.es/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.eucm.tracker;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import es.eucm.tracker.exceptions.*;
import eu.rageproject.asset.manager.*;

import static es.eucm.tracker.TrackerUtils.*;

/**
 * A tracker asset.
 */
public class TrackerAsset extends BaseAsset {

	/** Unique instance */
	private static TrackerAsset INSTANCE;

	/** JSON serializer/deserializer to use */
	public static final Gson gson = new Gson();

	/** Filename of the settings file */
	private static final String settingsFileName = "TrackerAssetSettings.xml";

	/** Settings. */
	private TrackerAssetSettings settings = null;

	/** The TimeStamp Format */
	private static final String timeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/** Set to indicate that the tracker must finish. */
	private boolean exiting = false;

	/** RegEx format pattern to extract a plain-quoted JSON Value. */
	private static final String tokenRegex = "\"%s\":\"(.+?)\"";
	private Pattern jsonAuthToken = Pattern.compile(String.format(tokenRegex,
			"authToken"));
	private Pattern jsonPlayerId = Pattern.compile(String.format(tokenRegex,
			"playerId"));
	private Pattern jsonSession = Pattern.compile(String.format(tokenRegex,
			"session"));
	private Pattern jsonObjectId = Pattern.compile(String.format(tokenRegex,
			"objectId"));
	private Pattern jsonToken = Pattern.compile(String.format(tokenRegex,
			"token"));
	private Pattern jsonHealth = Pattern.compile(String.format(tokenRegex,
			"status"));

	/** Queue of TrackerEvents to Send.*/
	private ConcurrentQueue<TrackerEvent> queue = new ConcurrentQueue<>();

	/** List of traces flushed while the connection was offline */
	private List<String> tracesPending = new ArrayList<>();

	/** List of traces queued when net storage unable to start */
	private List<TrackerEvent> unsentTraces = new ArrayList<>();

	/** List of Extensions that have to ve added to the next trace */
	private Map<String, Object> extensions = new HashMap<>();

	// sub-trackers

	private AccessibleTracker accessibleTracker;
	private AlternativeTracker alternativeTracker;
	private CompletableTracker completableTracker;
	private GameObjectTracker gameObjectTracker;

	// tracker state
	/**
	 * The tracker has been started
	 */
	private boolean started = false;
	/**
	 * Active connection: ActorObject and ObjectId have been extracted.
	 */
	private boolean active = false;
	/**
	 * Currently connected: a UserToken is present, no Fail() has occurred
	 */
	private boolean connected = false;
	/**
	 * Server health, retrieved by checkHealth
	 */
	private String health = "";

	// general xapi information

	/** Used from TrackerEvent.TraceObject.toJson() */
	private String objectId;
	/** actor object, extracted from JSON inside Success() */
	private Map<String, Object> actorObject;

	private TrackerEventMarshaller marshaller;

	/**
	 * Values that represent the different extensions for traces.
	 *
	 * Extensions can be either 'special' or 'common'.
	 *
	 * Special extensions are stored separately in xAPI,
	 * e.g.: result: { score: { raw: score_value_float> }, success:
	 * success_value_bool, completion: completion_value_bool,
	 * response: response_value_string ... }
	 *
	 * Common extensions are stored in the
	 * result.extensions object (in the xAPI format), e.g.:
	 * result: { ...
	 *  extensions: { .../health: value, .../position: value,
	 * .../progress: value} }
	 */
	public enum Extension implements XApiConstant {
		/*
		 * Special extensions - no ID assigned
		 */
		Score(null, null),
		Success(null, null),
		Response(null, null),
		Completion(null, null),
		
		/*
		 * Common extensions
		 */
		Health(EXTENSIONS_BASE_IRI, "health"),
		Position(EXTENSIONS_BASE_IRI, "position"),
		Progress(EXTENSIONS_BASE_IRI, "progress");
		
		private String baseIri;
		
		private String id;
		
		Extension(String baseIri, String id) {
			this.baseIri = baseIri;
			this.id = id;
		}
		
		@Override
		public String getId() {
			return baseIri+id;
		}

		@Override
		public String getSimpleName() {
			return id;
		}
		
		public boolean isSpecial() {
			return id == null;
		}
	}

	/**
	 * Private constructor, only called (once) from getInstance()
	 */
	public TrackerAsset() {
		TrackerUtils.setLogger(new Logger() {
			@Override
			public void log(Severity severity, String message) {
				INSTANCE.log(severity, message);
			}
		});

		// BEGIN - XXX workaround for BaseAsset#loadSettings(String)
		settings = new TrackerAssetSettings();
		// END
		
		if (loadSettings(settingsFileName)) {
			// settings loaded
		} else {
			// settings not found: make up some defaults
			TrackerAssetSettings defaultSettings = new TrackerAssetSettings();
			defaultSettings.setSecure(true);
			defaultSettings.setHost("rage.e-ucm.es");
			defaultSettings.setPort(443);
			defaultSettings.setBasePath("/api/");
			defaultSettings.setUserToken("");
			defaultSettings.setTrackingCode("");
			defaultSettings.setStorageType(TrackerAssetSettings.StorageTypes.LOCAL);
			defaultSettings.setTraceFormat(TrackerAssetSettings.TraceFormats.CSV);
			defaultSettings.setBatchSize(10);
			setSettings(defaultSettings);
		}

		TraceProcessor bridge = new TraceProcessorBridge();
		accessibleTracker = new AccessibleTracker(bridge);
		alternativeTracker = new AlternativeTracker(bridge);
		completableTracker = new CompletableTracker(bridge);
		gameObjectTracker = new GameObjectTracker(bridge);
	}

	/**
	 * Retrieves the (unique) instance
	 */
	public static TrackerAsset getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TrackerAsset();
		}
		return INSTANCE;
	}

	@Override
	public ISettings getSettings() {
		return this.settings;
	}

	@Override
	public void setSettings(ISettings settings) {
		if (! (settings instanceof TrackerAssetSettings) ) {
			throw new IllegalArgumentException("settings must be an instance of: "+TrackerAssetSettings.class.getName());
		}
		
		if (started) {
			throw new IllegalStateException("Settings must not be changed after the tracker has been started");
		}
		
		this.settings = (TrackerAssetSettings)settings;
		switch(this.settings.getTraceFormat()) {
			case JSON:
				this.marshaller = new JsonTrackerEventMarshaller();
			case XAPI:
				this.marshaller = new XapiTrackerEventMarshaller();
				break;
			case CSV:
			default:
				this.marshaller = new CsvTrackerEventMarshaller();
				break;
		}
	}

	/**
	 * Checks the health of the analytics server.
	 * 
	 * @return true if server replies ok, false otherwise.
	 */
	public boolean checkHealth() {
		RequestResponse response = issueRequest("health", "GET");
		if (response.GetResultAllowed()) {
			Matcher m = jsonHealth.matcher(response.body);
			if (m.find()) {
				health = m.group(1);
				log(Severity.Information, "Health Status=%s", health);
			}
		} else {
			log(Severity.Error, "Request Error: %s-%2$s",
					response.responseCode, response.responsMessage);
		}
		return response.GetResultAllowed();
	}

	/**
	 * Flushes the queue.
	 */
	public void flush() {
		processQueue();
	}

	/**
	 * Login with a Username and Password.
	 * 
	 * After this call, the Success method will extract the token from the
	 * returned .
	 * 
	 * @param username
	 *            The username.
	 * @param password
	 *            The password.
	 * @return true if it succeeds, false if it fails.
	 */
	public boolean login(String username, String password) {
		boolean logged = false;
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		RequestResponse response = this
				.issueRequest(
						"login",
						"POST",
						headers,
						String.format(
								"{\"username\":\"%s\",\"password\":\"%2$s\"}",
								username, password));
		if (response.GetResultAllowed()) {
			Matcher m = jsonToken.matcher(response.body);
			if (m.find()) {
				settings.setUserToken(m.group(1));
				if (settings.getUserToken().startsWith("Bearer ")) {
					settings.setUserToken(settings.getUserToken().substring(
							"Bearer ".length()));
				}

				log(Severity.Information, "Token= %s", settings.getUserToken());
				logged = true;
			}

		} else {
			logged = false;
			log(Severity.Error, "Request Error: %s-%2$s",
					response.responseCode, response.responsMessage);
		}
		return logged;
	}

	/**
	 * Login with an Anonymous PlayerId
	 * 
	 * @param anonymousId
	 *            The playerId of the anonymous player
	 * @return true if it succeeds, false if it fails.
	 */
	public boolean login(String anonymousId) {
		settings.setPlayerId(anonymousId);
		return true;
	}

	/**
	 * Starts with a userToken and trackingCode.
	 * 
	 * @param userToken
	 *            The user token.
	 * @param trackingCode
	 *            The tracking code.
	 */
	public void start(String userToken, String trackingCode) {
		settings.setUserToken(userToken);
		settings.setTrackingCode(trackingCode);
		start();
	}

	/**
	 * Starts with a trackingCode (and with the already extracted UserToken).
	 * 
	 * @param trackingCode
	 *            The tracking code.
	 */
	public void start(String trackingCode) {
		settings.setTrackingCode(trackingCode);
		start();
	}

	/**
	 * Starts Tracking.
	 * Requires:
	 * 1) UserToken (extracted from Login) and
	 * 2) TrackingCode (Shown at Game on a2 server).
	 */
	public void start() {
		started = true;
		switch (settings.getStorageType()) {
			case NET:
				connect();
				break;
			case LOCAL: {
				// Allow LocalStorage if a Bridge is implementing IDataStorage.
				//
				IDataStorage tmp = getInterface(IDataStorage.class);
				connected = (tmp != null);
				active = (tmp != null);
				break;
			}
		}
	}

	private void connect() {
		Map<String, String> headers = new HashMap<>();
		String body = "";
		// ! The UserToken might get swapped for a better one during response
		// ! processing.
		if (!(settings.getUserToken() == null || settings.getUserToken()
				.isEmpty()))
			headers.put("Authorization",
					String.format("Bearer %s", settings.getUserToken()));
		else if (!(settings.getPlayerId() == null || settings.getPlayerId()
				.isEmpty())) {
			headers.put("Content-Type", "application/json");
			headers.put("Accept", "application/json");
			body = "{\"anonymous\" : \"" + settings.getPlayerId() + "\"}";
		}

		RequestResponse response = issueRequest(
				String.format("proxy/gleaner/collector/start/%s",
						settings.getTrackingCode()), "POST", headers, body);
		if (response.GetResultAllowed()) {
			log(Severity.Information, "");
			// Extract AuthToken.
			//
			Matcher m = jsonAuthToken.matcher(response.body);
			if (m.find()) {
				settings.setUserToken(m.group(1));
				log(Severity.Information, "AuthToken= %s",
						settings.getUserToken());
				connected = true;
			}

			// Extract PlayerId.
			//
			m = jsonPlayerId.matcher(response.body);
			if (m.find()) {
				settings.setPlayerId(m.group(1));
				log(Severity.Information, "PlayerId= %s",
						settings.getPlayerId());
			}

			// Extract Session number.
			//
			m = jsonSession.matcher(response.body);
			if (m.find()) {
				log(Severity.Information, "Session= %s", m.group());
			}

			// Extract ObjectID.
			//
			m = jsonObjectId.matcher(response.body);
			if (m.find()) {
				objectId = m.group(1);
				if (!objectId.endsWith("/")) {
					objectId += "/";
				}

				log(Severity.Information, "ObjectId= %s", objectId);
			}

			// Extract Actor Json Object.
			Map<String, Object> jbody = gson.fromJson(response.body, Map.class);
			if (jbody.containsKey("actor")) {
				actorObject = ((Map) jbody.get("actor"));
				log(Severity.Information, "Actor= %s", actorObject);
				active = true;
			}

		} else {
			log(Severity.Error, "Request Error: %s-%2$s",
					response.responseCode, response.responsMessage);
			active = false;
			connected = false;
		}
	}

	/**
	 * Starts with a trackingCode (and with the already extracted UserToken).
	 */
	public void stop() {
		active = false;
		connected = false;
		started = false;
		actorObject= null;
		queue = new ConcurrentQueue<>();
		tracesPending = new ArrayList<>();
	}

	/**
	 * Exit the tracker before closing to guarantee the thread closing.
	 */
	public void exit() {
		exiting = true;
		flush();
	}

	/**
	 * Clears the unflushed Trace queue and the unappended extensions queue
	 */
	public void clear() {
		queue.clear();
		extensions.clear();
	}

	/**
	 * Adds the given value to the Queue.
	 */
	public void trace(TrackerEvent trace) {
		if (!started)
			throw new TrackerException("Tracker Has not been started");

		if (extensions.size() > 0) {
			trace.getResult().setExtensions(extensions);
			extensions.clear();
		}

		queue.enqueue(trace);
	}

	/**
	 * Adds a trace with verb, target and targeit
	 * 
	 */
	public void trace(String verb, String targetType, String targetId) {
		boolean trace = true;
		trace &= check(verb,
				"Tracker: Trace verb can't be null, ignoring. ",
				"Tracker: Trace verb can't be null.",
				TraceException.class);
		trace &= check(targetType,
				"Tracker: Trace Target type can't be null, ignoring. ",
				"Tracker: Trace Target type can't be null.",
				TraceException.class);
		trace &= check(targetId,
				"Tracker: Trace Target ID can't be null, ignoring. ",
				"Tracker: Trace Target ID can't be null.",
				TraceException.class);
		if (trace) {
			TrackerEvent te = new TrackerEvent();
			te.setEvent(new TraceVerb(verb));
			te.setTarget(new TrackerEvent.TraceObject(targetType, targetId));
			trace(te);
		}
	}

	/**
	 * Issue a HTTP Webrequest.
	 * 
	 * @param path
	 *            Full pathname of the file.
	 * @param method
	 *            The method.
	 * @return true if it succeeds, false if it fails.
	 */
	private RequestResponse issueRequest(String path, String method) {
		return issueRequest(path, method, new HashMap<>(), "");
	}

	/**
	 * Issue a HTTP Webrequest.
	 * 
	 * @param path
	 *            Full pathname of the file.
	 * @param method
	 *            The method.
	 * @param headers
	 *            The headers.
	 * @param body
	 *            The body.
	 * @return true if it succeeds, false if it fails.
	 */
	private RequestResponse issueRequest(String path, String method,
			Map<String, String> headers, String body) {
		return issueRequest(path, method, headers, body, settings.getPort());
	}

	/**
	 * Query if this object issue request 2.
	 * 
	 * @param path
	 *            Full pathname of the file.
	 * @param method
	 *            The method.
	 * @param headers
	 *            The headers.
	 * @param body
	 *            The body.
	 * @param port
	 *            The port.
	 * @return true if it succeeds, false if it fails.
	 */
	private RequestResponse issueRequest(String path, String method,
			Map<String, String> headers, String body, Integer port) {
		IWebServiceRequest ds = getInterface(IWebServiceRequest.class);
		RequestResponse response = new RequestResponse();
		if (ds != null) {
			// ! allowedResponsCodes, // TODO default is ok
			// or method.Equals("GET")?string.Empty:body
			/*
			 * RefSupport<RequestResponse> refVar___0 = new
			 * RefSupport<RequestResponse>(); ds.WebServiceRequest(new
			 * RequestSettings(),refVar___0); response = refVar___0.getValue();
			 */

			RequestSettings request = new RequestSettings();
			try {
				String url = String.format(
						"http%s://%2$s%3$s%4$s/%5$s",
						settings.isSecure() ? "s" : "",
						settings.getHost(),
						port == 80 ? "" : String.format(":%d", port),
						isNullOrEmpty(settings.getBasePath().replaceAll(
								"[/]+$", "")) ? "" : settings.getBasePath()
								.replaceAll("[/]+$", ""), path.replaceAll("^/",
								""));
				request.uri = new URI(url);
			} catch (URISyntaxException ex) {
				System.out.println("Invalid URI");
				return response;
			}

			request.method = method;
			request.requestHeaders = headers;
			request.body = body;
			response = ds.WebServiceRequest(request);
		}

		return response;
	}

	/**
	 * Process the queue.
	 */
	private void processQueue() {
		if (!started) {
			log(Severity.Warning,
					"Refusing to send traces without starting tracker (Active is False, should be True)");
			return;
		} else if (!active) {
			connect();
		}

		if (queue.getCount() > 0 ||
				! tracesPending.isEmpty() || ! unsentTraces.isEmpty()) {

			// Extract the traces from the queue and remove from the queue
			List<TrackerEvent> traces = collectTraces();
			// Check if it is connected now
			if (active) {
				if (sendUnloggedTraces()) {
					String data = processTraces(traces,	settings.getTraceFormat());
					if ((!sendPendingTraces() || !(queue.getCount() > 0 && sendTraces(data)))
							&& queue.getCount() > 0) {
						tracesPending.add(data);
					}
				}
			} else {
				unsentTraces.addAll(traces);
			}
			// if backup requested, save a copy
			if (settings.isBackupStorage()) {
				IDataStorage storage = getInterface(IDataStorage.class);
				IAppend appendStorage = getInterface(IAppend.class);
				if (queue.getCount() > 0) {
					String rawData = processTraces(traces, TrackerAssetSettings.TraceFormats.CSV);
					if (appendStorage != null) {
						appendStorage
								.Append(settings.getBackupFile(), rawData);
					} else if (storage != null) {
						String previous = storage.exists(settings
								.getBackupFile()) ? storage.load(settings
								.getBackupFile()) : "";
						if (storage.exists(settings.getBackupFile()))
							storage.save(settings.getBackupFile(), previous
									+ rawData);
						else
							storage.save(settings.getBackupFile(), rawData);
					}
				}
			}
			queue.dequeue(traces.size());
		} else {
			log(Severity.Information, "Nothing to flush");
		}
	}

	List<TrackerEvent> collectTraces() {
		Integer cnt = settings.getBatchSize() == 0 ? Integer.MAX_VALUE
				: settings.getBatchSize();
		cnt = Math.min(queue.getCount(), cnt);
		List<TrackerEvent> traces = Arrays.asList(queue.peek(cnt));
		return traces;
	}

	String processTraces(List<TrackerEvent> traces, TrackerAssetSettings.TraceFormats format) {
		List<String> stringsToSend = new ArrayList<>();
		for (TrackerEvent item : traces) {
			stringsToSend.add(this.marshaller.marshal(item, this));
		}
		StringBuilder data = new StringBuilder(String.join(",\r\n", stringsToSend));
		switch (format) {
			case JSON:
			case XAPI:
				data.insert(0, "[\r\n").append("\r\n]");
				break;
			case CSV:
			default:
				data.append("\r\n");
				break;
		}
		return data.toString();
	}

	boolean sendPendingTraces() {
		while (tracesPending.size() > 0) {
			// Try to send old traces
			log(Severity.Information,
					"Enqueued trace-blocks detected: %s. Processing...",
					tracesPending.size());
			String data = tracesPending.get(0);
			if (!sendTraces(data)) {
				log(Severity.Information, "Error sending enqueued traces");
				break;
			} else {
				// does not keep sending old traces, but continues processing
				// new traces so that get added to tracesPending
				tracesPending.remove(0);
				log(Severity.Information, "Sent enqueued traces OK");
			}
		}
		return tracesPending.size() == 0;
	}

	boolean sendUnloggedTraces() {
		if (unsentTraces.size() > 0 && actorObject != null) {
			String data = processTraces(unsentTraces,
					settings.getTraceFormat());
			boolean sent = sendTraces(data);
			unsentTraces.clear();
			if (!sent)
				tracesPending.add(data);

		}

		return unsentTraces.size() == 0;
	}

	boolean sendTraces(String data) {
		switch (settings.getStorageType()) {
		case LOCAL:
			IDataStorage storage = getInterface(IDataStorage.class);
			IAppend append_storage = getInterface(IAppend.class);
			if (storage != null) {
				String previous = storage.exists(settings.getLogFile()) ? storage
						.load(settings.getLogFile()) : "";
				if (previous.length() > 0) {
					previous = previous.replace("\r\n]", ",\r\n");
					data = data.replace("[\r\n", "");
				}

				storage.save(settings.getLogFile(), previous + data);
			}

			break;
		case NET:
			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/json");
			headers.put("Authorization",
					String.format("%s", settings.getUserToken()));
			log(Severity.Information, "\r\n" + data);
			RequestResponse response = issueRequest(
					"proxy/gleaner/collector/track", "POST", headers, data);
			if (response.GetResultAllowed()) {
				log(Severity.Information, "Track= %s", response.body);
				connected = true;
			} else {
				log(Severity.Error, "Request Error: %s-%s",
						response.responseCode, response.responsMessage);
				log(Severity.Warning,
						"Error flushing, connection disabled temporarily");
				connected = false;
				return false;
			}
			break;

		}
		return true;
	}

	/**
	 * Sets if the following trace has been a success, including this value to
	 * the extensions.
	 * 
	 * @param success
	 *            If set to {@code true} means it has been a success.
	 */
	public void setSuccess(boolean success) {
		setVar(Extension.Success.toString().toLowerCase(), success);
	}

	/**
	 * Sets the score of the following trace, including it to the extensions.
	 * 
	 * @param score
	 *            Score, (Recomended between 0 and 1)
	 */
	public void setScore(float score) {
		if (score < 0 || score > 1)
			log(Severity.Warning,
					"Tracker: Score recommended between 0 and 1 (Current: "
							+ score + ")");

		setVar(Extension.Score.toString().toLowerCase(), score);
	}

	/**
	 * Sets the response. If the player chooses between alternatives, the
	 * response should be the selected alternative.
	 * 
	 * @param response
	 *            Response.
	 */
	public void setResponse(String response) {
		addExtension(Extension.Response.toString().toLowerCase(), response);
	}

	/**
	 * Sets the completion of the following trace extensions. Completion
	 * specifies if something has been completed.
	 * 
	 * @param completion
	 *            If set to {@code true} the trace action has been completed.
	 */
	public void setCompletion(boolean completion) {
		setVar(Extension.Completion.toString().toLowerCase(), completion);
	}

	/**
	 * Sets the coords where the trace takes place.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param z
	 *            The z coordinate.
	 */
	public void setPosition(float x, float y, float z) {
		boolean valid =  ! (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z));
		if ( ! checkIsTrue(valid,
				"Tracker: x, y or z cannot be null.",
				"Tracker: x, y or z cannot be null, ignoring.",
				ValueExtensionException.class)) {
			return;
		}
		addExtension(Extension.Position.toString().toLowerCase(),
				"{\"x\":" + x + ", \"y\": " + y + ", \"z\": " + z + "}");
	}

	/**
	 * Sets the health of the player's character when the trace occurs.
	 * 
	 * @param health
	 *            Health.
	 */
	public void setHealth(float health) {
		if (check(health,
			"Tracker: Health cannot be null, ignoring.",
			"Tracker: Health cannot be null.",
			ValueExtensionException.class)) {
			addExtension(Extension.Health.toString().toLowerCase(), health);
		}
	}

	/**
	 * Adds a variable to the extensions.
	 * 
	 * @param id
	 *            A string identifier. If adding an official xAPI key,
	 *            its official identifier should be used.
	 * @param value
	 *            Value, which may be a boolean, float, string, int, or map
	 */
	public void setVar(String id, Object value) {
		addExtension(id, value);
	}

	/**
	 * Adds a extension to the extension list.
	 * 
	 * @param key
	 *            An extension key, which must be
	 * @param value
	 *            Value.
	 */
	public void setExtension(String key, Object value) {
		addExtension(key, value);
	}

	private void addExtension(String key, Object value) {
		if (checkExtension(key, value)) {
			extensions.put(key, value);
		}
	}

	// getters

	public boolean isExiting() {
		return exiting;
	}

	public boolean isStarted() {
		return started;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isConnected() {
		return connected;
	}

	public String getHealth() {
		return health;
	}

	public String getObjectId() {
		return objectId;
	}

	public Map<String, Object> getActorObject() {
		return actorObject;
	}

	// sub-tracker access

	public AccessibleTracker getAccessible() {
		return accessibleTracker;
	}

	public AlternativeTracker getAlternative() {
		return alternativeTracker;
	}

	public CompletableTracker getCompletable() {
		return completableTracker;
	}

	public GameObjectTracker getGameObject() {
		return gameObjectTracker;
	}
	
	private class TraceProcessorBridge implements TraceProcessor {
		
		/**
		 * Processes a trace, by storing or sending it.
		 * @param trace to add
		 */
		@Override
		public void process(TrackerEvent trace) {
			trace(trace);
		}
		
		/**
		 * Sets the progress of the action.
		 * 
		 * @param progress
		 *            Progress. (Recomended between 0 and 1)
		 */
		@Override
		public void setProgress(float progress) {
			if (progress < 0 || progress > 1)
				log(Severity.Warning,
						"Tracker: Progress recommended between 0 and 1 (Current: "
								+ progress + ")");

			setVar(Extension.Progress.toString().toLowerCase(), progress);
		}		
	}
}
