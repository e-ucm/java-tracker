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

	/**
	 * Values that represent the available verbs for traces.
	 */
	public enum Verb implements TrackerUtils.XApiConstant {
		Initialized("http://adlnet.gov/expapi/verbs/initialized"),
		Progressed("http://adlnet.gov/expapi/verbs/progressed"),
		Completed("http://adlnet.gov/expapi/verbs/completed"),
		Accessed("https://w3id.org/xapi/seriousgames/verbs/accessed"),
		Skipped("http://id.tincanapi.com/verb/skipped"),
		Selected("https://w3id.org/xapi/adb/verbs/selected"),
		Unlocked("https://w3id.org/xapi/seriousgames/verbs/unlocked"),
		Interacted("http://adlnet.gov/expapi/verbs/interacted"),
		Used("https://w3id.org/xapi/seriousgames/verbs/used");
		private String id;
		Verb(String id) {
			this.id = id;
		}
		@Override
		public String getId() {
			return id;
		}
	}

	private static Map<String, String> xApiVerbs =
			TrackerUtils.buildXApiMap(Verb.class);

	private String stringVerb = "";
	private Verb xApiVerb = Verb.Initialized;

	public String getStringVerb() {
		return stringVerb;
	}

	public void setStringVerb(String value) throws TrackerException {
		stringVerb = value;
		xApiVerb = parseEnumOrComplain(value, Verb.class,
				"Tracker-xAPI: Unknown definition for verb: " + value,
				"Tracker-xAPI: Unknown definition for verb: " + value, VerbXApiException.class);
		if (xApiVerb != null) {
			stringVerb = value.toLowerCase();
		}
	}

	public Verb getVerb() {
		return xApiVerb;
	}

	public void setVerb(Verb value) {
		stringVerb = value.toString().toLowerCase();
		xApiVerb = value;
	}

	public TraceVerb(Verb verb) {
		setVerb(verb);
	}

	public TraceVerb(String verb) {
		setStringVerb(verb);
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

	public boolean isValid() {
		boolean check = true;
		setStringVerb(getStringVerb());

		return check && notNullEmptyOrNan(stringVerb);
	}
}
