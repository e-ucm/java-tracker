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
//
// Translated by CS2J (http://www.cs2j.com): 05/11/2018 15:29:16
//

package es.eucm.tracker;

import es.eucm.tracker.exceptions.KeyExtensionException;
import es.eucm.tracker.exceptions.TrackerException;
import es.eucm.tracker.exceptions.ValueExtensionException;
import eu.rageproject.asset.manager.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities
 */
public class TrackerUtils {

	/** disallows instantiation */
	private TrackerUtils() {}

	/**
	 * Log or throw an exception.
	 * @param message to log (if strict mode not enabled)
	 * @param strictMessage to throw, wrapped in a
	 * @param exceptionClass to throw, if required
	 * @param cause to include in exception, if required. Use null to avoid.
	 * @throws TrackerException
	 */
	public static void complain(TrackerAsset tracker, String message, String strictMessage,
	                     Class<? extends TrackerException> exceptionClass, Throwable cause)
			throws TrackerException {
		if (tracker.isStrictMode()) {
			TrackerException complaint = null;
			try {
				complaint =  exceptionClass.getConstructor(String.class, Throwable.class)
						.newInstance(strictMessage, cause);
			} catch (Exception e) {
				tracker.log(Severity.Error, "Exception reporting exception: missing constructors for "
						+ exceptionClass.getCanonicalName() + ": " + e);
				e.printStackTrace();
				throw new TrackerException(strictMessage, cause);
			}
			throw complaint;
		} else {
			tracker.log(Severity.Warning, message);
		}
	}

	/**
	 * Parses a trace line into a list
	 * @param trace to parse
	 * @return list of parts
	 * @throws TrackerException
	 */
	public static List<String> parseCSV(String trace) throws TrackerException {
		List<String> p = new ArrayList<>();
		boolean escape = false;
		int start = 0; // start of current field
		int length = trace.length();
		for (int i = 0; i < length; i++) {
			char c = trace.charAt(i);
			if (c == '\\') {
				escape = true;
			} else if (c == ',') {
				if (!escape) {
					// flush recently-ended field into list
					p.add(trace.substring(start, i).replace("\\,", ","));
					start = i + 1;
				} else {
					escape = false;
				}
			}
		}
		// flush last segment of field
		p.add(trace.substring(start).replace("\\,", ","));
		return p;
	}

	public static boolean quickCheckExtension(String key, Object value)
			throws Exception {
		return notNullEmptyOrNan(key) && notNullEmptyOrNan(value);
	}

	public static boolean checkExtension(String key, Object value, TrackerAsset tracker) throws Exception {
		boolean keyOk = check(key, tracker,
				"Tracker: Extension key is null or empty. Ignored extension.",
				"Tracker: Extension key is null or empty.",
				KeyExtensionException.class);
		boolean valueOk = check(value, tracker,
				"Tracker: Extension value is null or empty. Ignored extension.",
				"Tracker: Extension value is null or empty.",
				ValueExtensionException.class);
		return keyOk && valueOk;
	}

	public static boolean notNullEmptyOrNan(Object value) {
		boolean bad =
				(value == null) ||
				(value instanceof String && "".equals(value)) ||
				(value instanceof Float && Float.isNaN((Float) value));
		return ! bad;
	}

	public static boolean check(Object value,
	                     TrackerAsset tracker, String message, String strictMessage,
			Class<? extends TrackerException> c) throws Exception {
		boolean ok = notNullEmptyOrNan(value);
		if (!ok) {
			complain(tracker, message, strictMessage, c, null);
		}
		return ok;
	}

	public static boolean checkFloat(String value, TrackerAsset tracker,
	                                 String message, String strictMessage, Class<? extends TrackerException> c)
			throws TrackerException {
		try {
			Float.parseFloat(value);
		} catch (Exception ex) {
			complain(tracker, message, strictMessage, c, ex);
			return false;
		}
		return true;
	}

	public static boolean checkBoolean(String value, String message,
	                                   TrackerAsset tracker, String strictMessage, Class<? extends TrackerException> c)
			throws TrackerException {
		try {
			Boolean.parseBoolean(value);
		} catch (Exception ex) {
			complain(tracker, message, strictMessage, c, null);
			return false;
		}
		return true;
	}

	/**
	 * Complains if the input is not a valid enum; also returns result, if any.
	 */
	public static <E extends Enum<E>> E parseEnum(String text, Class<E> enumType,
			TrackerAsset tracker, String message, String strictMessage,
			Class<? extends TrackerException> c) throws TrackerException {
		Exception cause = null;
		try {
			for (E enumValue : enumType.getEnumConstants()) {
				if (enumValue.toString().equalsIgnoreCase(text)) {
					return enumValue;
				}
			}
		} catch (Exception e) {
			cause = e;
		}
		complain(tracker, message, strictMessage, c, cause);
		return null;
	}

	/**
	 * Return a new map of XApiConstant enum-values (lowercase) to ids.
	 * This should be cached for efficiency
	 */
	public static <T extends Enum<T>&TrackerAsset.XApiConstant>
			Map<String, String> buildXApiMap(Class<T> enumType) {
		Map<String, String> map = new HashMap<>();
		for (T v : enumType.getEnumConstants()) {
			// some XApiConstants (such as Extension) have elements without IDs
			if (v.getId() != null) {
				map.put(v.toString().toLowerCase(), v.getId());
			}
		}
		return map;
	}
}
