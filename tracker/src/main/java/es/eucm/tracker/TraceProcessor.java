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

/**
 * An object that can process traces
 */
interface TraceProcessor {
	/**
	 * Adds a trace to the queue.
	 * 
	 * @param trace
	 *            to add
	 */
	void process(TrackerEvent trace);

	/**
	 * Allows progress to be set
	 */
	void setProgress(float progress);
}
