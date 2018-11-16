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
//
// Translated by CS2J (http://www.cs2j.com): 05/11/2018 15:29:16
//

package es.eucm.tracker;

import com.google.gson.Gson;
import es.eucm.tracker.Exceptions.*;
import es.eucm.tracker.Utils.RefSupport;
import es.eucm.tracker.Utils.TrackerAssetUtils;
import eu.rageproject.asset.manager.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A tracker asset.
 * 
 * TODO - Add method to return the mime-type/content-type.TODO - Add method to
 * return the accept-type.TODO - Check disk based/off-line storage (local).TODO
 * - Serialize Queue for later submission (using queue.ToList()).TODO - Prevent
 * csv/xml/json from net storage and xapi from local storage.
 */
public class TrackerAsset extends BaseAsset {
	private static final Gson gson = new Gson();

	public static long START_DATE = System.currentTimeMillis();
	/**
	 * True when the thread must exit.
	 */
	private boolean exit = false;
	/**
	 * The RegEx to extract a JSON Object. Used to extract 'actor'.
	 * 
	 * NOTE: This regex handles matching brackets by using balancing groups.
	 * This should be tested in Mono if it works there too. NOTE: {} brackets
	 * must be escaped as {{ and }} for String.Format statements. NOTE: \ must
	 * be escaped as \\ in strings.
	 */

	/*
	 * private static final String ObjectRegEx = "\"%s\":(" + // {0} is replaced
	 * by the proprty name, capture only its value in {} brackets. "\\{" + //
	 * Start with a opening brackets. "(?>" + "    [^{{}}]+" + // Capture each
	 * non bracket chracter. "    |    \\{ (?<n>)" + // +1 for opening bracket.
	 * //"    |    \\} (?<m>)" + // -1 for closing bracket. ")*" +
	 * //"(?(n)(?!))" + // Handle unaccounted left brackets with a fail. "\\})";
	 * // Stop at matching bracket.
	 */

	// private const string ObjectRegEx = "\"{0}\":(\\{{(?:.+?)\\}},)";
	/**
	 * Filename of the settings file.
	 */
	private static final String SettingsFileName = "TrackerAssetSettings.xml";
	/**
	 * The TimeStamp Format.
	 */
	private static final String TimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSz";
	/**
	 * The RegEx to extract a plain quoted JSON Value. Used to extract 'token'.
	 */
	private static final String TokenRegEx = "\"%s\":\"(.+?)\"";
	/**
	 * The instance.
	 */
	private static TrackerAsset _instance;
	/**
	 * The instance.
	 */
	private TrackerAssetUtils __Utils;

	TrackerAssetUtils getUtils() {
		return __Utils;
	}

	private void setUtils(TrackerAssetUtils value) {
		__Utils = value;
	}

	/**
	 * Identifier for the object.
	 * 
	 * Extracted from JSON inside Success().
	 */
	private String ObjectId = "";
	/**
	 * Tracker StrictMode
	 */
	private boolean strictMode = true;

	/**
	 * Tracker StrictMode
	 */
	public boolean getStrictMode() {
		return strictMode;
	}

	public void setStrictMode(boolean value) {
		strictMode = value;
	}

	/**
	 * A Regex to extact the actor object from JSON.
	 */
	// private Pattern jsonActor =
	// Pattern.compile(String.format(ObjectRegEx.replaceAll("\\s+", ""),
	// "actor"));
	/**
	 * A Regex to extact the authentication token value from JSON.
	 */
	private Pattern jsonAuthToken = Pattern.compile(String.format(TokenRegEx,
			"authToken"));
	/**
	 * A Regex to extact the playerid token value from JSON.
	 */
	private Pattern jsonPlayerId = Pattern.compile(String.format(TokenRegEx,
			"playerId"));
	/**
	 * A Regex to extact the session token value from JSON.
	 */
	private Pattern jsonSession = Pattern.compile(String.format(TokenRegEx,
			"session"));
	/**
	 * A Regex to extact the objectId value from JSON.
	 */
	private Pattern jsonObjectId = Pattern.compile(String.format(TokenRegEx,
			"objectId"));
	/**
	 * A Regex to extact the token value from JSON.
	 */
	private Pattern jsonToken = Pattern.compile(String.format(TokenRegEx,
			"token"));
	/**
	 * A Regex to extact the status value from JSON.
	 */
	private Pattern jsonHealth = Pattern.compile(String.format(TokenRegEx,
			"status"));
	/**
	 * The queue of TrackerEvents to Send.
	 */
	private ConcurrentQueue<TrackerAsset.TrackerEvent> queue = new ConcurrentQueue<TrackerAsset.TrackerEvent>();
	/**
	 * The list of traces flushed while the connection was offline
	 */
	private List<String> tracesPending = new ArrayList<String>();
	/**
	 * The list of traces sent when net storage unable to start
	 */
	private List<TrackerEvent> tracesUnlogged = new ArrayList<TrackerEvent>();
	/**
	 * Options for controlling the operation.
	 */
	private TrackerAssetSettings settings = null;
	/**
	 * List of Extensions that have to ve added to the next trace
	 */
	private Map<String, Object> extensions = new HashMap<String, Object>();
	/**
	 * Instance of AccessibleTracker
	 */
	private AccessibleTracker accessibletracker;
	/**
	 * Instance of AlternativeTracker
	 */
	private AlternativeTracker alternativetracker;
	/**
	 * Instance of CompletableTracker
	 */
	private CompletableTracker completabletracker;
	/**
	 * Instance of GameObjectTracker
	 */
	private GameObjectTracker gameobjecttracer;

	/**
	 * Prevents a default instance of the TrackerAsset class from being created.
	 */
	public TrackerAsset() {
		super();
		this.setUtils(new TrackerAssetUtils(this));
		settings = new TrackerAssetSettings();
		if (loadSettings(SettingsFileName)) {
		} else {
			// ok
			settings.setSecure(true);
			settings.setHost("rage.e-ucm.es");
			settings.setPort(443);
			settings.setBasePath("/api/");
			settings.setUserToken("");
			settings.setTrackingCode("");
			settings.setStorageType(StorageTypes.local);
			settings.setTraceFormat(TraceFormats.csv);
			settings.setBatchSize(10);
			SaveSettings(SettingsFileName);
		}
	}

	public enum Events {
		/**
		 * Values that represent events.
		 * 
		 * An enum constant representing the choice option.
		 */
		choice,
		/**
		 * An enum constant representing the click option.
		 */
		click,
		/**
		 * An enum constant representing the screen option.
		 */
		screen, var,
		/**
		 * An enum constant representing the variable option.
		 * 
		 * An enum constant representing the zone option.
		 */
		zone
	}

	public enum StorageTypes {
		/**
		 * Values that represent storage types.
		 * 
		 * An enum constant representing the network option.
		 */
		net,
		/**
		 * An enum constant representing the local option.
		 */
		local
	}

	public enum TraceFormats {
		/**
		 * Values that represent trace formats.
		 * 
		 * An enum constant representing the JSON option.
		 */
		json,
		/**
		 * An enum constant representing the XML option.
		 */
		xml,
		/**
		 * An enum constant representing the xAPI option.
		 */
		xapi,
		/**
		 * An enum constant representing the CSV option.
		 */
		csv
	}

	public enum Verb {
		/**
		 * Values that represent the available verbs for traces.
		 */
		Initialized, Progressed, Completed, Accessed, Skipped, Selected, Unlocked, Interacted, Used
	}

	public enum Extension {
		/**
		 * Values that represent the different extensions for traces.
		 */
		/*
		 * Special extensions, those extensions are stored reparatedly in xAPI,
		 * e.g.: result: { score: { raw: <score_value: float> }, success:
		 * <success_value: bool>, completion: <completion_value: bool>,
		 * response: <response_value: string> ... }
		 */
		Score, Success, Response, Completion,
		/*
		 * Common extensions, these extensions are stored in the
		 * result.extensions object (in the xAPI format), e.g.: result: { ...
		 * extensions: { .../health: <value>, .../position: <value>,
		 * .../progress: <value> } }
		 */
		Health, Position, Progress
	}

	/**
	 * Visible when reflecting.
	 * 
	 * The instance.
	 */
	public static TrackerAsset getInstance() {
		if (_instance == null)
			_instance = new TrackerAsset();

		return _instance;
	}

	/**
	 * Gets a value indicating whether the tracker has been started
	 * 
	 * true if started, false if not.
	 */
	private Boolean __Started = false;

	public Boolean getStarted() {
		return __Started;
	}

	public void setStarted(Boolean value) {
		__Started = value;
	}

	/**
	 * Gets a value indicating whether the connection active (ie the ActorObject
	 * and ObjectId have been extracted).
	 * 
	 * true if active, false if not.
	 */
	private Boolean __Active = false;

	public Boolean getActive() {
		return __Active;
	}

	public void setActive(Boolean value) {
		__Active = value;
	}

	/**
	 * Gets a value indicating whether the connected (ie a UserToken is present
	 * and no Fail() has occurred).
	 * 
	 * true if connected, false if not.
	 */
	private Boolean __Connected = false;

	public Boolean getConnected() {
		return __Connected;
	}

	public void setConnected(Boolean value) {
		__Connected = value;
	}

	/**
	 * Gets the health.
	 * 
	 * The health.
	 */
	private String __Health = "";

	public String getHealth() {
		return __Health;
	}

	public void setHealth(String value) {
		__Health = value;
	}

	/**
	 * Gets or sets options for controlling the operation. Besides the toXml()
	 * and fromXml() methods, we never use this property but use it's correctly
	 * typed backing field 'settings' instead. This property should go into each
	 * asset having Settings of its own. The actual class used should be derived
	 * from BaseAsset (and not directly from ISetting). The settings.
	 */
	public ISettings getSettings() {
		return settings;
	}

	public void setSettings(ISettings value) {
		settings = (value instanceof TrackerAssetSettings ? (TrackerAssetSettings) value
				: (TrackerAssetSettings) null);
	}

	/**
	 * The actor object.
	 * 
	 * Extracted from JSON inside Success().
	 */
	private Map<String, Object> __ActorObject;

	private Map<String, Object> getActorObject() {
		return __ActorObject;
	}

	private void setActorObject(Map<String, Object> value) {
		__ActorObject = value;
	}

	/**
	 * Access point for Accessible Traces generation
	 */
	public AccessibleTracker getAccessible() {
		if (accessibletracker == null) {
			accessibletracker = new AccessibleTracker();
			accessibletracker.setTracker(this);
		}

		return accessibletracker;
	}

	/**
	 * Access point for Alternative Traces generation
	 */
	public AlternativeTracker getAlternative() {
		if (alternativetracker == null) {
			alternativetracker = new AlternativeTracker();
			alternativetracker.setTracker(this);
		}

		return alternativetracker;
	}

	/**
	 * Access point for Completable Traces generation
	 */
	public CompletableTracker getCompletable() {
		if (completabletracker == null) {
			completabletracker = new CompletableTracker();
			completabletracker.setTracker(this);
		}

		return completabletracker;
	}

	/**
	 * Access point for Completable Traces generation
	 */
	public GameObjectTracker getGameObject() {
		if (gameobjecttracer == null) {
			gameobjecttracer = new GameObjectTracker();
			gameobjecttracer.setTracker(this);
		}

		return gameobjecttracer;
	}

	/**
	 * Access point for Accessible Traces generation
	 */
	public AccessibleTracker getaccessible() {
		return getAccessible();
	}

	/**
	 * Access point for Alternative Traces generation
	 */
	public AlternativeTracker getalternative() {
		return getAlternative();
	}

	/**
	 * Access point for Completable Traces generation
	 */
	public CompletableTracker getcompletable() {
		return getCompletable();
	}

	/**
	 * Access point for Completable Traces generation
	 */
	public GameObjectTracker gettrackedGameObject() {
		return getGameObject();
	}

	/**
	 * Checks the health of the UCM Tracker.
	 * 
	 * @return true if it succeeds, false if it fails.
	 */
	public Boolean checkHealth() {
		RequestResponse response = issueRequest("health", "GET");
		if (response.GetResultAllowed()) {
			Matcher m = jsonHealth.matcher(response.body);
			if (m.find()) {
				setHealth(m.group(1));
				Log(Severity.Information, "Health Status=%s", getHealth());
			}

		} else {
			Log(Severity.Error, "Request Error: %s-%2$s",
					response.responseCode, response.responsMessage);
		}
		return response.GetResultAllowed();
	}

	/**
	 * Flushes the queue.
	 */
	public void flush() throws XApiException {
		processQueue();
	}

	/**
	 * Flushes the queue.
	 */
	public void requestFlush() throws XApiException {
		flush();
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
	public Boolean login(String username, String password) {
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
								"{{\r\n \"username\": \"%s\",\r\n \"password\": \"%2$s\"\r\n}}",
								username, password));
		if (response.GetResultAllowed()) {
			Matcher m = jsonToken.matcher(response.body);
			if (m.find()) {
				settings.setUserToken(m.group(1));
				if (settings.getUserToken().startsWith("Bearer ")) {
					settings.setUserToken(settings.getUserToken().substring(
							"Bearer ".length()));
				}

				Log(Severity.Information, "Token= %s", settings.getUserToken());
				logged = true;
			}

		} else {
			logged = false;
			Log(Severity.Error, "Request Error: %s-%2$s",
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
	public Boolean login(String anonymousId) {
		this.settings.setPlayerId(anonymousId);
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
	 * Starts Tracking with: 1) An already extracted UserToken (from Login) and
	 * 2) TrackingCode (Shown at Game on a2 server).
	 */
	public void start() {
		this.setStarted(true);
		switch (settings.getStorageType()) {
		case net:
			connect();
			break;
		case local: {
			// Allow LocalStorage if a Bridge is implementing IDataStorage.
			//
			IDataStorage tmp = getInterface(IDataStorage.class);
			setConnected(tmp != null);
			setActive(tmp != null);
		}
			break;

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
			Log(Severity.Information, "");
			// Extract AuthToken.
			//
			Matcher m = jsonAuthToken.matcher(response.body);
			if (m.find()) {
				settings.setUserToken(m.group(1));
				Log(Severity.Information, "AuthToken= $s",
						settings.getUserToken());
				setConnected(true);
			}

			// Extract PlayerId.
			//
			m = jsonPlayerId.matcher(response.body);
			if (m.find()) {
				settings.setPlayerId(m.group(1));
				Log(Severity.Information, "PlayerId= $s",
						settings.getPlayerId());
			}

			// Extract Session number.
			//
			m = jsonSession.matcher(response.body);
			if (m.find()) {
				Log(Severity.Information, "Session= %s", m.group());
			}

			// Extract ObjectID.
			//
			m = jsonObjectId.matcher(response.body);
			if (m.find()) {
				ObjectId = m.group(1);
				if (!ObjectId.endsWith("/")) {
					ObjectId += "/";
				}

				Log(Severity.Information, "ObjectId= %s", ObjectId);
			}

			// Extract Actor Json Object.
			Map<String, Object> jbody = gson.fromJson(response.body, Map.class);
			if (jbody.containsKey("actor")) {
				setActorObject((Map) jbody.get("actor"));
				Log(Severity.Information, "Actor= %s", getActorObject());
				setActive(true);
			}

		} else {
			Log(Severity.Error, "Request Error: %s-%s", response.responseCode,
					response.responsMessage);
			setActive(false);
			setConnected(false);
		}
	}

	/**
	 * Starts with a trackingCode (and with the already extracted UserToken).
	 */
	public void stop() {
		this.setActive(false);
		this.setConnected(false);
		this.setStarted(false);
		this.setActorObject(null);
		this.queue = new ConcurrentQueue<TrackerAsset.TrackerEvent>();
		this.tracesPending = new ArrayList<>();
	}

	/**
	 * Exit the tracker before closing to guarantee the thread closing.
	 */
	public void exit() throws XApiException {
		exit = true;
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
	 * Adds a full trace to the queue, ignoring current extensions.
	 * 
	 * @param trace
	 *            A comma separated string with the values of the trace
	 */
	public void trace(String trace) throws Exception {
		if (trace == null || "".equals(trace))
			throw new TraceException("Trace is be empty or null");

		List<String> parts = TrackerAssetUtils.parseCSV(trace);
		if (parts.size() != 3)
			throw new TraceException(
					"Trace length must be 3 (verb,target_type,target_id)");

		actionTrace(parts.get(0), parts.get(1), parts.get(2));
	}

	/**
	 * Adds a trace with the specified values
	 * 
	 * @param values
	 *            Values of the trace.
	 */
	public void trace(String... values) throws Exception {
		/*
		 * if (strictMode) { Debug.LogWarning
		 * ("Tracker: Trace() method is Obsolete. Ignoring"); return; } else {
		 */
		if (values.length != 3)
			throw new TraceException(
					"Tracker: Trace must have at least 3 arguments: a verb, a target type and a target ID");

		for (int i = 0; i < values.length; i++) {
			if (!getUtils().check(
					values[i],
					"Tracker: Trace param " + i
							+ " is null or empty, ignoring trace.",
					"Tracker: Trace param " + i + " is null or empty",
					TraceException.class))
				return;

		}
		// }
		actionTrace(values[0], values[1], values[2]);
	}

	/**
	 * Adds the given value to the Queue.
	 * 
	 */
	public void trace(TrackerAsset.TrackerEvent trace) throws Exception {
		if (!this.getStarted())
			throw new TrackerException("Tracker Has not been started");

		if (extensions.size() > 0) {
			trace.getResult().setExtensions(new HashMap(extensions));
			extensions.clear();
		}

		queue.enqueue(trace);
	}

	/**
	 * Adds a trace with verb, target and targeit
	 * 
	 */
	public void actionTrace(String verb, String target_type, String target_id)
			throws Exception {
		boolean trace = true;
		trace &= getUtils().check(verb,
				"Tracker: Trace verb can't be null, ignoring. ",
				"Tracker: Trace verb can't be null.", TraceException.class);
		trace &= getUtils().check(target_type,
				"Tracker: Trace Target type can't be null, ignoring. ",
				"Tracker: Trace Target type can't be null.",
				TraceException.class);
		trace &= getUtils()
				.check(target_id,
						"Tracker: Trace Target ID can't be null, ignoring. ",
						"Tracker: Trace Target ID can't be null.",
						TraceException.class);
		if (trace) {
			TrackerEvent te = new TrackerEvent(this);
			te.setEvent(new TrackerEvent.TraceVerb(verb));
			te.setTarget(new TrackerEvent.TraceObject(target_type, target_id));
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
		return issueRequest(path, method, new HashMap<String, String>(), "");
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
						settings.getSecure() ? "s" : "",
						settings.getHost(),
						port == 80 ? "" : String.format(":%d", port),
						IsNullOrEmpty(settings.getBasePath().replaceAll(
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
	private void processQueue() throws XApiException {
		if (!getStarted()) {
			Log(Severity.Warning,
					"Refusing to send traces without starting tracker (Active is False, should be True)");
			return;
		} else if (!getActive()) {
			connect();
		}

		if (queue.getCount() > 0 || tracesPending.size() > 0
				|| tracesUnlogged.size() > 0) {
			// Extract the traces from the queue and remove from the queue
			List<TrackerEvent> traces = collectTraces();
			// Check if it's connected now
			if (getActive()) {
				if (sendUnloggedTraces()) {
					String data = processTraces(traces,
							settings.getTraceFormat());
					if ((!sendPendingTraces() || !(queue.getCount() > 0 && sendTraces(data)))
							&& queue.getCount() > 0)
						tracesPending.add(data);

				}

			} else {
				tracesUnlogged.addAll(traces);
			}
			// if backup requested, save a copy
			if (settings.getBackupStorage()) {
				IDataStorage storage = getInterface(IDataStorage.class);
				IAppend append_storage = getInterface(IAppend.class);
				if (queue.getCount() > 0) {
					String rawData = processTraces(traces, TraceFormats.csv);
					if (append_storage != null) {
						append_storage
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
			Log(Severity.Information, "Nothing to flush");
		}
	}

	List<TrackerEvent> collectTraces() {
		Integer cnt = settings.getBatchSize() == 0 ? Integer.MAX_VALUE
				: settings.getBatchSize();
		cnt = Math.min(queue.getCount(), cnt);
		List<TrackerEvent> traces = Arrays.asList(queue.peek(cnt));
		return traces;
	}

	String processTraces(List<TrackerEvent> traces, TraceFormats format)
			throws XApiException {
		String data = "";
		TrackerAsset.TrackerEvent item;
		List<String> sb = new ArrayList<>();
		for (int i = 0; i < traces.size(); i++) {
			item = traces.get(i);
			switch (format) {
			case json:
				sb.add(item.toJson());
				break;
			case xml:
				sb.add(item.toXml());
				break;
			case xapi:
				sb.add(item.toXapi());
				break;
			default:
				sb.add(item.toCsv());
				break;

			}
		}
		switch (format) {
		case csv:
			data = String.join("\r\n", sb) + "\r\n";
			break;
		case json:
			data = "[\r\n" + String.join(",\r\n", sb) + "\r\n]";
			break;
		case xml:
			data = "<TrackEvents>\r\n" + String.join("\r\n", sb)
					+ "\r\n</TrackEvent>";
			break;
		case xapi:
			data = "[\r\n" + String.join(",\r\n", sb) + "\r\n]";
			break;
		default:
			data = String.join("\r\n", sb);
			break;

		}
		sb.clear();
		return data;
	}

	boolean sendPendingTraces() {
		while (tracesPending.size() > 0) {
			// Try to send old traces
			Log(Severity.Information,
					"Enqueued trace-blocks detected: %s. Processing...",
					tracesPending.size());
			String data = tracesPending.get(0);
			if (!sendTraces(data)) {
				Log(Severity.Information, "Error sending enqueued traces");
				break;
			} else {
				// does not keep sending old traces, but continues processing
				// new traces so that get added to tracesPending
				tracesPending.remove(0);
				Log(Severity.Information, "Sent enqueued traces OK");
			}
		}
		return tracesPending.size() == 0;
	}

	boolean sendUnloggedTraces() throws XApiException {
		if (tracesUnlogged.size() > 0 && this.getActorObject() != null) {
			String data = processTraces(tracesUnlogged,
					settings.getTraceFormat());
			boolean sent = sendTraces(data);
			tracesUnlogged.clear();
			if (!sent)
				tracesPending.add(data);

		}

		return tracesUnlogged.size() == 0;
	}

	boolean sendTraces(String data) {
		switch (settings.getStorageType()) {
		case local:
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
		case net:
			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/json");
			headers.put("Authorization",
					String.format("%s", settings.getUserToken()));
			Log(Severity.Information, "\r\n" + data);
			RequestResponse response = issueRequest(
					"proxy/gleaner/collector/track", "POST", headers, data);
			if (response.GetResultAllowed()) {
				Log(Severity.Information, "Track= %s", response.body);
				setConnected(true);
			} else {
				Log(Severity.Error, "Request Error: %s-%s",
						response.responseCode, response.responsMessage);
				Log(Severity.Warning,
						"Error flushing, connection disabled temporaly");
				setConnected(false);
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
	public void setSuccess(boolean success) throws Exception {
		setVar(Extension.Success.toString().toLowerCase(), success);
	}

	/**
	 * Sets the score of the following trace, including it to the extensions.
	 * 
	 * @param score
	 *            Score, (Recomended between 0 and 1)
	 */
	public void setScore(float score) throws Exception {
		if (score < 0 || score > 1)
			Log(Severity.Warning,
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
	public void setResponse(String response) throws Exception {
		addExtension(Extension.Response.toString().toLowerCase(), response);
	}

	/**
	 * Sets the completion of the following trace extensions. Completion
	 * specifies if something has been completed.
	 * 
	 * @param completion
	 *            If set to {@code true} the trace action has been completed.
	 */
	public void setCompletion(boolean completion) throws Exception {
		setVar(Extension.Completion.toString().toLowerCase(), completion);
	}

	/**
	 * Sets the progress of the action.
	 * 
	 * @param progress
	 *            Progress. (Recomended between 0 and 1)
	 */
	public void setProgress(float progress) throws Exception {
		if (progress < 0 || progress > 1)
			Log(Severity.Warning,
					"Tracker: Progress recommended between 0 and 1 (Current: "
							+ progress + ")");

		setVar(Extension.Progress.toString().toLowerCase(), progress);
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
	public void setPosition(float x, float y, float z) throws Exception {
		if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z)) {
			if (getStrictMode())
				throw new ValueExtensionException(
						"Tracker: x, y or z cant be null.");
			else {
				Log(Severity.Information,
						"Tracker: x, y or z cant be null, ignoring.");
				return;
			}
		}

		addExtension(Extension.Position.toString().toLowerCase(), "{\"x\":" + x
				+ ", \"y\": " + y + ", \"z\": " + z + "}");
	}

	/**
	 * Sets the health of the player's character when the trace occurs.
	 * 
	 * @param health
	 *            Health.
	 */
	public void setHealth(float health) throws Exception {
		if (getUtils().check(health, "Tracker: Health cant be null, ignoring.",
				"Tracker: Health cant be null.", ValueExtensionException.class))
			addExtension(Extension.Health.toString().toLowerCase(), health);

	}

	/**
	 * Adds a variable to the extensions.
	 * 
	 * @param id
	 *            Identifier.
	 * @param value
	 *            Value.
	 */
	public void setVar(String id, Dictionary<String, Boolean> value)
			throws Exception {
		addExtension(id, value);
	}

	/**
	 * Adds a variable to the extensions.
	 * 
	 * @param id
	 *            Identifier.
	 * @param value
	 *            Value.
	 */
	public void setVar(String id, String value) throws Exception {
		addExtension(id, value);
	}

	/**
	 * Adds a variable to the extensions.
	 * 
	 * @param key
	 *            Key.
	 * @param value
	 *            Value.
	 */
	public void setVar(String key, int value) throws Exception {
		addExtension(key, value);
	}

	/**
	 * Adds a variable to the extensions.
	 * 
	 * @param key
	 *            Key.
	 * @param value
	 *            Value.
	 */
	public void setVar(String key, float value) throws Exception {
		addExtension(key, value);
	}

	/**
	 * Adds a variable to the extensions.
	 * 
	 * @param key
	 *            Key.
	 * @param value
	 *            Value.
	 */
	public void setVar(String key, double value) throws Exception {
		addExtension(key, value);
	}

	/**
	 * Adds a variable to the extensions.
	 * 
	 * @param key
	 *            Key.
	 * @param value
	 *            Value.
	 */
	public void setVar(String key, boolean value) throws Exception {
		addExtension(key, value);
	}

	/**
	 * Adds a extension to the extension list.
	 * 
	 * @param key
	 *            Key.
	 * @param value
	 *            Value.
	 */
	public void setExtension(String key, float value) throws Exception {
		addExtension(key, value);
	}

	/**
	 * Adds a extension to the extension list.
	 * 
	 * @param key
	 *            Key.
	 * @param value
	 *            Value.
	 */
	public void setExtension(String key, double value) throws Exception {
		addExtension(key, value);
	}

	/**
	 * Adds a extension to the extension list.
	 * 
	 * @param key
	 *            Key.
	 * @param value
	 *            Value.
	 */
	public void setExtension(String key, Object value) throws Exception {
		addExtension(key, value);
	}

	private void addExtension(String key, Object value) throws Exception {
		if (getUtils().checkExtension(key, value)) {
			extensions.put(key, value);
		}

	}

	/**
	 * Interface that subtrackers must implement.
	 */
	public interface IGameObjectTracker {
		void setTracker(TrackerAsset tracker);

	}

	/**
	 * A tracker event.
	 */
	public static class TrackerEvent {
		private static Map<String, String> verbIds = null;
		private static Map<String, String> objectIds = null;
		private static Map<String, String> extensionIds = null;
		private TrackerAsset.TrackerEvent.TraceVerb verb;
		private TrackerAsset.TrackerEvent.TraceObject target;
		private TrackerAsset.TrackerEvent.TraceResult result;

		public TrackerEvent(TrackerAsset tracker) {
			this.setTracker(tracker);
			this.setTimeStamp(System.currentTimeMillis());
			this.setResult(new TrackerAsset.TrackerEvent.TraceResult());
		}

		private static Map<String, String> getVerbIDs() {
			if (verbIds == null) {
				verbIds = new HashMap<String, String>();
				verbIds.put(TrackerAsset.Verb.Initialized.toString()
						.toLowerCase(),
						"http://adlnet.gov/expapi/verbs/initialized");
				verbIds.put(TrackerAsset.Verb.Progressed.toString()
						.toLowerCase(),
						"http://adlnet.gov/expapi/verbs/progressed");
				verbIds.put(TrackerAsset.Verb.Completed.toString()
						.toLowerCase(),
						"http://adlnet.gov/expapi/verbs/completed");
				verbIds.put(
						TrackerAsset.Verb.Accessed.toString().toLowerCase(),
						"https://w3id.org/xapi/seriousgames/verbs/accessed");
				verbIds.put(TrackerAsset.Verb.Skipped.toString().toLowerCase(),
						"http://id.tincanapi.com/verb/skipped");
				verbIds.put(
						TrackerAsset.Verb.Selected.toString().toLowerCase(),
						"https://w3id.org/xapi/adb/verbs/selected");
				verbIds.put(
						TrackerAsset.Verb.Unlocked.toString().toLowerCase(),
						"https://w3id.org/xapi/seriousgames/verbs/unlocked");
				verbIds.put(TrackerAsset.Verb.Interacted.toString()
						.toLowerCase(),
						"http://adlnet.gov/expapi/verbs/interacted");
				verbIds.put(TrackerAsset.Verb.Used.toString().toLowerCase(),
						"https://w3id.org/xapi/seriousgames/verbs/used");

			}

			return verbIds;
		}

		private static Map<String, String> getObjectIDs() {
			if (objectIds == null) {
				objectIds = new HashMap<String, String>();

				// Completable
				objectIds
						.put(CompletableTracker.Completable.Game.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/serious-game");
				objectIds
						.put(CompletableTracker.Completable.Session.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/session");
				objectIds
						.put(CompletableTracker.Completable.Level.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/level");
				objectIds
						.put(CompletableTracker.Completable.Quest.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/quest");
				objectIds
						.put(CompletableTracker.Completable.Stage.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/stage");
				objectIds
						.put(CompletableTracker.Completable.Combat.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/combat");
				objectIds
						.put(CompletableTracker.Completable.StoryNode
								.toString().toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/story-node");
				objectIds
						.put(CompletableTracker.Completable.Race.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/race");
				objectIds
						.put(CompletableTracker.Completable.Completable
								.toString().toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/completable");

				// Acceesible
				objectIds
						.put(AccessibleTracker.Accessible.Screen.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/screen");
				objectIds
						.put(AccessibleTracker.Accessible.Area.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/area");
				objectIds
						.put(AccessibleTracker.Accessible.Zone.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/zone");
				objectIds
						.put(AccessibleTracker.Accessible.Cutscene.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/cutscene");
				objectIds
						.put(AccessibleTracker.Accessible.Accessible.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/accessible");

				// Alternative
				objectIds.put(AlternativeTracker.Alternative.Question
						.toString().toLowerCase(),
						"http://adlnet.gov/expapi/activities/question");
				objectIds
						.put(AlternativeTracker.Alternative.Menu.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/menu");
				objectIds
						.put(AlternativeTracker.Alternative.Dialog.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/dialog-tree");
				objectIds
						.put(AlternativeTracker.Alternative.Path.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/path");
				objectIds
						.put(AlternativeTracker.Alternative.Arena.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/arena");
				objectIds
						.put(AlternativeTracker.Alternative.Alternative
								.toString().toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/alternative");

				// GameObject
				objectIds
						.put(GameObjectTracker.TrackedGameObject.Enemy
								.toString().toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/enemy");
				objectIds
						.put(GameObjectTracker.TrackedGameObject.Npc.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/non-player-character");
				objectIds
						.put(GameObjectTracker.TrackedGameObject.Item
								.toString().toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/item");
				objectIds
						.put(GameObjectTracker.TrackedGameObject.GameObject
								.toString().toLowerCase(),
								"https://w3id.org/xapi/seriousgames/activity-types/game-object");
			}

			return objectIds;
		}

		private static Map<String, String> getExtensionIDs() {
			if (extensionIds == null) {
				extensionIds = new HashMap<String, String>();
				extensionIds.put(TrackerAsset.Extension.Health.toString()
						.toLowerCase(),
						"https://w3id.org/xapi/seriousgames/extensions/health");
				extensionIds
						.put(TrackerAsset.Extension.Position.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/extensions/position");
				extensionIds
						.put(TrackerAsset.Extension.Progress.toString()
								.toLowerCase(),
								"https://w3id.org/xapi/seriousgames/extensions/progress");
			}

			return extensionIds;
		}

		/**
		 * Gets or sets the Tracker
		 * 
		 * The Tracker.
		 */
		private TrackerAsset __Tracker;

		public TrackerAsset getTracker() {
			return __Tracker;
		}

		public void setTracker(TrackerAsset value) {
			__Tracker = value;
		}

		/**
		 * Gets or sets the event.
		 * 
		 * The event.
		 */
		public TrackerAsset.TrackerEvent.TraceVerb getEvent() {
			return verb;
		}

		public void setEvent(TrackerAsset.TrackerEvent.TraceVerb value)
				throws Exception {
			this.verb = value;
			this.verb.setParent(this);
			this.verb.isValid();
		}

		/**
		 * Gets or sets the Target for the.
		 * 
		 * The target.
		 */
		public TrackerAsset.TrackerEvent.TraceObject getTarget() {
			return target;
		}

		public void setTarget(TrackerAsset.TrackerEvent.TraceObject value) {
			this.target = value;
			this.target.setParent(this);
		}

		/**
		 * Gets or sets the Result for the.
		 * 
		 * The Result.
		 */
		public TrackerAsset.TrackerEvent.TraceResult getResult() {
			return result;
		}

		public void setResult(TrackerAsset.TrackerEvent.TraceResult value) {
			this.result = value;
			this.result.setParent(this);
		}

		/**
		 * Gets the Date/Time of the time stamp.
		 * 
		 * The time stamp.
		 */
		private double __TimeStamp = 0;

		public double getTimeStamp() {
			return __TimeStamp;
		}

		public void setTimeStamp(double value) {
			__TimeStamp = value;
		}

		/**
		 * Converts this object to a CSV Item.
		 * 
		 * @return This object as a string.
		 */
		public String toCsv() {
			return this.getTimeStamp()
					+ ","
					+ getEvent().toCsv()
					+ ","
					+ getTarget().toCsv()
					+ (this.getResult() == null
							|| IsNullOrEmpty(this.getResult().toCsv()) ? ""
							: this.getResult().toCsv());
		}

		/**
		 * Converts this object to a JSON Item.
		 * 
		 * @return This object as a string.
		 */
		public String toJson() throws XApiException {
			Map json = new HashMap();
			json.put("actor",
					(getTracker().getActorObject() == null) ? new HashMap<>()
							: getTracker().getActorObject());
			json.put("event", getEvent().toJson());
			json.put("target", getTarget().toJson());
			Map<String, Object> result = getResult().toJson();
			if (result.size() > 0)
				json.put("result", result);

			Date date = new Date(System.currentTimeMillis());
			DateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSSz");
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			String dateFormatted = formatter.format(date);

			json.put("timestamp", dateFormatted);
			return gson.toJson(json, Map.class);
		}

		/**
		 * Converts this object to an XML Item.
		 * 
		 * @return This object as a string.
		 */
		public String toXml() {
			return ""; // "<TrackEvent \"timestamp\"=\"" +
						// this.getTimeStamp().ToString(TimeFormat) + "\"" +
						// " \"event\"=\"" +
						// verbIds[this.getEvent().ToString().ToLower()] + "\""
						// + " \"target\"=\"" + this.getTarget() + "\"" +
						// (this.getResult() == null ||
						// String.IsNullOrEmpty(this.getResult().toXml()) ?
						// " />" : "><![CDATA[" + this.getResult().toXml() +
						// "]]></TrackEvent>");
		}

		/**
		 * Converts this object to an xapi.
		 * 
		 * @return This object as a string.
		 */
		public String toXapi() throws XApiException {
			Map json = new HashMap();
			json.put("actor",
					(getTracker().getActorObject() == null) ? new HashMap<>()
							: getTracker().getActorObject());
			json.put("verb", getEvent().toXapi());
			json.put("object", getTarget().toXapi());
			Map<String, Object> result = getResult().toXapi();
			if (result.size() > 0)
				json.put("result", result);

			Date date = new Date(System.currentTimeMillis());
			DateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSSz");
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			String dateFormatted = formatter.format(date);

			json.put("timestamp", dateFormatted);
			return gson.toJson(json, Map.class);
		}

		/**
		 * Enquotes.
		 * 
		 * Both checks could be combined.
		 * 
		 * @param value
		 *            The value.
		 * @return A string.
		 */
		private String enquote(String value) {
			if (value.contains("\"")) {
				return String.format("\"%s\"", value.replace("\"", "\"\""));
			} else // 1) Replace one quote by two quotes and enquote the whole
					// string.
			if (value.contains("\r\n") || value.contains(",")) {
				return String.format("\"%s\"", value);
			}

			return value;
		}

		// 2) If the string contains a CRLF or , enquote the whole string.
		private boolean isValid() throws Exception {
			boolean check = true;
			check &= getEvent().isValid();
			check &= getTarget().isValid();
			check &= getResult().isValid();
			return true;
		}

		/**
		 * Class for Target storage.
		 */
		public static class TraceObject {
			String _type;
			String _id;
			private TrackerAsset.TrackerEvent __Parent;

			public TrackerAsset.TrackerEvent getParent() {
				return __Parent;
			}

			public void setParent(TrackerAsset.TrackerEvent value) {
				__Parent = value;
			}

			public String getType() {
				return _type;
			}

			public void setType(String value) throws Exception {
				if (getParent() == null
						|| getParent()
								.getTracker()
								.getUtils()
								.check(value,
										"xAPI Exception: Target Type is null or empty. Ignoring.",
										"xAPI Exception: Target Type can't be null or empty.",
										TargetXApiException.class))
					_type = value;
			}

			public String getID() {
				return _id;
			}

			public void setID(String value) throws Exception {
				if (getParent() == null
						|| getParent()
								.getTracker()
								.getUtils()
								.check(value,
										"xAPI Exception: Target ID is null or empty. Ignoring.",
										"xAPI Exception: Target ID can't be null or empty.",
										TargetXApiException.class))
					_id = value;

			}

			private Map<String, Object> __Definition;

			public Map<String, Object> getDefinition() {
				return __Definition;
			}

			public void setDefinition(Map<String, Object> value) {
				__Definition = value;
			}

			public TraceObject(String type, String id) throws Exception {
				this.setType(type);
				this.setID(id);
			}

			public String toCsv() {
				return getType().replace(",", "\\,") + ","
						+ getID().replace(",", "\\,");
			}

			public Map<String, Object> toJson() throws TargetXApiException {
				String typeKey = getType();

				if (getObjectIDs().containsKey(getType())) {
					typeKey = getObjectIDs().get(getType());
				} else if (getParent().getTracker().getStrictMode()) {
					throw (new TargetXApiException(
							"Tracker-xAPI: Unknown definition for target type: "
									+ getType()));
				} else {
					getParent().getTracker().Log(
							Severity.Warning,
							"Tracker-xAPI: Unknown definition for target type: "
									+ getType());
				}

				Map<String, Object> obj = new HashMap<>(), definition = new HashMap<>();

				obj.put("id",
						((getParent().getTracker().getActorObject() != null) ? getParent()
								.getTracker().ObjectId : "")
								+ getID());
				definition.put("type", typeKey);
				obj.put("definition", definition);

				return obj;
			}

			public String toXml() {
				return getType() + "," + getID();
			}

			// TODO;
			public Map<String, Object> toXapi() throws TargetXApiException {
				return this.toJson();
			}

			public boolean isValid() throws Exception {
				return TrackerAssetUtils.quickCheck(getType())
						&& TrackerAssetUtils.quickCheck(getID());
			}

		}

		/**
		 * Class for Verb storage.
		 */
		public static class TraceVerb {
			private TrackerAsset.TrackerEvent __Parent;

			public TrackerAsset.TrackerEvent getParent() {
				return __Parent;
			}

			public void setParent(TrackerAsset.TrackerEvent value) {
				__Parent = value;
			}

			private String sverb = "";
			private Verb vverb = Verb.Initialized;

			public String getsVerb() {
				return sverb;
			}

			public void setsVerb(String value) throws TrackerException {
				sverb = value;

				RefSupport<Verb> rv = new RefSupport<Verb>();
				if (TrackerAssetUtils.ParseEnum(value, rv, Verb.class)) {
					sverb = value.toLowerCase();
					this.vverb = rv.getValue();
				} else if (getParent() != null) {
					if (getParent().getTracker().getStrictMode()) {
						throw (new VerbXApiException(
								"Tracker-xAPI: Unknown definition for verb: "
										+ value));
					} else {
						getParent().getTracker().Log(
								Severity.Warning,
								"Tracker-xAPI: Unknown definition for verb: "
										+ value);
					}

				}
			}

			public Verb getVerb() {
				return vverb;
			}

			public void setVerb(Verb value) {
				sverb = value.toString().toLowerCase();
				vverb = value;
			}

			public TraceVerb(Verb verb) {
				this.setVerb(verb);
			}

			public TraceVerb(String verb) throws Exception {
				this.setsVerb(verb);
			}

			public String toCsv() {
				return this.getsVerb().replace(",", "\\,");
			}

			public Map<String, Object> toJson() {
				String id = this.getsVerb();
				Map<String, Object> verb = new HashMap<>();
				if (getVerbIDs().containsKey(id)) {
					verb.put("id", getVerbIDs().get(id));
				} else {
					verb.put("id", sverb);
				}
				return verb;
			}

			public String toXml() {
				return "";
			}

			public Map<String, Object> toXapi() {
				return this.toJson();
			}

			public boolean isValid() throws Exception {
				boolean check = true;
				if (getParent() != null)
					setsVerb(getsVerb());

				return check && TrackerAssetUtils.quickCheck(sverb);
			}

		}

		/**
		 * Class for Result storage.
		 */
		public static class TraceResult {
			private TrackerAsset.TrackerEvent __Parent;

			public TrackerAsset.TrackerEvent getParent() {
				return __Parent;
			}

			public void setParent(TrackerAsset.TrackerEvent value) {
				__Parent = value;
			}

			private int success = -1;
			private int completion = -1;
			private float score = Float.NaN;

			public boolean getSuccess() {
				return success == 1 ? true : false;
			}

			public void setSuccess(boolean value) {
				success = value ? 1 : 0;
			}

			public boolean getCompletion() {
				return completion == 1 ? true : false;
			}

			public void setCompletion(boolean value) {
				completion = value ? 1 : 0;
			}

			String res = new String();

			public String getResponse() {
				return res;
			}

			public void setResponse(String value) throws Exception {
				if (getParent() == null
						|| getParent()
								.getTracker()
								.getUtils()
								.check(value,
										"xAPI extension: response Empty or null. Ignoring",
										"xAPI extension: response can't be empty or null",
										ValueExtensionException.class))
					res = value;

			}

			public float getScore() {
				return score;
			}

			public void setScore(float value) throws Exception {
				if (getParent() == null
						|| getParent()
								.getTracker()
								.getUtils()
								.check(value,
										"xAPI extension: score null or NaN. Ignoring",
										"xAPI extension: score can't be null or NaN.",
										ValueExtensionException.class))
					score = value;

			}

			Map<String, Object> extdir = new HashMap<String, Object>();

			public Map<String, Object> getExtensions() {
				return extdir;
			}

			public void setExtensions(Map<String, Object> value)
					throws Exception {
				extdir = new HashMap<String, Object>();

				Iterator it = value.entrySet().iterator();

				while (it.hasNext()) {
					Map.Entry<String, Object> extension = (Map.Entry<String, Object>) it
							.next();

					switch (extension.getKey().toLowerCase()) {
					case "success":
						setSuccess((boolean) extension.getValue());
						break;
					case "completion":
						setCompletion((boolean) extension.getValue());
						break;
					case "response":
						setResponse((String) extension.getValue());
						break;
					case "score":
						setScore((float) extension.getValue());
						break;
					default:
						extdir.put(extension.getKey(), extension.getValue());
						break;
					}
				}
			}

			public String toCsv() {
				String result = ((success > -1) ? ",success"
						+ intToBoolString(success) : "")
						+ ((completion > -1) ? ",completion"
								+ intToBoolString(completion) : "")
						+ ((!IsNullOrEmpty(getResponse())) ? ",response,"
								+ getResponse().replace(",", "\\,") : "")
						+ ((!Float.isNaN(score)) ? ",score,"
								+ Float.toString(score).replace(',', '.') : "");

				if (getExtensions() != null && getExtensions().size() > 0) {

					Iterator it = getExtensions().entrySet().iterator();

					while (it.hasNext()) {
						Map.Entry<String, Object> extension = (Map.Entry<String, Object>) it
								.next();
						result += "," + extension.getKey().replace(",", "\\,")
								+ ",";
						if (extension.getValue() != null) {
							if (extension.getValue().getClass() == String.class) {
								result += extension.getValue().toString()
										.replace(",", "\\,");
							} else if ((extension.getValue() instanceof Float)) {
								result += Float.toString(
										(Float) extension.getValue()).replace(
										',', '.');
							} else if ((extension.getValue() instanceof Double)) {
								result += Double.toString(
										(Double) extension.getValue()).replace(
										',', '.');
							} else if ((extension.getValue() instanceof Integer)) {
								result += Integer.toString(
										(Integer) extension.getValue())
										.replace(',', '.');
							} else if (extension.getValue().getClass() == HashMap.class) {
								Map<String, Object> map = (HashMap<String, Object>) extension
										.getValue();
								String smap = "";

								Iterator it2 = ((HashMap<String, Object>) extension
										.getValue()).entrySet().iterator();

								while (it2.hasNext()) {
									Map.Entry<String, Object> t = (Map.Entry<String, Object>) it2
											.next();
									smap += t.getKey()
											+ "="
											+ t.getValue().toString()
													.toLowerCase() + "-";
								}

								result += smap.replaceAll("[-]+$", "");
							} else {
								result += String.valueOf(extension.getValue());
							}
						}
					}
				}

				return result;
			}

			public Map<String, Object> toJson() {
				Map<String, Object> result = new HashMap<>();
				if (success != -1)
					result.put("success", getSuccess());

				if (completion != -1)
					result.put("completion", getCompletion());

				if (!IsNullOrEmpty(getResponse()))
					result.put("response", getResponse());

				if (!Float.isNaN(score)) {
					Map<String, Object> s = new HashMap<>();
					s.put("raw", score);
					result.put("score", s);
				}

				Map<String, Object> extensions = new HashMap<>();
				if (getExtensions().size() > 0) {
					Iterator it = getExtensions().entrySet().iterator();

					while (it.hasNext()) {
						Map.Entry<String, Object> extension = (Map.Entry<String, Object>) it
								.next();

						if (getExtensionIDs().containsKey(extension.getKey())) {
							extensions.put(
									getExtensionIDs().get(extension.getKey()),
									extension.getValue());
						} else {
							extensions.put(extension.getKey(),
									extension.getValue());
						}
					}
					result.put("extensions", extensions);
				}

				return result;
			}

			public String toXml() {
				return "";
			}

			// TODO;
			public Map<String, Object> toXapi() {
				return this.toJson();
			}

			private static String intToBoolString(int property) {
				String ret = "";
				if (property >= 1) {
					ret = ",true";
				} else if (property == 0) {
					ret = ",false";
				}

				return ret;
			}

			public boolean isValid() throws Exception {
				boolean valid = true;
				if (!IsNullOrEmpty(getResponse()))
					this.setResponse(getResponse());

				if (!Float.isNaN(getScore()))
					this.setScore(getScore());

				Map<String, Object> result = new HashMap<String, Object>();
				if (success != -1)
					valid &= TrackerAssetUtils.quickCheck(success);

				if (completion != -1)
					valid &= TrackerAssetUtils.quickCheck(completion);

				if (!IsNullOrEmpty(getResponse()))
					valid &= TrackerAssetUtils.quickCheck(getResponse());

				if (!Float.isNaN(score)) {
					valid &= TrackerAssetUtils.quickCheck(score);
				}

				if (getExtensions() != null && getExtensions().size() > 0) {
					Iterator it = getExtensions().entrySet().iterator();

					while (it.hasNext()) {
						Map.Entry<String, Object> extension = (Map.Entry<String, Object>) it
								.next();
						valid &= TrackerAssetUtils.quickCheckExtension(
								extension.getKey(), extension.getValue());
					}
				}

				return valid;
			}

		}

	}

	private static boolean IsNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}

}
