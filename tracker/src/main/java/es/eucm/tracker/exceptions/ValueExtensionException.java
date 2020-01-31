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

public class ValueExtensionException extends ExtensionException {

	/**
	 * @see java.io.Serializable
	 */
	private static final long serialVersionUID = 94614500251462952L;

	public ValueExtensionException(String message) {
		super(message);
	}

	public ValueExtensionException(String message, Throwable cause) {
		super(message, cause);
	}

}
