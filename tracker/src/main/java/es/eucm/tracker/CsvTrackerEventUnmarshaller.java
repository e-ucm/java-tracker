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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import es.eucm.tracker.TrackerUtils.XApiConstant;
import es.eucm.tracker.exceptions.TrackerException;
import es.eucm.tracker.exceptions.UnmarshallingException;

public class CsvTrackerEventUnmarshaller implements TrackerEventUnmarshaller {

	// XXX Fragile CVS format must use full IRI !
	private static Map<String, XApiConstant> OBJECT_TYPES;

	static {
		OBJECT_TYPES = new HashMap<>();
		OBJECT_TYPES.putAll(TrackerUtils.buildReverseXApiMap(CompletableTracker.Completable.class));
		OBJECT_TYPES.putAll(TrackerUtils.buildReverseXApiMap(AccessibleTracker.Accessible.class));
		OBJECT_TYPES.putAll(TrackerUtils.buildReverseXApiMap(AlternativeTracker.Alternative.class));
		OBJECT_TYPES.putAll(TrackerUtils.buildReverseXApiMap(GameObjectTracker.TrackedGameObject.class));
	}

  private static boolean parseBoolean(String s) {
  	boolean trueValue = ((s != null) && s.equalsIgnoreCase("true"));
  	boolean falseValue = ((s != null) && s.equalsIgnoreCase("false"));
  	if ( ! (trueValue || falseValue) ) {
  		throw new UnmarshallingException("Not a valid boolean: "+s);
  	}
    return trueValue;
  }
  
  private static float parseFloat(String s) {
  	float value = 0.0f;
  	
  	try {
  		value = Float.parseFloat(s);
  	} catch (NullPointerException | NumberFormatException e) {
  		throw new UnmarshallingException("Not a valid float: "+s);
  	} 
  	
  	return value;
  }
	
	@Override
	public TrackerEvent unmarshal(String event) {
		if (event == null) {
			throw new NullPointerException("event must not be null");
		}
		
		if ("".equals(event)) {
			throw new IllegalArgumentException("event must not be emtpy");
		}
		
		String[] components = event.split(",");
		if (components.length < 4) {
			throw new UnmarshallingException("At least 4 values are required: <timestamp>,<verb>,<objectType>,<objetID>");
		}
		
		
		Instant timestamp = null;
		try {
			long millis = Long.parseLong(components[0]);
			if (millis < 0) {
				throw new UnmarshallingException("timestamp must not be negative: "+components[0]);
			}
			timestamp = Instant.ofEpochMilli(millis);
		} catch (NumberFormatException e) {
			throw new UnmarshallingException("timestamp is not valid", e);
		}
		
		TraceVerb verb = null;
		try {
			verb = new TraceVerb(components[1]);
		} catch (TrackerException e) {
			throw new UnmarshallingException("Can not parse verb", e);
		}
		
		TrackerEvent.TraceObject object = null;
		XApiConstant objectType = OBJECT_TYPES.get(components[2]);
		if (objectType == null) {
			throw new UnmarshallingException("object type is not recognized: "+components[2]);
		}
		
		try {
			object = new TrackerEvent.TraceObject(objectType, components[3]);
		} catch (TrackerException e) {
			throw new UnmarshallingException("Can not parse target object", e);
		}
		
		Map<String, Object> extensions = parseExtensions(components, 4);
		
		TrackerEvent te = new TrackerEvent(timestamp);
		te.setEvent(verb);
		te.setTarget(object);
		if (extensions.size() > 0) {
			te.getResult().setExtensions(extensions);
		}
		
		return te;
	}
	
	private Map<String, Object> parseExtensions(String[] values, int startIdx) {
		Map<String, Object> extensions = new HashMap<>();
		for (int i = startIdx; i+1 < values.length; i+=2) {
			String id = values[i];
			String value = values[i+1];
			switch (id.toLowerCase()) {
			case "success":
			case "completion":
				extensions.put(id, parseBoolean(value));
				break;
			case "score":
				extensions.put(id, parseFloat(value));
				break;
			case "response":
			default:
				extensions.put(id, value);
				break;
			}
		}
		
		return extensions;
	}
}
