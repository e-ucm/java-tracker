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
import java.util.Map;

/**
 * Http Response class.
 *
 * @author Wim van der Vegt
 */
public class RequestResponse extends RequestSettings {
    /**
     * The response code.
     */
    public int responseCode;

    /**
     * Message describing the respons.
     */
    public String responsMessage;

    /**
     * The response headers.
     */
    public Map<String, String> responseHeaders;

    public byte[] binaryResponse;

    /**
     * Initializes a new instance of the AssetPackage.RequestResponse class.
     */
    public RequestResponse() {
        hasBinaryResponse = false;

        binaryResponse = new byte[0];

        responseCode = 0;
        responsMessage = "";

        responseHeaders = new HashMap<String, String>();
    }

    /**
     * Initializes a new instance of the AssetPackage.RequestResponse class.
     *
     * The body is not copied as it will contain thee response body instead.
     *
     * @param settings Options for controlling the operation.
     */
    public RequestResponse(RequestSettings settings) {
        super();

        method = settings.method;
        requestHeaders = settings.requestHeaders;
        uri = settings.uri;
        body = "";

        allowedResponsCodes = settings.allowedResponsCodes;

        hasBinaryResponse = settings.hasBinaryResponse;
    }

    /**
     * Gets a value indicating whether result is allowed.
     * 
     * @ returns true if result allowed, false if not.
     */
    public boolean GetResultAllowed() {
        return allowedResponsCodes.contains(responseCode);
    }
}