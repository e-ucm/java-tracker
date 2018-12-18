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

public class CompletableTracker {
	private TraceProcessor tracker;

	public CompletableTracker(TraceProcessor tracker) {
		this.tracker = tracker;
	}

	public enum Completable implements TrackerUtils.XApiConstant {
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
	public void initialized(String completableId) {
		if (TrackerUtils.check(completableId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TraceVerb.Verb.Initialized));
			trace.setTarget(new TrackerEvent.TraceObject(
					Completable.Completable.toString().toLowerCase(),
					completableId));

			tracker.process(trace);
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
	public void initialized(String completableId, Completable type) {
		if (TrackerUtils.check(completableId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TraceVerb.Verb.Initialized));
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), completableId));

			tracker.process(trace);
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
	public void progressed(String completableId, float value) {
		if (TrackerUtils.check(completableId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TraceVerb.Verb.Progressed));
			trace.setTarget(new TrackerEvent.TraceObject(
					Completable.Completable.toString().toLowerCase(),
					completableId));

			tracker.setProgress(value);
			tracker.process(trace);
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
	public void progressed(String completableId, Completable type, float value) {
		if (TrackerUtils.check(completableId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TraceVerb.Verb.Progressed));
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), completableId));

			tracker.setProgress(value);
			tracker.process(trace);
		}
	}

	/**
	 * Player completed a completable. Type = Completable Success = true Score =
	 * 1
	 * 
	 * @param completableId
	 *            Completable identifier.
	 */
	public void completed(String completableId) {
		if (TrackerUtils.check(completableId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TraceVerb.Verb.Completed));
			trace.setTarget(new TrackerEvent.TraceObject(
					Completable.Completable.toString().toLowerCase(),
					completableId));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setSuccess(true);
			result.setScore(1f);
			trace.setResult(result);

			tracker.process(trace);
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
	public void completed(String completableId, Completable type) {
		if (TrackerUtils.check(completableId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TraceVerb.Verb.Completed));
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), completableId));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setSuccess(true);
			result.setScore(1f);
			trace.setResult(result);

			tracker.process(trace);
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
			boolean success) {
		if (TrackerUtils.check(completableId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TraceVerb.Verb.Completed));
			trace.setTarget(new TrackerEvent.TraceObject(
					Completable.Completable.toString().toLowerCase(),
					completableId));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setSuccess(success);
			result.setScore(1f);
			trace.setResult(result);

			tracker.process(trace);
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
	public void completed(String completableId, Completable type, float score) {
		if (TrackerUtils.check(completableId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TraceVerb.Verb.Completed));
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), completableId));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setSuccess(true);
			result.setScore(score);
			trace.setResult(result);

			tracker.process(trace);
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
			boolean success, float score) {
		if (TrackerUtils.check(completableId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class)) {
			TrackerEvent trace = new TrackerEvent();

			trace.setEvent(new TraceVerb(
					TraceVerb.Verb.Completed));
			trace.setTarget(new TrackerEvent.TraceObject(type
					.toString().toLowerCase(), completableId));

			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setSuccess(success);
			result.setScore(score);
			trace.setResult(result);

			tracker.process(trace);
		}
	}
}
