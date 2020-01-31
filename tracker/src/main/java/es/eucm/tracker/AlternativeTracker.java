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

public class AlternativeTracker extends BaseTracker {
	private TraceProcessor tracker;

	public enum Alternative implements TrackerUtils.XApiConstant {
		Question("http://adlnet.gov/expapi/activities/", "question"), Menu(
				ACTIVITY_TYPES_BASE_IRI,
				"menu"), Dialog(ACTIVITY_TYPES_BASE_IRI, "dialog-tree"), Path(
						ACTIVITY_TYPES_BASE_IRI, "path"), Arena(
								ACTIVITY_TYPES_BASE_IRI, "arena"), Alternative(
										ACTIVITY_TYPES_BASE_IRI, "alternative");

		private String baseIri;

		private String id;

		Alternative(String baseIri, String id) {
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

	public AlternativeTracker(TraceProcessor tracker) {
		this.tracker = tracker;
	}

	/**
	 * Player selected an option in a presented alternative Type = Alternative
	 * 
	 * @param alternativeId
	 *            Alternative identifier.
	 * @param optionId
	 *            Option identifier.
	 */
	public void selected(String alternativeId, String optionId) {
		tracker.process(generateTrace(new TraceVerb(TraceVerb.Verb.Selected),
				Alternative.Alternative, optionId, alternativeId));
	}

	/**
	 * Player selected an option in a presented alternative
	 * 
	 * @param alternativeId
	 *            Alternative identifier.
	 * @param optionId
	 *            Option identifier.
	 * @param type
	 *            Alternative type.
	 */
	public void selected(String alternativeId, String optionId,
			Alternative type) {
		tracker.process(generateTrace(new TraceVerb(TraceVerb.Verb.Selected),
				type, optionId, alternativeId));
	}

	/**
	 * Player unlocked an option Type = Alternative
	 * 
	 * @param alternativeId
	 *            Alternative identifier.
	 * @param optionId
	 *            Option identifier.
	 */
	public void unlocked(String alternativeId, String optionId) {
		tracker.process(generateTrace(new TraceVerb(TraceVerb.Verb.Unlocked),
				Alternative.Alternative, optionId, alternativeId));
	}

	/**
	 * Player unlocked an option
	 * 
	 * @param alternativeId
	 *            Alternative identifier.
	 * @param optionId
	 *            Option identifier.
	 * @param type
	 *            Alternative type.
	 */
	public void unlocked(String alternativeId, String optionId,
			Alternative type) {
		tracker.process(generateTrace(new TraceVerb(TraceVerb.Verb.Unlocked),
				type, optionId, alternativeId));
	}
}
