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
package eu.rageproject.asset.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Values that represent messages
 * 
 * @author Ivan Martinez-Ortiz
 */
public enum Messages {

	INSTANCE;

	/** Interface for messages event callback. */
	public static interface MessagesEventCallback {
		public void messageUpdated(final String topic, final Object... params);
	}

	/**
	 * Gets the instance
	 * 
	 * @return The instance.
	 */
	public static final Messages getInstance() {
		return Messages.INSTANCE;
	}

	private int subUid;

	private Map<String, Map<String, MessagesEventCallback>> messages;

	/**
	 * Avoid manual instantiation
	 */
	private Messages() {
		this.subUid = 0;
		this.messages = new HashMap<>();
	}

	/**
	 * Defines a Message.
	 * 
	 * @param message
	 *            The message.
	 * 
	 * @return True if it succeeds, false if it fails.
	 */
	public boolean define(String message) {
		if (!messages.containsKey(message)) {
			messages.put(message, new HashMap<String, MessagesEventCallback>());
			return true;
		}
		return false;
	}

	/**
	 * Broadcasts a Message.
	 * 
	 * @param message
	 *            The message.
	 * @param params
	 *            Variable arguments providing options for controlling the
	 *            operation.
	 * 
	 * @return True if it succeeds, false if it fails.
	 */
	public boolean broadcast(final String message, final Object... params) {
		if (!messages.containsKey(message)) {
			return false;
		}

		for (Map.Entry<String, MessagesEventCallback> entry : messages.get(
				message).entrySet()) {
			entry.getValue().messageUpdated(message, params);
		}

		return true;
	}

	/**
	 * Subscribes to a Message.
	 * 
	 * @param message
	 *            The message.
	 * @param callback
	 *            The callback.
	 * 
	 * @return A String.
	 */
	public String subscribe(final String message,
			final MessagesEventCallback callback) {
		define(message);

		String subscriptionId = Integer.toString(++this.subUid);
		messages.get(message).put(subscriptionId, callback);

		return subscriptionId;
	}

	/**
	 * Unsubscribes from a Message given the subscription identifier
	 * 
	 * @param subscriptionId
	 *            Identifier for the subscription.
	 * 
	 * @return True if it succeeds, false if it fails.
	 */
	public boolean unsubscribe(final String subscriptionId) {
		for (Map.Entry<String, Map<String, MessagesEventCallback>> message : messages
				.entrySet()) {
			Iterator<Map.Entry<String, MessagesEventCallback>> subscribers = message
					.getValue().entrySet().iterator();

			while (subscribers.hasNext()) {
				Map.Entry<String, MessagesEventCallback> subscriber = subscribers
						.next();
				if (subscriber.getKey().equals(subscriptionId)) {
					subscribers.remove();
					return true;
				}
			}
		}
		return false;
	}
}
