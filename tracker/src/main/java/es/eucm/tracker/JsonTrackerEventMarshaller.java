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

import static es.eucm.tracker.TrackerUtils.complain;
import static es.eucm.tracker.TrackerUtils.notNullEmptyOrNan;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import es.eucm.tracker.TraceVerb.Verb;
import es.eucm.tracker.TrackerEvent.TraceObject;
import es.eucm.tracker.TrackerEvent.TraceResult;
import es.eucm.tracker.exceptions.TargetXApiException;

class JsonTrackerEventMarshaller implements TrackerEventMarshaller {

	private static Map<String, String> xApiVerbs = TrackerUtils.buildXApiMap(Verb.class);
	private static Map<String, String> extensionIds = TrackerUtils.buildXApiMap(TrackerAsset.Extension.class);
	private static Map<String, String> objectIds;

	private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_INSTANT;

	static {
		objectIds = new HashMap<>();
		objectIds.putAll(TrackerUtils.buildXApiMap(CompletableTracker.Completable.class));
		objectIds.putAll(TrackerUtils.buildXApiMap(AccessibleTracker.Accessible.class));
		objectIds.putAll(TrackerUtils.buildXApiMap(AlternativeTracker.Alternative.class));
		objectIds.putAll(TrackerUtils.buildXApiMap(GameObjectTracker.TrackedGameObject.class));
	}

	@Override
	public String marshal(TrackerEvent event, TrackerAsset tracker) {
		return TrackerAsset.gson.toJson(toMap(event, tracker), Map.class);
	}
	
	public Map<String, Object> toMap(TrackerEvent event, TrackerAsset tracker) {
		Map<String, Object> json = new HashMap<>();
		actorToJson(json, tracker);
		verbToJson(json, event.getEvent());
		targetToJson(tracker, json, event.getTarget());
		resultToJson(json, event.getResult());
		json.put("timestamp", TIMESTAMP_FORMAT.format(event.getTimeStamp()));
		return json;
	}

	private void actorToJson(Map<String, Object> object, TrackerAsset tracker) {
		Map<String, Object> actor = Collections.emptyMap();
		if (tracker != null) {
			Map<String, Object> currentActor = tracker.getActorObject();
			if (currentActor != null) {
				actor = currentActor;
			}
		}
		object.put("actor", actor);
	}

	private void verbToJson(Map<String, Object> object, TraceVerb verb) {
		String originalId = verb.getStringVerb();
		String xApiVerbId = xApiVerbs.get(originalId);
		String id = xApiVerbId != null ? xApiVerbId : originalId;

		Map<String, Object> jsonVerb = new HashMap<>();
		jsonVerb.put("id", id);

		object.put("verb", jsonVerb);
	}

	private void targetToJson(TrackerAsset tracker, Map<String, Object> object, TraceObject target) {
		String type = target.getType();
		if (objectIds.containsKey(type)) {
			type = objectIds.get(type);
		} else {
			String complaint = "Tracker-xAPI: Unknown definition for target type: " + type;
			complain(complaint, complaint + " - ignored", TargetXApiException.class, null);
		}
		Map<String, Object> definition = new HashMap<>();
		definition.put("type", type);

		Map<String, Object> jsonTarget = new HashMap<>();
		jsonTarget.put("definition", definition);
		// XXX SMELL FIXME - strongly suspect this logic ->
		jsonTarget.put("id", ((tracker.getActorObject() != null) ? tracker.getObjectId() : "") + target.getID());

		object.put("target", jsonTarget);
	}

	private void resultToJson(Map<String, Object> object, TraceResult result) {
		Map<String, Object> jsonResult = new HashMap<>();

		Boolean success = result.getSuccess();
		if (success != null) {
			jsonResult.put("success", success);
		}

		Boolean completion = result.getCompletion();
		if (completion != null) {
			jsonResult.put("completion", completion);
		}

		String response = result.getResponse();
		if (notNullEmptyOrNan(response)) {
			jsonResult.put("response", response);
		}

		Float score = result.getScore();

		if (score != null) {
			Map<String, Object> scoreRaw = new HashMap<>();
			scoreRaw.put("raw", score);
			jsonResult.put("score", scoreRaw);
		}

		Map<String, Object> jsonExtensions = new HashMap<>();
		for (Map.Entry<String, Object> extension : result.getExtensions().entrySet()) {
			String id = extension.getKey();
			Object value = extension.getValue();
			if (extensionIds.containsKey(id)) {
				id = extensionIds.get(id);
			}
			jsonExtensions.put(id, value);
		}
		if (!jsonExtensions.isEmpty()) {
			jsonResult.put("extensions", jsonExtensions);
		}

		if (jsonResult.size() > 0) {
			jsonResult.put("result", result);
		}
	}
}
