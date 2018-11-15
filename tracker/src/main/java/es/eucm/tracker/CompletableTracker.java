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

public class CompletableTracker implements TrackerAsset.IGameObjectTracker
{
    private TrackerAsset tracker;
    public void setTracker(TrackerAsset tracker) {
        this.tracker = tracker;
    }

    public enum Completable
    {
        /* COMPLETABLES */
        Game,
        Session,
        Level,
        Quest,
        Stage,
        Combat,
        StoryNode,
        Race,
        Completable
    }
    /**
    * Player initialized a completable.
    * 
    *  @param completableId Completable identifier.
    */
    public void initialized(String completableId) throws Exception {
        if (tracker.getUtils().check(completableId,"xAPI Exception: Target ID is null or empty. Ignoring.","xAPI Exception: Target ID can't be null or empty.", TargetXApiException.class))
        {
            TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(tracker);

            trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(TrackerAsset.Verb.Initialized));
            trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(Completable.Completable.toString().toLowerCase(), completableId));

            tracker.trace(trace);
        }
    }

    /**
    * Player initialized a completable.
    * 
    *  @param completableId Completable identifier.
    *  @param type Completable type.
    */
    public void initialized(String completableId, Completable type) throws Exception {
        if (tracker.getUtils().check(completableId,"xAPI Exception: Target ID is null or empty. Ignoring.","xAPI Exception: Target ID can't be null or empty.", TargetXApiException.class))
        {
            TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(tracker);

            trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(TrackerAsset.Verb.Initialized));
            trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(type.toString().toLowerCase(), completableId));

            tracker.trace(trace);
        }
    }

    /**
    * Player progressed a completable.
    * Type = Completable
    * 
    *  @param completableId Completable identifier.
    *  @param value New value for the completable's progress.
    */
    public void progressed(String completableId, float value) throws Exception {
        if (tracker.getUtils().check(completableId,"xAPI Exception: Target ID is null or empty. Ignoring.","xAPI Exception: Target ID can't be null or empty.", TargetXApiException.class))
        {
            TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(tracker);

            trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(TrackerAsset.Verb.Progressed));
            trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(Completable.Completable.toString().toLowerCase(), completableId));

            tracker.setProgress(value);
            tracker.trace(trace);
        }
    }

    /**
    * Player progressed a completable.
    * 
    *  @param completableId Completable identifier.
    *  @param value New value for the completable's progress.
    *  @param type Completable type.
    */
    public void progressed(String completableId, Completable type, float value) throws Exception {
        if (tracker.getUtils().check(completableId,"xAPI Exception: Target ID is null or empty. Ignoring.","xAPI Exception: Target ID can't be null or empty.", TargetXApiException.class))
        {
            TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(tracker);

            trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(TrackerAsset.Verb.Progressed));
            trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(type.toString().toLowerCase(), completableId));

            tracker.setProgress(value);
            tracker.trace(trace);
        }
    }

    /**
    * Player completed a completable.
    * Type = Completable
    * Success = true
    * Score = 1
    * 
    *  @param completableId Completable identifier.
    */
    public void completed(String completableId) throws Exception {
        if (tracker.getUtils().check(completableId,"xAPI Exception: Target ID is null or empty. Ignoring.","xAPI Exception: Target ID can't be null or empty.", TargetXApiException.class))
        {
            TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(tracker);

            trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(TrackerAsset.Verb.Completed));
            trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(Completable.Completable.toString().toLowerCase(), completableId));

            TrackerAsset.TrackerEvent.TraceResult result = new TrackerAsset.TrackerEvent.TraceResult();
            result.setSuccess(true);
            result.setScore(1f);
            trace.setResult(result);

            tracker.trace(trace);
        }
    }

    /**
    * Player completed a completable.
    * Success = true
    * Score = 1
    * 
    *  @param completableId Completable identifier.
    *  @param type Completable type.
    */
    public void completed(String completableId, Completable type) throws Exception {
        if (tracker.getUtils().check(completableId,"xAPI Exception: Target ID is null or empty. Ignoring.","xAPI Exception: Target ID can't be null or empty.", TargetXApiException.class))
        {
            TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(tracker);

            trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(TrackerAsset.Verb.Completed));
            trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(type.toString().toLowerCase(), completableId));

            TrackerAsset.TrackerEvent.TraceResult result = new TrackerAsset.TrackerEvent.TraceResult();
            result.setSuccess(true);
            result.setScore(1f);
            trace.setResult(result);

            tracker.trace(trace);
        }
    }

    /**
    * Player completed a completable.
    * Score = 1
    * 
    *  @param completableId Completable identifier.
    *  @param type Completable type.
    *  @param success Completable success.
    */
    public void completed(String completableId, Completable type, boolean success) throws Exception {
        if (tracker.getUtils().check(completableId,"xAPI Exception: Target ID is null or empty. Ignoring.","xAPI Exception: Target ID can't be null or empty.", TargetXApiException.class))
        {
            TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(tracker);

            trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(TrackerAsset.Verb.Completed));
            trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(Completable.Completable.toString().toLowerCase(), completableId));

            TrackerAsset.TrackerEvent.TraceResult result = new TrackerAsset.TrackerEvent.TraceResult();
            result.setSuccess(success);
            result.setScore(1f);
            trace.setResult(result);

            tracker.trace(trace);
        }
    }

    /**
    * Player completed a completable.
    * 
    *  @param completableId Completable identifier.
    *  @param type Completable type.
    *  @param score Completable score.
    */
    public void completed(String completableId, Completable type, float score) throws Exception {
        if (tracker.getUtils().check(completableId,"xAPI Exception: Target ID is null or empty. Ignoring.","xAPI Exception: Target ID can't be null or empty.", TargetXApiException.class))
        {
            TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(tracker);

            trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(TrackerAsset.Verb.Completed));
            trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(type.toString().toLowerCase(), completableId));

            TrackerAsset.TrackerEvent.TraceResult result = new TrackerAsset.TrackerEvent.TraceResult();
            result.setSuccess(true);
            result.setScore(score);
            trace.setResult(result);

            tracker.trace(trace);
        }
    }

    /**
    * Player completed a completable.
    * 
    *  @param completableId Completable identifier.
    *  @param type Completable type.
    *  @param success Completable success.
    *  @param score Completable score.
    */
    public void completed(String completableId, Completable type, boolean success, float score) throws Exception {
        if (tracker.getUtils().check(completableId,"xAPI Exception: Target ID is null or empty. Ignoring.","xAPI Exception: Target ID can't be null or empty.", TargetXApiException.class))
        {
            TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(tracker);

            trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(TrackerAsset.Verb.Completed));
            trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(type.toString().toLowerCase(), completableId));

            TrackerAsset.TrackerEvent.TraceResult result = new TrackerAsset.TrackerEvent.TraceResult();
            result.setSuccess(success);
            result.setScore(score);
            trace.setResult(result);

            tracker.trace(trace);
        }
    }

}


