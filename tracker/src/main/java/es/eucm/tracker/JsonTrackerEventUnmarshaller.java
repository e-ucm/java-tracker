package es.eucm.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.eucm.tracker.TrackerUtils.XApiConstant;
import es.eucm.tracker.exceptions.UnmarshallingException;

public class JsonTrackerEventUnmarshaller implements TrackerEventUnmarshaller {

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
		
		Map<String, Object> trace = parseAsMap(event);
		
		TrackerEvent te = new TrackerEvent();
		
		return te;
	}

	public Map<String, Object> parseAsMap(String text) {
		Map<String, Object> list = null;
		try {
			list = TrackerAsset.gson.<Map<String, Object>>fromJson(text, ArrayList.class);
		} catch (Exception e) {
			throw new UnmarshallingException("Can't load trace", e);
		}
		return list;
	}
}
