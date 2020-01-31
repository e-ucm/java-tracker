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
package es.eucm.tracker.exceptions;

public class ExtensionXApiException extends XApiException {

	/**
	 * @see java.io.Serializable
	 */
	private static final long serialVersionUID = -4844299731627950942L;

	public ExtensionXApiException(String message) {
		super(message);
	}

	public ExtensionXApiException(String message, Throwable cause) {
		super(message, cause);
	}

}
