package es.eucm.tracker;

import es.eucm.tracker.exceptions.*;
import eu.rageproject.asset.manager.Severity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static es.eucm.tracker.TrackerUtils.*;

/**
 * A tracker event.
 */
public class TrackerEvent {

	private static Map<String, String> objectIds = null;
	private static Map<String, String> extensionIds = TrackerUtils.buildXApiMap(
			TrackerAsset.Extension.class);
	private TraceVerb verb;
	private TraceObject target;
	private TraceResult result;

	public TrackerEvent() {
		timeStamp = System.currentTimeMillis();
		result = new TraceResult();
	}

	private static Map<String, String> getObjectIDs() {
		if (objectIds == null) {
			objectIds = new HashMap<>();

			objectIds.putAll(TrackerUtils.buildXApiMap(
					CompletableTracker.Completable.class));

			objectIds.putAll(TrackerUtils.buildXApiMap(
					AccessibleTracker.Accessible.class));

			objectIds.putAll(TrackerUtils.buildXApiMap(
					AlternativeTracker.Alternative.class));

			objectIds.putAll(TrackerUtils.buildXApiMap(
					GameObjectTracker.TrackedGameObject.class));
		}

		return objectIds;
	}

	/**
	 * Gets or sets the event.
	 *
	 * The event.
	 */
	public TraceVerb getEvent() {
		return verb;
	}

	public void setEvent(TraceVerb value) {
		this.verb = value;
		this.verb.isValid();
	}

	/**
	 * Gets or sets the Target for the.
	 *
	 * The target.
	 */
	public TraceObject getTarget() {
		return target;
	}

	public void setTarget(TraceObject value) {
		this.target = value;
	}

	/**
	 * Gets or sets the Result for the.
	 *
	 * The Result.
	 */
	public TraceResult getResult() {
		return result;
	}

	public void setResult(TraceResult value) {
		this.result = value;
	}

	/**
	 * Gets the Date/Time of the time stamp.
	 *
	 * The time stamp.
	 */
	private double timeStamp;

	public double getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Converts this object to a CSV Item.
	 *
	 * @return This object as a string.
	 */
	public String toCsv() {
		boolean goodResultCSV = ! (getResult() == null) &&
				notNullEmptyOrNan(getResult().toCsv());
		return getTimeStamp()
				+ ","
				+ getEvent().toCsv()
				+ ","
				+ getTarget().toCsv()
				+ (goodResultCSV ? getResult().toCsv() : "");
	}

	/**
	 * Converts this object to a JSON Item.
	 *
	 * @return This object as a string.
	 */
	public String toJson(TrackerAsset tracker) throws XApiException {
		Map json = new HashMap<String, Object>();
		json.put("actor",
				(tracker == null || tracker.getActorObject() == null) ?
						new HashMap<>() : tracker.getActorObject());
		json.put("event", getEvent().toJson());
		json.put("target", getTarget().toJson(tracker));
		Map<String, Object> result = getResult().toJson();
		if (result.size() > 0)
			json.put("result", result);

		Date date = new Date(System.currentTimeMillis());
		DateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateFormatted = formatter.format(date);

		json.put("timestamp", dateFormatted);
		return TrackerAsset.gson.toJson(json, Map.class);
	}

	/**
	 * Converts this object to an XML Item.
	 *
	 * @return This object as a string.
	 */
	public String toXml(TrackerAsset tracker) {
		return ""; // "<TrackEvent \"timestamp\"=\"" +
					// this.getTimeStamp().ToString(TimeFormat) + "\"" +
					// " \"event\"=\"" +
					// verbIds[this.getEvent().ToString().ToLower()] + "\""
					// + " \"target\"=\"" + this.getTarget() + "\"" +
					// (this.getResult() == null ||
					// String.isNullOrEmpty(this.getResult().toXml()) ?
					// " />" : "><![CDATA[" + this.getResult().toXml() +
					// "]]></TrackEvent>");
	}

	/**
	 * Converts this object to an xapi.
	 *
	 * @return This object as a string.
	 */
	public String toXapi(TrackerAsset tracker) throws XApiException {
		Map json = new HashMap();
		json.put("actor",
				(tracker == null || tracker.getActorObject() == null) ? new HashMap<>()
						: tracker.getActorObject());
		json.put("verb", getEvent().toXapi());
		json.put("object", getTarget().toXapi(tracker));
		Map<String, Object> result = getResult().toXapi();
		if (result.size() > 0)
			json.put("result", result);

		Date date = new Date(System.currentTimeMillis());
		DateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String dateFormatted = formatter.format(date);

		json.put("timestamp", dateFormatted);
		return TrackerAsset.gson.toJson(json, Map.class);
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
		return check;
	}

	/**
	 * Class for Target storage.
	 */
	public static class TraceObject {
		private String type;
		private String id;

		public String getType() {
			return type;
		}

		public void setType(String value) {
			if (check(value,
				"xAPI Exception: Target Type is null or empty. Ignoring.",
				"xAPI Exception: Target Type can't be null or empty.",
				TargetXApiException.class))
				type = value;
		}

		public String getID() {
			return id;
		}

		public void setID(String value) {
			if (check(value,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class))
				id = value;

		}

		public TraceObject(String type, String id){
			this.setType(type);
			this.setID(id);
		}

		public String toCsv() {
			return getType().replace(",", "\\,") + ","
					+ getID().replace(",", "\\,");
		}

		public Map<String, Object> toJson(TrackerAsset tracker) {
			String typeKey = getType();

			if (getObjectIDs().containsKey(getType())) {
				typeKey = getObjectIDs().get(getType());
			} else {
				String complaint = "Tracker-xAPI: Unknown definition for target type: " +
						getType();

				complain(complaint, complaint + " - ignored",
						TargetXApiException.class, null);
			}

			Map<String, Object> obj = new HashMap<>(), definition = new HashMap<>();

			// FIXME - strongly suspect this logic ->
			obj.put("id",
					((tracker.getActorObject() != null) ? tracker.getObjectId() : "")
							+ getID());
			definition.put("type", typeKey);
			obj.put("definition", definition);

			return obj;
		}

		public String toXml() {
			return getType() + "," + getID();
		}

		// TODO;
		public Map<String, Object> toXapi(TrackerAsset tracker) {
			return this.toJson(tracker);
		}

		public boolean isValid() throws Exception {
			return notNullEmptyOrNan(getType())
					&& notNullEmptyOrNan(getID());
		}

	}

	/**
	 * Class for Result storage.
	 */
	public static class TraceResult {

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

		public void setResponse(String value) {
			if (check(value,
					"xAPI extension: response Empty or null. Ignoring",
					"xAPI extension: response can't be empty or null",
					ValueExtensionException.class))
				res = value;

		}

		public float getScore() {
			return score;
		}

		public void setScore(float value) {
			if (check(value,
						"xAPI extension: score null or NaN. Ignoring",
						"xAPI extension: score can't be null or NaN.",
						ValueExtensionException.class))
				score = value;
		}

		Map<String, Object> extensions = new HashMap<>();

		public Map<String, Object> getExtensions() {
			return extensions;
		}

		public void setExtensions(Map<String, Object> value) {
			extensions = new HashMap<>();
			for (Map.Entry<String, Object> extension : value.entrySet()) {

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
					extensions.put(extension.getKey(), extension.getValue());
					break;
				}
			}
		}

		public String toCsv() {
			StringBuilder result = new StringBuilder(((success > -1) ? ",success"
					+ intToBoolString(success) : "")
					+ ((completion > -1) ? ",completion"
							+ intToBoolString(completion) : "")
					+ ((notNullEmptyOrNan(getResponse())) ? ",response,"
							+ getResponse().replace(",", "\\,") : "")
					+ ((notNullEmptyOrNan(score)) ? ",score,"
							+ Float.toString(score).replace(',', '.') : ""));

			if (getExtensions() != null) {

				for (Map.Entry<String, Object> extension : getExtensions().entrySet()) {
					result.append("," + extension.getKey().replace(",", "\\,") + ",");
					if (extension.getValue() != null) {
						if (extension.getValue() instanceof String) {
							result.append(extension.getValue().toString()
									.replace(",", "\\,"));
						} else if ((extension.getValue() instanceof Float)) {
							result.append(Float.toString(
									(Float) extension.getValue()).replace(
									',', '.'));
						} else if ((extension.getValue() instanceof Double)) {
							result.append(Double.toString(
									(Double) extension.getValue()).replace(
									',', '.'));
						} else if ((extension.getValue() instanceof Integer)) {
							result.append(Integer.toString(
									(Integer) extension.getValue())
									.replace(',', '.'));
						} else if (extension.getValue().getClass() == HashMap.class) {
							@SuppressWarnings("unchecked")
							Map<String, Object> map = (Map<String, Object>)extension.getValue();
							StringBuilder inner = new StringBuilder();
							for (Map.Entry<String, Object> e : map.entrySet()) {
								 inner.append(e.getKey())
										 .append("=")
										 .append(e.getValue().toString().toLowerCase())
										 .append("-");
							}

							result.append(inner.toString().replaceAll("[-]+$", ""));
						} else {
							result.append(String.valueOf(extension.getValue()));
						}
					}
				}
			}

			return result.toString();
		}

		public Map<String, Object> toJson() {
			Map<String, Object> result = new HashMap<>();
			if (success != -1)
				result.put("success", getSuccess());

			if (completion != -1)
				result.put("completion", getCompletion());

			if (notNullEmptyOrNan(getResponse()))
				result.put("response", getResponse());

			if (!Float.isNaN(score)) {
				Map<String, Object> s = new HashMap<>();
				s.put("raw", score);
				result.put("score", s);
			}

			Map<String, Object> extensions = new HashMap<>();
			for (Map.Entry<String, Object> extension : getExtensions().entrySet()) {
				String xApiKey = extensionIds.get(extension.getKey());
				extensions.put(xApiKey != null ?
							xApiKey : extension.getKey(),
							extension.getValue());
			}
			if (!extensions.isEmpty()) {
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

			// FIXME --> these are mostly redundant
			if (notNullEmptyOrNan(getResponse())) {
				setResponse(getResponse());
			}

			if (notNullEmptyOrNan(getScore())) {
				setScore(getScore());
			}

			// FIXME --> these two do nothing useful
			if (success != -1)
				valid &= notNullEmptyOrNan(success);

			if (completion != -1)
				valid &= notNullEmptyOrNan(completion);

			valid &= notNullEmptyOrNan(getResponse());

			valid &= notNullEmptyOrNan(score);

			if (getExtensions() != null) {
				for (Map.Entry<String, Object> extension : getExtensions().entrySet()) {
					valid &= quickCheckExtension(
							extension.getKey(), extension.getValue());
				}
			}

			return valid;
		}

	}

}
