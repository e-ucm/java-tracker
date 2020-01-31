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

import static es.eucm.tracker.TrackerUtils.notNullEmptyOrNan;

import java.util.Map;

import es.eucm.tracker.TrackerEvent.TraceObject;
import es.eucm.tracker.TrackerEvent.TraceResult;

class CsvTrackerEventMarshaller implements TrackerEventMarshaller {

	@Override
	public String marshal(TrackerEvent event, TrackerAsset tracker) {

		StringBuilder buffer = new StringBuilder();
		buffer.append(event.getTimeStamp().toEpochMilli());
		buffer.append(",");
		verbToCsv(buffer, event.getEvent());
		buffer.append(",");
		targetToCsv(buffer, event.getTarget());
		resultToCsv(buffer, event.getResult());

		return buffer.toString();
	}

	private void verbToCsv(StringBuilder buffer, TraceVerb verb) {
		buffer.append(escapeString(verb.getStringVerb()));
	}

	private void targetToCsv(StringBuilder buffer, TraceObject target) {
		buffer.append(escapeString(target.getType()));
		buffer.append(",");
		buffer.append(escapeString(target.getID()));
	}

	private void resultToCsv(StringBuilder buffer, TraceResult result) {
		if (result == null) {
			return;
		}

		Boolean success = result.getSuccess();
		if (success != null) {
			buffer.append(",success,");
			buffer.append(Boolean.toString(success));
		}
		Boolean completion = result.getCompletion();
		if (completion != null) {
			buffer.append(",completion,");
			buffer.append(Boolean.toString(completion));
		}
		String response = result.getResponse();
		if (notNullEmptyOrNan(response)) {
			buffer.append(",response,");
			buffer.append(escapeString(response));
		}
		Float score = result.getScore();
		if (score != null) {
			buffer.append(",score,");
			buffer.append(decimalToString(score));
		}
		Map<String, Object> extensions = result.getExtensions();
		if (extensions != null) {
			for (Map.Entry<String, Object> extension : extensions.entrySet()) {
				String key = extension.getKey();
				Object value = extension.getValue();
				buffer.append(",");
				buffer.append(escapeString(key));
				buffer.append(",");
				if (value != null) {
					if (value instanceof String) {
						buffer.append(escapeString((String) value));
					} else if ((value instanceof Float)) {
						buffer.append(decimalToString((Float) value));
					} else if ((value instanceof Double)) {
						buffer.append(decimalToString((Double) value));
					} else if ((value instanceof Integer)) {
						buffer.append(integerToString((Integer) value));
					} else if (value instanceof Map) {
						Map<?, ?> map = (Map<?, ?>) value;
						StringBuilder inner = new StringBuilder();
						int count = 0;
						for (Map.Entry<?, ?> e : map.entrySet()) {
							if (count > 0) {
								buffer.append("-");
							}
							inner.append(e.getKey()).append("=").append(
									e.getValue().toString().toLowerCase());
							count++;
						}
					} else {
						buffer.append(String.valueOf(value));
					}
				}
			}
		}
	}

	private String escapeString(String string) {
		return string.replace(",", "\\,");
	}

	private String decimalToString(double value) {
		return Double.toString(value);
	}

	private String decimalToString(float value) {
		return Float.toString(value);
	}

	private String integerToString(int value) {
		return Integer.toString(value);
	}
}
