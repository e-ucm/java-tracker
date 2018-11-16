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
import es.eucm.tracker.Exceptions.ValueExtensionException;

public class AlternativeTracker implements TrackerAsset.IGameObjectTracker {
	private TrackerAsset tracker;

	public void setTracker(TrackerAsset tracker) {
		this.tracker = tracker;
	}

	public enum Alternative {
		/* ALTERNATIVES */
		Question, Menu, Dialog, Path, Arena, Alternative
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
		check &= tracker.getUtils().check(alternativeId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class);
		check &= tracker.getUtils().check(optionId,
				"xAPI Exception: Selected alternative is null or empty",
				"xAPI Exception: Selected alternative can't be null or empty",
				ValueExtensionException.class);
		if (check) {
			TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(
					tracker);

			trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(
					TrackerAsset.Verb.Selected));
			trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(
					Alternative.Alternative.toString().toLowerCase(),
					alternativeId));

			TrackerAsset.TrackerEvent.TraceResult result = new TrackerAsset.TrackerEvent.TraceResult();
			result.setResponse(optionId);
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
		check &= tracker.getUtils().check(alternativeId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class);
		check &= tracker.getUtils().check(optionId,
				"xAPI Exception: Selected alternative is null or empty",
				"xAPI Exception: Selected alternative can't be null or empty",
				ValueExtensionException.class);
		if (check) {
			TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(
					tracker);

			trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(
					TrackerAsset.Verb.Selected));
			trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(type
					.toString().toLowerCase(), alternativeId));

			TrackerAsset.TrackerEvent.TraceResult result = new TrackerAsset.TrackerEvent.TraceResult();
			result.setResponse(optionId);
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
		check &= tracker.getUtils().check(alternativeId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class);
		check &= tracker.getUtils().check(optionId,
				"xAPI Exception: Selected alternative is null or empty",
				"xAPI Exception: Selected alternative can't be null or empty",
				ValueExtensionException.class);
		if (check) {
			TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(
					tracker);

			trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(
					TrackerAsset.Verb.Unlocked));
			trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(
					Alternative.Alternative.toString().toLowerCase(),
					alternativeId));

			TrackerAsset.TrackerEvent.TraceResult result = new TrackerAsset.TrackerEvent.TraceResult();
			result.setResponse(optionId);
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
		check &= tracker.getUtils().check(alternativeId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class);
		check &= tracker.getUtils().check(optionId,
				"xAPI Exception: Selected alternative is null or empty",
				"xAPI Exception: Selected alternative can't be null or empty",
				ValueExtensionException.class);
		if (check) {
			TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(
					tracker);

			trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(
					TrackerAsset.Verb.Unlocked));
			trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(type
					.toString().toLowerCase(), alternativeId));

			TrackerAsset.TrackerEvent.TraceResult result = new TrackerAsset.TrackerEvent.TraceResult();
			result.setResponse(optionId);
			trace.setResult(result);

			tracker.trace(trace);
		}
	}

}
