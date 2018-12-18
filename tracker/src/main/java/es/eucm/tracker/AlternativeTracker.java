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

import es.eucm.tracker.exceptions.TargetXApiException;
import es.eucm.tracker.exceptions.ValueExtensionException;

public class AlternativeTracker implements TrackerAsset.IGameObjectTracker {
	private TrackerAsset tracker;

	public void setTracker(TrackerAsset tracker) {
		this.tracker = tracker;
	}

	public enum Alternative implements TrackerAsset.XApiConstant {
		Question("http://adlnet.gov/expapi/activities/question"),
		Menu("https://w3id.org/xapi/seriousgames/activity-types/menu"),
		Dialog("https://w3id.org/xapi/seriousgames/activity-types/dialog-tree"),
		Path("https://w3id.org/xapi/seriousgames/activity-types/path"),
		Arena("https://w3id.org/xapi/seriousgames/activity-types/arena"),
		Alternative("https://w3id.org/xapi/seriousgames/activity-types/alternative");
		private String id;
		Alternative(String id) {
			this.id = id;
		}
		@Override
		public String getId() {
			return id;
		}
	}

	/**
	 * Player selected an option in a presented alternative Type = Alternative
	 * 
	 * @param alternativeId
	 *            Alternative identifier.
	 * @param optionId
	 *            Option identifier.
	 */
	public void selected(String alternativeId, String optionId)
			throws Exception {
		boolean check = true;
		check &= TrackerUtils.check(alternativeId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class);
		check &= TrackerUtils.check(optionId, tracker,
				"xAPI Exception: Selected alternative is null or empty",
				"xAPI Exception: Selected alternative can't be null or empty",
				ValueExtensionException.class);
		if (check) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Selected), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(
					Alternative.Alternative.toString().toLowerCase(),
					alternativeId, tracker));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setResponse(optionId, tracker);
			trace.setResult(result);

			tracker.trace(trace);
		}

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
	public void selected(String alternativeId, String optionId, Alternative type)
			throws Exception {
		boolean check = true;
		check &= TrackerUtils.check(alternativeId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class);
		check &= TrackerUtils.check(optionId, tracker,
				"xAPI Exception: Selected alternative is null or empty",
				"xAPI Exception: Selected alternative can't be null or empty",
				ValueExtensionException.class);
		if (check) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Selected), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), alternativeId, tracker));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setResponse(optionId, tracker);
			trace.setResult(result);

			tracker.trace(trace);
		}

	}

	/**
	 * Player unlocked an option Type = Alternative
	 * 
	 * @param alternativeId
	 *            Alternative identifier.
	 * @param optionId
	 *            Option identifier.
	 */
	public void unlocked(String alternativeId, String optionId)
			throws Exception {
		boolean check = true;
		check &= TrackerUtils.check(alternativeId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class);
		check &= TrackerUtils.check(optionId, tracker,
				"xAPI Exception: Selected alternative is null or empty",
				"xAPI Exception: Selected alternative can't be null or empty",
				ValueExtensionException.class);
		if (check) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Unlocked), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(
					Alternative.Alternative.toString().toLowerCase(),
					alternativeId, tracker));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setResponse(optionId, tracker);
			trace.setResult(result);

			tracker.trace(trace);
		}

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
	public void unlocked(String alternativeId, String optionId, Alternative type)
			throws Exception {
		boolean check = true;
		check &= TrackerUtils.check(alternativeId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class);
		check &= TrackerUtils.check(optionId, tracker,
				"xAPI Exception: Selected alternative is null or empty",
				"xAPI Exception: Selected alternative can't be null or empty",
				ValueExtensionException.class);
		if (check) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Unlocked), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), alternativeId, tracker));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setResponse(optionId, tracker);
			trace.setResult(result);

			tracker.trace(trace);
		}
	}

}
