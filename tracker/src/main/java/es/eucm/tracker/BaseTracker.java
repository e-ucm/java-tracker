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

import java.util.Objects;
import java.util.Optional;

import es.eucm.tracker.AccessibleTracker.Accessible;
import es.eucm.tracker.AlternativeTracker.Alternative;
import es.eucm.tracker.CompletableTracker.Completable;
import es.eucm.tracker.TrackerUtils.XApiConstant;
import es.eucm.tracker.exceptions.TargetXApiException;
import es.eucm.tracker.exceptions.ValueExtensionException;

class BaseTracker {
	
	protected TrackerEvent generateTrace(TraceVerb verb, Accessible type, String targetId) {
		TrackerEvent trace = null;
		TrackerEvent.TraceObject target = createTarget(type, Accessible.Accessible, targetId);
		if ( target != null ) {
			trace = generateTrace(verb, new TraceVerb(TraceVerb.Verb.Accessed), target);
		}
		return trace;
	}
	
	protected TrackerEvent generateInitializedTrace(TraceVerb verb, Completable type, String targetId) {
		TrackerEvent trace = null;
		TrackerEvent.TraceObject target = createTarget(type, Completable.Completable, targetId);
		if ( target != null ) {
			trace = generateTrace(verb, new TraceVerb(TraceVerb.Verb.Initialized), target);
		}
		return trace;
	}
	
	protected TrackerEvent generateProgressTrace(TraceVerb verb, Completable type, String targetId) {
		TrackerEvent trace = null;
		TrackerEvent.TraceObject target = createTarget(type, Completable.Completable, targetId);
		if ( target != null ) {
			trace = generateTrace(verb, new TraceVerb(TraceVerb.Verb.Progressed), target);
		}
		return trace;
	}
	
	protected TrackerEvent generateTrace(TraceVerb verb, Alternative type, String optionId, String targetId) {
		
		TrackerEvent trace = null;
		
		TrackerEvent.TraceObject target = createTarget(type, Alternative.Alternative, targetId);
		
		boolean check = target != null;
		check = check && TrackerUtils.check(optionId,
				"xAPI Exception: Selected alternative is null or empty",
				"xAPI Exception: Selected alternative can't be null or empty",
				ValueExtensionException.class);
		
		if ( check ) {
			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setResponse(optionId);
			trace = generateTrace(verb, new TraceVerb(TraceVerb.Verb.Accessed), target, Optional.of(result));
		}
		return trace;
	}
	
	protected TrackerEvent generateSuccessTrace(TraceVerb verb, Completable type, String targetId,
			boolean success, float score) {
		
		TrackerEvent trace = null;
		
		TrackerEvent.TraceObject target = createTarget(type, Completable.Completable, targetId);
		
		boolean check = target != null;
		
		if ( check ) {
			TrackerEvent.TraceResult result = new TrackerEvent.TraceResult();
			result.setSuccess(success);
			result.setScore(score);
			trace = generateTrace(verb, new TraceVerb(TraceVerb.Verb.Completed), target, Optional.of(result));
		}
		return trace;
	}

	private TrackerEvent.TraceObject createTarget(XApiConstant targetType, XApiConstant defaultTargetType, String targetId) {
		Objects.requireNonNull(defaultTargetType, "defaultTargetType must not be null.");
		
		boolean check = TrackerUtils.check(targetId,
				"xAPI Exception: Target ID is null or empty. Ignoring.",
				"xAPI Exception: Target ID can't be null or empty.",
				TargetXApiException.class);
		
		if( ! (check = check && TrackerUtils.check(targetType, 
				"target type is null. Using default: "+defaultTargetType.getId(),
				"target can't be null",
				TargetXApiException.class)) ) {
			
			targetType = defaultTargetType;
		}
		
		TrackerEvent.TraceObject target = null;
		if ( check ) {
			target = new TrackerEvent.TraceObject(targetType, targetId);
		}
		return target;
	}
	
	private TrackerEvent generateTrace(TraceVerb verb, TraceVerb defaultVerb, TrackerEvent.TraceObject target) {
		return generateTrace(verb, defaultVerb, target, Optional.empty());
	}
	
	private TrackerEvent generateTrace(TraceVerb verb, TraceVerb defaultVerb, TrackerEvent.TraceObject target, Optional<TrackerEvent.TraceResult> result) {
		Objects.requireNonNull(defaultVerb, "defaultVerb must not be null.");
		
		boolean check = true;

		if ( ! (check = check && TrackerUtils.check(verb,
				"xAPI Exception: verb is null or empty. Unsing default: "+defaultVerb.getStringVerb(),
				"xAPI Exception: verb can't be null or empty.",
				TargetXApiException.class)) ) {
					
				verb = defaultVerb;
		}

		TrackerEvent generatedTrace = null;
		if ( check ) {
			final TrackerEvent trace = new TrackerEvent();
			trace.setEvent(verb);
			trace.setTarget(target);
			result.ifPresent(r -> trace.setResult(r));
			generatedTrace = trace;
		}
		
		return generatedTrace;
	}
}
