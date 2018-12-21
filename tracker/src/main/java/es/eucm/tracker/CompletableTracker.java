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

public class CompletableTracker extends BaseTracker {
	private TraceProcessor tracker;

	public CompletableTracker(TraceProcessor tracker) {
		this.tracker = tracker;
	}

	public enum Completable implements TrackerUtils.XApiConstant {
		Game("serious-game"),
		Session("session"),
		Level("level"),
		Quest("quest"),
		Stage("stage"),
		Combat("combat"),
		StoryNode("story-node"),
		Race("race"),
		Completable("completable");

		private String id;
		
		Completable(String id) {
			this.id = id;
		}
		
		@Override
		public String getId() {
			return ACTIVITY_TYPES_BASE_IRI+id;
		}

		@Override
		public String getSimpleName() {
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
		tracker.process(generateInitializedTrace(new TraceVerb(
				TraceVerb.Verb.Initialized), Completable.Completable, completableId));
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
		tracker.process(generateInitializedTrace(new TraceVerb(
				TraceVerb.Verb.Initialized), Completable.Completable, completableId));
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
		tracker.setProgress(value);
		tracker.process(generateProgressTrace(new TraceVerb(
				TraceVerb.Verb.Progressed), Completable.Completable, completableId));
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
		tracker.setProgress(value);
		tracker.process(generateProgressTrace(new TraceVerb(
				TraceVerb.Verb.Progressed), type, completableId));
	}


	/**
	 * Player completed a completable. Type = Completable Success = true Score =
	 * 1
	 * 
	 * @param completableId
	 *            Completable identifier.
	 */
	public void completed(String completableId) {
		tracker.process(generateSuccessTrace(new TraceVerb(
				TraceVerb.Verb.Completed), Completable.Completable, completableId, true, 1.0f));
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
		tracker.process(generateSuccessTrace(new TraceVerb(
				TraceVerb.Verb.Completed), type, completableId, true, 1.0f));
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
		tracker.process(generateSuccessTrace(new TraceVerb(
				TraceVerb.Verb.Completed), type, completableId, success, 1.0f));
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
		tracker.process(generateSuccessTrace(new TraceVerb(
				TraceVerb.Verb.Completed), type, completableId, true, score));
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
		tracker.process(generateSuccessTrace(new TraceVerb(
					TraceVerb.Verb.Completed), type, completableId, success, score));
	}
}
