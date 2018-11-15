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


public class GameObjectTracker implements TrackerAsset.IGameObjectTracker
{
    private TrackerAsset tracker;
    public void setTracker(TrackerAsset tracker) {
        this.tracker = tracker;
    }

    public enum TrackedGameObject
    {
        /* GAMEOBJECT */
        Enemy,
        Npc,
        Item,
        GameObject
    }
    /**
    * Player interacted with a game object.
    * Type = GameObject
    * 
    *  @param gameobjectId Reachable identifier.
    */
    public void interacted(String gameobjectId) throws Exception {
        if (tracker.getUtils().check(gameobjectId,"xAPI Exception: Target ID is null or empty. Ignoring.","xAPI Exception: Target ID can't be null or empty.", TargetXApiException.class))
        {
            TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(tracker);

            trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(TrackerAsset.Verb.Interacted));
            trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(TrackedGameObject.GameObject.toString().toLowerCase(), gameobjectId));

            tracker.trace(trace);
        }
    }

    /**
    * Player interacted with a game object.
    * 
    *  @param gameobjectId TrackedGameObject identifier.
    */
    public void interacted(String gameobjectId, TrackedGameObject type) throws Exception {
        if (tracker.getUtils().check(gameobjectId,"xAPI Exception: Target ID is null or empty. Ignoring.","xAPI Exception: Target ID can't be null or empty.", TargetXApiException.class))
        {
            TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(tracker);

            trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(TrackerAsset.Verb.Interacted));
            trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(type.toString().toLowerCase(), gameobjectId));

            tracker.trace(trace);
        }
    }

    /**
    * Player interacted with a game object.
    * Type = GameObject
    * 
    *  @param gameobjectId Reachable identifier.
    */
    public void used(String gameobjectId) throws Exception {
        if (tracker.getUtils().check(gameobjectId,"xAPI Exception: Target ID is null or empty. Ignoring.","xAPI Exception: Target ID can't be null or empty.", TargetXApiException.class))
        {
            TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(tracker);

            trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(TrackerAsset.Verb.Interacted));
            trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(TrackedGameObject.GameObject.toString().toLowerCase(), gameobjectId));

            tracker.trace(trace);
        }
    }

    /**
    * Player interacted with a game object.
    * 
    *  @param gameobjectId TrackedGameObject identifier.
    */
    public void used(String gameobjectId, TrackedGameObject type) throws Exception {
        if (tracker.getUtils().check(gameobjectId,"xAPI Exception: Target ID is null or empty. Ignoring.","xAPI Exception: Target ID can't be null or empty.", TargetXApiException.class))
        {
            TrackerAsset.TrackerEvent trace = new TrackerAsset.TrackerEvent(tracker);

            trace.setEvent(new TrackerAsset.TrackerEvent.TraceVerb(TrackerAsset.Verb.Used));
            trace.setTarget(new TrackerAsset.TrackerEvent.TraceObject(type.toString().toLowerCase(), gameobjectId));

            tracker.trace(trace);
        }
    }

}


