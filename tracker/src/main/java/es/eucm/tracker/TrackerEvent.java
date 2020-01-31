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
import static es.eucm.tracker.TrackerUtils.notNullEmptyOrNan;
import static es.eucm.tracker.TrackerUtils.quickCheckExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.eucm.tracker.TrackerUtils.XApiConstant;
import es.eucm.tracker.exceptions.TargetXApiException;
import es.eucm.tracker.exceptions.ValueExtensionException;

/**
 * A tracker event.
 */
public class TrackerEvent implements Comparable<TrackerEvent> {

	public static final TrackerEventIgnoreTimestampComparator TIMESTAMP_IGNORED_COMPARATOR = new TrackerEventIgnoreTimestampComparator();

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
	 * Gets the event's verb.
	 *
	 * @return the verb.
	 */
	public TraceVerb getEvent() {
		return verb;
	}

	public void setEvent(TraceVerb value) {
		this.verb = value;
		this.verb.isValid();
	}

	/**
	 * Gets the event's target.
	 *
	 * @return the target.
	 */
	public TraceObject getTarget() {
		return target;
	}

	public void setTarget(TraceObject value) {
		this.target = value;
	}

	/**
	 * Gets the event's result.
	 *
	 * @return the result.
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result
				+ ((timeStamp == null) ? 0 : timeStamp.hashCode());
		result = prime * result + ((verb == null) ? 0 : verb.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrackerEvent other = (TrackerEvent) obj;
		if (timeStamp == null) {
			if (other.timeStamp != null)
				return false;
		} else if (!timeStamp.equals(other.timeStamp))
			return false;
		if (verb == null) {
			if (other.verb != null)
				return false;
		} else if (!verb.equals(other.verb))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		return true;
	}

	@Override
	public int compareTo(TrackerEvent o) {
		int result = 0;

		if ((result = this.timeStamp.compareTo(o.timeStamp)) != 0) {
			return result;
		}

		if ((result = this.verb.compareTo(o.verb)) != 0) {
			return result;
		}

		if ((result = this.target.compareTo(o.target)) != 0) {
			return result;
		}

		if ((result = this.result.compareTo(o.result)) != 0) {
			return result;
		}

		return result;
	}

	/**
	 * Class for Target storage.
	 */
	public static class TraceObject implements Comparable<TraceObject> {
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

		public TraceObject(String type, String id) {
			this.setType(type);
			this.setID(id);
		}

		public boolean isValid() throws Exception {
			return notNullEmptyOrNan(getType()) && notNullEmptyOrNan(getID());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TraceObject other = (TraceObject) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}

		@Override
		public int compareTo(TraceObject o) {
			int result = this.id.compareTo(o.id);

			if (result == 0) {
				result = this.type.compareTo(o.type);
			}

			return result;
		}
	}

	/**
	 * Class for Result storage.
	 */
	public static class TraceResult implements Comparable<TraceResult> {

		private Boolean success;

		private Boolean completion;

		private Float score;

		private String res;

		private Map<String, Object> extensions = new HashMap<>();

		public Boolean getSuccess() {
			return success;
		}

		public void setSuccess(Boolean value) {
			this.success = value;
		}

		public Boolean getCompletion() {
			return completion;
		}

		public void setCompletion(Boolean value) {
			this.completion = value;
		}

		public String getResponse() {
			return res;
		}

		public void setResponse(String value) {
			if (check(value, "xAPI extension: response Empty or null. Ignoring",
					"xAPI extension: response can't be empty or null",
					ValueExtensionException.class))
				res = value;

		}

		public Float getScore() {
			return score;
		}

		public void setScore(Float value) {
			if (check(value, "xAPI extension: score null or NaN. Ignoring",
					"xAPI extension: score can't be null or NaN.",
					ValueExtensionException.class))
				score = value;
		}

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
					setScore((Float) extension.getValue());
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
				for (Map.Entry<String, Object> extension : getExtensions()
						.entrySet()) {
					valid &= quickCheckExtension(extension.getKey(),
							extension.getValue());
				}
			}

			return valid;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((completion == null) ? 0 : completion.hashCode());
			result = prime * result
					+ ((extensions == null) ? 0 : extensions.hashCode());
			result = prime * result + ((res == null) ? 0 : res.hashCode());
			result = prime * result + ((score == null) ? 0 : score.hashCode());
			result = prime * result
					+ ((success == null) ? 0 : success.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TraceResult other = (TraceResult) obj;
			if (completion == null) {
				if (other.completion != null)
					return false;
			} else if (!completion.equals(other.completion))
				return false;
			if (extensions == null) {
				if (other.extensions != null)
					return false;
			} else if (!extensions.equals(other.extensions))
				return false;
			if (res == null) {
				if (other.res != null)
					return false;
			} else if (!res.equals(other.res))
				return false;
			if (score == null) {
				if (other.score != null)
					return false;
			} else if (!score.equals(other.score))
				return false;
			if (success == null) {
				if (other.success != null)
					return false;
			} else if (!success.equals(other.success))
				return false;
			return true;
		}

		@Override
		public int compareTo(TraceResult o) {
			int result = 0;

			if (success == null && o.success == null) {
				result = 0;
			} else if (success == null && o.success != null) {
				return -1;
			} else if (success != null && o.success == null) {
				return 1;
			} else {
				result = success.compareTo(o.success);
			}

			if (result == 0) {
				if (completion == null && o.completion == null) {
					result = 0;
				} else if (completion == null && o.completion != null) {
					return -1;
				} else if (completion != null && o.completion == null) {
					return 1;
				} else {
					result = completion.compareTo(o.completion);
				}
			}

			if (result == 0) {
				if (score == null && o.score == null) {
					result = 0;
				} else if (score == null && o.score != null) {
					return -1;
				} else if (score != null && o.score == null) {
					return 1;
				} else {
					result = score.compareTo(o.score);
				}
			}

			if (result == 0) {
				if (res == null && o.res == null) {
					result = 0;
				} else if (res == null && o.res != null) {
					return -1;
				} else if (res != null && o.res == null) {
					return 1;
				} else {
					result = res.compareTo(o.res);
				}
			}

			if (result == 0) {
				if (extensions == null && o.extensions == null) {
					result = 0;
				} else if (extensions == null && o.extensions != null) {
					return -1;
				} else if (extensions != null && o.extensions == null) {
					return 1;
				} else {
					result = extensions.size() - o.extensions.size();
					if (result != 0) {
						return result;
					}

					List<Map.Entry<String, Object>> orderedEntries = new ArrayList<>();
					orderedEntries.addAll(extensions.entrySet());
					orderedEntries.sort(Comparator.comparing(
							(Map.Entry<String, Object> e) -> e.getKey()));

					List<Map.Entry<String, Object>> oOrderedEntries = new ArrayList<>();
					oOrderedEntries.addAll(o.extensions.entrySet());
					oOrderedEntries.sort(Comparator.comparing(
							(Map.Entry<String, Object> e) -> e.getKey()));

					Iterator<Map.Entry<String, Object>> entriesIterator = orderedEntries
							.iterator();
					Iterator<Map.Entry<String, Object>> oEntriesIterator = oOrderedEntries
							.iterator();
					Map.Entry<String, Object> entry, oEntry;

					while (result == 0 && entriesIterator.hasNext()) {
						entry = entriesIterator.next();
						oEntry = oEntriesIterator.next();
						if ((result = entry.getKey()
								.compareTo(oEntry.getKey())) != 0) {
							Object value = entry.getValue();
							Object oValue = entry.getValue();
							if (value.getClass().equals(oValue.getClass())
									&& value instanceof Comparable
									&& oValue instanceof Comparable) {
								@SuppressWarnings({ "unchecked", "rawtypes" })
								Comparable<Comparable> c1 = (Comparable<Comparable>) value;
								@SuppressWarnings({ "unchecked", "rawtypes" })
								Comparable<Comparable> c2 = (Comparable<Comparable>) oValue;
								result = c1.compareTo(c2);
							} else {
								result = value.hashCode() - oValue.hashCode();
							}
						}
					}
				}
			}

			return result;
		}
	}

	public static class TrackerEventIgnoreTimestampComparator
			implements Comparator<TrackerEvent> {
		@Override
		public int compare(TrackerEvent o1, TrackerEvent o2) {
			int result = 0;

			if ((result = o1.verb.compareTo(o2.verb)) != 0) {
				return result;
			}

			if ((result = o1.target.compareTo(o2.target)) != 0) {
				return result;
			}

			if ((result = o1.result.compareTo(o2.result)) != 0) {
				return result;
			}

			return result;
		}
	}
}
