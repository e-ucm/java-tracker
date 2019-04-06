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

import static es.eucm.tracker.TrackerUtils.check;
import static es.eucm.tracker.TrackerUtils.complain;
import static es.eucm.tracker.TrackerUtils.notNullEmptyOrNan;
import static es.eucm.tracker.TrackerUtils.quickCheckExtension;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import es.eucm.tracker.TrackerUtils.XApiConstant;
import es.eucm.tracker.exceptions.TargetXApiException;
import es.eucm.tracker.exceptions.ValueExtensionException;

/**
 * A tracker event.
 */
public class TrackerEvent {

	private TraceVerb verb;
	private TraceObject target;
	private TraceResult result;

	public TrackerEvent() {
		this(Instant.now());
	}
	
	public TrackerEvent(Instant timestamp) {
		this.timeStamp = timestamp;
		this.result = new TraceResult();
	}

	/**
	 * Gets or sets the event.
	 *
	 * The event.
	 */
	public TraceVerb getEvent() {
		return verb;
	}

	public void setEvent(TraceVerb value) {
		this.verb = value;
		this.verb.isValid();
	}

	/**
	 * Gets or sets the Target for the.
	 *
	 * The target.
	 */
	public TraceObject getTarget() {
		return target;
	}

	public void setTarget(TraceObject value) {
		this.target = value;
	}

	/**
	 * Gets or sets the Result for the.
	 *
	 * The Result.
	 */
	public TraceResult getResult() {
		return result;
	}

	public void setResult(TraceResult value) {
		this.result = value;
	}

	/**
	 * Gets the Date/Time of the time stamp.
	 *
	 * The time stamp.
	 */
	private Instant timeStamp;

	public Instant getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Class for Target storage.
	 */
	public static class TraceObject {
		private String type;
		private String id;

		public String getType() {
			return type;
		}

		public void setType(String value) {
			if (check(value,
				"xAPI Exception: Target Type is null or empty. Ignoring.",
				"xAPI Exception: Target Type can't be null or empty.",
				TargetXApiException.class))
				type = value;
		}

		public String getID() {
			return id;
		}

		public void setID(String value) {
			if (check(value,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class))
				id = value;

		}

		public TraceObject(XApiConstant type, String targetId) {
				this(type.getSimpleName(), targetId);
		}
		
		public TraceObject(String type, String id){
			this.setType(type);
			this.setID(id);
		}

		public boolean isValid() throws Exception {
			return notNullEmptyOrNan(getType())
					&& notNullEmptyOrNan(getID());
		}

	}

	/**
	 * Class for Result storage.
	 */
	public static class TraceResult {

		private Boolean success;
		private Boolean completion;
		private Float score;
		private String res;

		public boolean getSuccess() {
		  return success != null && success;
		}

		public void setSuccess(boolean value) {
			this.success = value;
		}

		public boolean getCompletion() {
			return completion != null && completion;
		}

		public void setCompletion(boolean value) {
			this.completion = value;
		}


		public String getResponse() {
			return res;
		}

		public void setResponse(String value) {
			if (check(value,
					"xAPI extension: response Empty or null. Ignoring",
					"xAPI extension: response can't be empty or null",
					ValueExtensionException.class))
				res = value;

		}

		public Float getScore() {
			return score;
		}

		public void setScore(Float value) {
			if (check(value,
						"xAPI extension: score null or NaN. Ignoring",
						"xAPI extension: score can't be null or NaN.",
						ValueExtensionException.class))
				score = value;
		}

		Map<String, Object> extensions = new HashMap<>();

		public Map<String, Object> getExtensions() {
			return extensions;
		}

		public void setExtensions(Map<String, Object> value) {
			extensions = new HashMap<>();
			for (Map.Entry<String, Object> extension : value.entrySet()) {

				switch (extension.getKey().toLowerCase()) {
				case "success":
					setSuccess((boolean) extension.getValue());
					break;
				case "completion":
					setCompletion((boolean) extension.getValue());
					break;
				case "response":
					setResponse((String) extension.getValue());
					break;
				case "score":
					setScore((float) extension.getValue());
					break;
				default:
					extensions.put(extension.getKey(), extension.getValue());
					break;
				}
			}
		}

		public boolean isValid() throws Exception {
			boolean valid = true;

			// FIXME --> these are mostly redundant
			if (notNullEmptyOrNan(getResponse())) {
				setResponse(getResponse());
			}

			if (notNullEmptyOrNan(getScore())) {
				setScore(getScore());
			}

			// FIXME --> these two do nothing useful
			if (success)
				valid &= notNullEmptyOrNan(success);

			if (completion)
				valid &= notNullEmptyOrNan(completion);

			valid &= notNullEmptyOrNan(getResponse());

			valid &= notNullEmptyOrNan(score);

			if (getExtensions() != null) {
				for (Map.Entry<String, Object> extension : getExtensions().entrySet()) {
					valid &= quickCheckExtension(
							extension.getKey(), extension.getValue());
				}
			}

			return valid;
		}

	}

}
