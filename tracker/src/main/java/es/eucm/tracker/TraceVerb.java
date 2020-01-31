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

import es.eucm.tracker.exceptions.TrackerException;
import es.eucm.tracker.exceptions.VerbXApiException;

import static es.eucm.tracker.TrackerUtils.notNullEmptyOrNan;
import static es.eucm.tracker.TrackerUtils.parseEnumOrComplain;

/**
 * Class for Verb storage.
 */
public class TraceVerb implements Comparable<TraceVerb> {

	/**
	 * Values that represent the available verbs for traces.
	 */
	public enum Verb implements TrackerUtils.XApiConstant {
		Initialized("http://adlnet.gov/expapi/verbs/",
				"initialized"), Progressed("http://adlnet.gov/expapi/verbs/",
						"progressed"), Completed(
								"http://adlnet.gov/expapi/verbs/",
								"completed"), Accessed(VERBS_BASE_IRI,
										"accessed"), Skipped(
												"http://id.tincanapi.com/verb/",
												"skipped"), Selected(
														"https://w3id.org/xapi/adb/verbs/",
														"selected"), Unlocked(
																VERBS_BASE_IRI,
																"unlocked"), Interacted(
																		"http://adlnet.gov/expapi/verbs/",
																		"interacted"), Used(
																				VERBS_BASE_IRI,
																				"used");

		private String baseIri;

		private String id;

		Verb(String baseIri, String id) {
			this.baseIri = baseIri;
			this.id = id;
		}

		@Override
		public String getId() {
			return baseIri + id;
		}

		@Override
		public String getSimpleName() {
			return id;
		}
	}

	private String stringVerb = "";
	private Verb xApiVerb = Verb.Initialized;

	public String getStringVerb() {
		return stringVerb;
	}

	public void setStringVerb(String value) throws TrackerException {
		stringVerb = value;
		xApiVerb = parseEnumOrComplain(value, Verb.class,
				"Tracker-xAPI: Unknown definition for verb: " + value,
				"Tracker-xAPI: Unknown definition for verb: " + value,
				VerbXApiException.class);
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

	public boolean isValid() {
		boolean check = true;
		setStringVerb(getStringVerb());

		return check && notNullEmptyOrNan(stringVerb);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((xApiVerb == null) ? 0 : xApiVerb.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TraceVerb other = (TraceVerb) obj;
		if (xApiVerb != other.xApiVerb)
			return false;
		return true;
	}

	@Override
	public int compareTo(TraceVerb o) {
		return xApiVerb.compareTo(o.xApiVerb);
	}
}
