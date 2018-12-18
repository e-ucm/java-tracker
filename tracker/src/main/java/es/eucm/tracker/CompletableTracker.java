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

public class CompletableTracker implements TrackerAsset.IGameObjectTracker {
	private TrackerAsset tracker;

	public void setTracker(TrackerAsset tracker) {
		this.tracker = tracker;
	}

	public enum Completable implements TrackerAsset.XApiConstant {
		Game("https://w3id.org/xapi/seriousgames/activity-types/serious-game"),
		Session("https://w3id.org/xapi/seriousgames/activity-types/session"),
		Level("https://w3id.org/xapi/seriousgames/activity-types/level"),
		Quest("https://w3id.org/xapi/seriousgames/activity-types/quest"),
		Stage("https://w3id.org/xapi/seriousgames/activity-types/stage"),
		Combat("https://w3id.org/xapi/seriousgames/activity-types/combat"),
		StoryNode("https://w3id.org/xapi/seriousgames/activity-types/story-node"),
		Race("https://w3id.org/xapi/seriousgames/activity-types/race"),
		Completable("https://w3id.org/xapi/seriousgames/activity-types/completable");
		private String id;
		Completable(String id) {
			this.id = id;
		}
		@Override
		public String getId() {
			return id;
		}
	}

	/**
	 * Player initialized a completable.
	 * 
	 * @param completableId
	 *            Completable identifier.
	 */
	public void initialized(String completableId) throws Exception {
		if (TrackerUtils.check(completableId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Initialized), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(
					Completable.Completable.toString().toLowerCase(),
					completableId, tracker));

			tracker.trace(trace);
		}
	}

	/**
	 * Player initialized a completable.
	 * 
	 * @param completableId
	 *            Completable identifier.
	 * @param type
	 *            Completable type.
	 */
	public void initialized(String completableId, Completable type)
			throws Exception {
		if (TrackerUtils.check(completableId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Initialized), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), completableId, tracker));

			tracker.trace(trace);
		}
	}

	/**
	 * Player progressed a completable. Type = Completable
	 * 
	 * @param completableId
	 *            Completable identifier.
	 * @param value
	 *            New value for the completable's progress.
	 */
	public void progressed(String completableId, float value) throws Exception {
		if (TrackerUtils.check(completableId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Progressed), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(
					Completable.Completable.toString().toLowerCase(),
					completableId, tracker));

			tracker.setProgress(value);
			tracker.trace(trace);
		}
	}

	/**
	 * Player progressed a completable.
	 * 
	 * @param completableId
	 *            Completable identifier.
	 * @param value
	 *            New value for the completable's progress.
	 * @param type
	 *            Completable type.
	 */
	public void progressed(String completableId, Completable type, float value)
			throws Exception {
		if (TrackerUtils.check(completableId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Progressed), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), completableId, tracker));

			tracker.setProgress(value);
			tracker.trace(trace);
		}
	}

	/**
	 * Player completed a completable. Type = Completable Success = true Score =
	 * 1
	 * 
	 * @param completableId
	 *            Completable identifier.
	 */
	public void completed(String completableId) throws Exception {
		if (TrackerUtils.check(completableId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Completed), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(
					Completable.Completable.toString().toLowerCase(),
					completableId, tracker));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setSuccess(true);
			result.setScore(1f, tracker);
			trace.setResult(result);

			tracker.trace(trace);
		}
	}

	/**
	 * Player completed a completable. Success = true Score = 1
	 * 
	 * @param completableId
	 *            Completable identifier.
	 * @param type
	 *            Completable type.
	 */
	public void completed(String completableId, Completable type)
			throws Exception {
		if (TrackerUtils.check(completableId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Completed), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), completableId, tracker));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setSuccess(true);
			result.setScore(1f, tracker);
			trace.setResult(result);

			tracker.trace(trace);
		}
	}

	/**
	 * Player completed a completable. Score = 1
	 * 
	 * @param completableId
	 *            Completable identifier.
	 * @param type
	 *            Completable type.
	 * @param success
	 *            Completable success.
	 */
	public void completed(String completableId, Completable type,
			boolean success) throws Exception {
		if (TrackerUtils.check(completableId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Completed), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(
					Completable.Completable.toString().toLowerCase(),
					completableId, tracker));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setSuccess(success);
			result.setScore(1f, tracker);
			trace.setResult(result);

			tracker.trace(trace);
		}
	}

	/**
	 * Player completed a completable.
	 * 
	 * @param completableId
	 *            Completable identifier.
	 * @param type
	 *            Completable type.
	 * @param score
	 *            Completable score.
	 */
	public void completed(String completableId, Completable type, float score)
			throws Exception {
		if (TrackerUtils.check(completableId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Completed), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), completableId, tracker));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setSuccess(true);
			result.setScore(score, tracker);
			trace.setResult(result);

			tracker.trace(trace);
		}
	}

	/**
	 * Player completed a completable.
	 * 
	 * @param completableId
	 *            Completable identifier.
	 * @param type
	 *            Completable type.
	 * @param success
	 *            Completable success.
	 * @param score
	 *            Completable score.
	 */
	public void completed(String completableId, Completable type,
			boolean success, float score) throws Exception {
		if (TrackerUtils.check(completableId, tracker,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TrackerAsset.Verb.Completed), tracker);
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), completableId, tracker));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setSuccess(success);
			result.setScore(score, tracker);
			trace.setResult(result);

			tracker.trace(trace);
		}
	}

}
