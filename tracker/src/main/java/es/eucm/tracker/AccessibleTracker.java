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

import es.eucm.tracker.Exceptions.TargetXApiException;

public class AccessibleTracker implements TrackerAsset.IGameObjectTracker {
	private TrackerAsset tracker;

	public void setTracker(TrackerAsset tracker) {
		this.tracker = tracker;
	}

	public enum Accessible {
		/* ACCESSIBLES */
		Screen, Area, Zone, Cutscene, Accessible
	}

	/**
	 * Player accessed a reachable. Type = Accessible
	 * 
	 * @param reachableId
	 *            Reachable identifier.
	 */
	public void accessed(String reachableId) throws Exception {
		if (tracker.getUtils().check(reachableId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(
					tracker);

			trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(
					TrackerAsset.Verb.Accessed));
			trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(
					Accessible.Accessible.toString().toLowerCase(), reachableId));

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
		if (tracker.getUtils().check(reachableId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(
					tracker);

			trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(
					TrackerAsset.Verb.Accessed));
			trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(type
					.toString().toLowerCase(), reachableId));

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
		if (tracker.getUtils().check(reachableId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(
					tracker);

			trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(
					TrackerAsset.Verb.Skipped));
			trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(
					Accessible.Accessible.toString().toLowerCase(), reachableId));

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
		if (tracker.getUtils().check(reachableId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(
					tracker);

			trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(
					TrackerAsset.Verb.Skipped));
			trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(type
					.toString().toLowerCase(), reachableId));

			tracker.trace(trace);
		}
	}

}
