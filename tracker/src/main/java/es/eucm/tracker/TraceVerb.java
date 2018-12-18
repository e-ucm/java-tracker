package es.eucm.tracker;

import es.eucm.tracker.exceptions.TrackerException;
import es.eucm.tracker.exceptions.VerbXApiException;

import java.util.HashMap;
import java.util.Map;

import static es.eucm.tracker.TrackerUtils.notNullEmptyOrNan;
import static es.eucm.tracker.TrackerUtils.parseEnumOrComplain;

/**
 * Class for Verb storage.
 */
public class TraceVerb {
	/** the parent event, for which this is the verb */
	private TrackerEvent parent;
	private static Map<String, String> xApiVerbs =
			TrackerUtils.buildXApiMap(TrackerAsset.Verb.class);

	public TrackerEvent getParent() {
		return parent;
	}

	public void setParent(TrackerEvent value) {
		parent = value;
	}

	private String stringVerb = "";
	private TrackerAsset.Verb xApiVerb = TrackerAsset.Verb.Initialized;

	public String getStringVerb() {
		return stringVerb;
	}

	public void setStringVerb(String value, TrackerAsset tracker) throws TrackerException {
		stringVerb = value;
		xApiVerb = parseEnumOrComplain(value, TrackerAsset.Verb.class, tracker,
				"Tracker-xAPI: Unknown definition for verb: " + value,
				"Tracker-xAPI: Unknown definition for verb: " + value, VerbXApiException.class);
		if (xApiVerb != null) {
			stringVerb = value.toLowerCase();
		}
	}

	public TrackerAsset.Verb getVerb() {
		return xApiVerb;
	}

	public void setVerb(TrackerAsset.Verb value) {
		stringVerb = value.toString().toLowerCase();
		xApiVerb = value;
	}

	public TraceVerb(TrackerAsset.Verb verb) {
		setVerb(verb);
	}

	public TraceVerb(String verb, TrackerAsset tracker) throws Exception {
		setStringVerb(verb, tracker);
	}

	public String toCsv() {
		return getStringVerb().replace(",", "\\,");
	}

	public Map<String, Object> toJson() {
		String originalId = getStringVerb();
		String xApiVerbId = xApiVerbs.get(originalId);

		Map<String, Object> verb = new HashMap<>();
		verb.put("id", xApiVerbId != null ? xApiVerbId : originalId);

		return verb;
	}

	public String toXml() {
		return "";
	}

	public Map<String, Object> toXapi() {
		return this.toJson();
	}

	public boolean isValid(TrackerAsset tracker) throws Exception {
		boolean check = true;
		if (getParent() != null) {
			setStringVerb(getStringVerb(), tracker);
		}

		return check && notNullEmptyOrNan(stringVerb);
	}

}
