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

import java.util.HashMap;

public class AccessibleTracker implements TrackerAsset.IGameObjectTracker {
	private TrackerAsset tracker;

	public void setTracker(TrackerAsset tracker) {
		this.tracker = tracker;
	}

	public enum Accessible implements TrackerAsset.XApiConstant {
		Screen("https://w3id.org/xapi/seriousgames/activity-types/screen"),
		Area("https://w3id.org/xapi/seriousgames/activity-types/area"),
		Zone("https://w3id.org/xapi/seriousgames/activity-types/zone"),
		Cutscene("https://w3id.org/xapi/seriousgames/activity-types/cutscene"),
		Accessible("https://w3id.org/xapi/seriousgames/activity-types/accessible");
		private String id;
		Accessible(String id) {
			this.id = id;
		}
		@Override
		public String getId() {
			return id;
		}
	}

	/**
	 * Player accessed a reachable. Type = Accessible
	 * 
	 * @param reachableId
	 *            Reachable identifier.
	 */
	public void accessed(String reachableId) throws Exception {
		if (TrackerUtils.check(reachableId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Accessed), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(
					Accessible.Accessible.toString().toLowerCase(), reachableId, tracker));

			tracker.trace(trace);
		}
	}

	/**
	 * Player accessed a reachable.
	 * 
	 * @param reachableId
	 *            Reachable identifier.
	 * @param type
	 *            Reachable type.
	 */
	public void accessed(String reachableId, Accessible type) throws Exception {
		if (TrackerUtils.check(reachableId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Accessed), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), reachableId, tracker));

			tracker.trace(trace);
		}
	}

	/**
	 * Player skipped a reachable. Type = Accessible
	 * 
	 * @param reachableId
	 *            Reachable identifier.
	 */
	public void skipped(String reachableId) throws Exception {
		if (TrackerUtils.check(reachableId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Skipped), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(
					Accessible.Accessible.toString().toLowerCase(), reachableId, tracker));

			tracker.trace(trace);
		}
	}

	/**
	 * Player skipped a reachable.
	 * 
	 * @param reachableId
	 *            Reachable identifier.
	 * @param type
	 *            Reachable type.
	 */
	public void skipped(String reachableId, Accessible type) throws Exception {
		if (TrackerUtils.check(reachableId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Skipped), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), reachableId, tracker));

			tracker.trace(trace);
		}
	}

}
