/**
 * Copyright © 2019-20 e-UCM (http://www.e-ucm.es/)
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

public class AccessibleTracker extends BaseTracker {
	private TraceProcessor tracker;

	public AccessibleTracker(TraceProcessor tracker) {
		this.tracker = tracker;
	}

	public enum Accessible implements TrackerUtils.XApiConstant {

		Screen("screen"), Area("area"), Zone("zone"), Cutscene(
				"cutscene"), Accessible("accessible");

		private String id;

		Accessible(String id) {
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
	 * Player accessed a reachable. Type = Accessible
	 * 
	 * @param reachableId
	 *            Reachable identifier.
	 */
	public void accessed(String reachableId) {
		tracker.process(generateTrace(new TraceVerb(TraceVerb.Verb.Accessed),
				Accessible.Accessible, reachableId));
	}

	/**
	 * Player accessed a reachable.
	 * 
	 * @param reachableId
	 *            Reachable identifier.
	 * @param type
	 *            Reachable type.
	 */
	public void accessed(String reachableId, Accessible type) {
		tracker.process(generateTrace(new TraceVerb(TraceVerb.Verb.Accessed),
				type, reachableId));
	}

	/**
	 * Player skipped a reachable. Type = Accessible
	 * 
	 * @param reachableId
	 *            Reachable identifier.
	 */
	public void skipped(String reachableId) {
		tracker.process(generateTrace(new TraceVerb(TraceVerb.Verb.Skipped),
				Accessible.Accessible, reachableId));
	}

	/**
	 * Player skipped a reachable.
	 * 
	 * @param reachableId
	 *            Reachable identifier.
	 * @param type
	 *            Reachable type.
	 */
	public void skipped(String reachableId, Accessible type) {
		tracker.process(generateTrace(new TraceVerb(TraceVerb.Verb.Skipped),
				type, reachableId));
	}

}
