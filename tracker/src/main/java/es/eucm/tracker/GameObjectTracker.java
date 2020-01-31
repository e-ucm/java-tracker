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

public class GameObjectTracker {

	private TraceProcessor tracker;

	public GameObjectTracker(TraceProcessor tracker) {
		this.tracker = tracker;
	}

	public enum TrackedGameObject implements TrackerUtils.XApiConstant {
		Enemy("enemy"), Npc("non-player-character"), Item("item"), GameObject(
				"game-object");

		private String id;

		TrackedGameObject(String id) {
			this.id = id;
		}

		@Override
		public String getId() {
			return ACTIVITY_TYPES_BASE_IRI + id;
		}

		@Override
		public String getSimpleName() {
			return id;
		}
	}

	/**
	 * Player interacted with a game object. Type = GameObject
	 * 
	 * @param gameobjectId
	 *            Reachable identifier.
	 */
	public void interacted(String gameobjectId) {
		if (TrackerUtils.check(gameobjectId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(TraceVerb.Verb.Interacted));
			trace.setTarget(new TrackerEvent.TraceObject(
					TrackedGameObject.GameObject.toString().toLowerCase(),
					gameobjectId));

			tracker.process(trace);
		}
	}

	/**
	 * Player interacted with a game object.
	 * 
	 * @param gameobjectId
	 *            TrackedGameObject identifier.
	 * @param type
	 *            type of event.
	 */
	public void interacted(String gameobjectId, TrackedGameObject type) {
		if (TrackerUtils.check(gameobjectId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(TraceVerb.Verb.Interacted));
			trace.setTarget(new TrackerEvent.TraceObject(
					type.toString().toLowerCase(), gameobjectId));

			tracker.process(trace);
		}
	}

	/**
	 * Player interacted with a game object. Type = GameObject
	 * 
	 * @param gameobjectId
	 *            Reachable identifier.
	 */
	public void used(String gameobjectId) {
		if (TrackerUtils.check(gameobjectId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(TraceVerb.Verb.Interacted));
			trace.setTarget(new TrackerEvent.TraceObject(
					TrackedGameObject.GameObject.toString().toLowerCase(),
					gameobjectId));

			tracker.process(trace);
		}
	}

	/**
	 * Player interacted with a game object.
	 * 
	 * @param gameobjectId
	 *            TrackedGameObject identifier.
	 * @param type
	 *            type of event.
	 */
	public void used(String gameobjectId, TrackedGameObject type) {
		if (TrackerUtils.check(gameobjectId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(TraceVerb.Verb.Used));
			trace.setTarget(new TrackerEvent.TraceObject(
					type.toString().toLowerCase(), gameobjectId));

			tracker.process(trace);
		}
	}

}
