/**
 * Copyright © 2019-20 e-UCM (http://www.e-ucm.es/)
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

/*
 * Copyright 2016 Open University of the Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * This project has received funding from the European Union’s Horizon
 * 2020 research and innovation programme under grant agreement No 644187.
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

import com.google.gson.Gson;
import eu.rageproject.asset.manager.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TesterBridge
		implements IBridge, ILog, IDataStorage, IAppend, IWebServiceRequest {
	private static final String StorageDir = String.format(".{0}TestStorage",
			'/');
	private static final Gson gson = new Gson();

	public TesterBridge() {
	}

	Map<String, String> files = new HashMap<>();

	public boolean exists(String fileId) {
		return files.containsKey(fileId);
	}

	public String[] files() {
		return null;
	}

	public void save(String fileId, String fileData) {
		files.put(fileId, fileData);
	}

	public String load(String fileId) {
		String content = "";

		if (exists(fileId))
			content = files.get(fileId);

		return content;
	}

	public boolean delete(String fileId) {
		if (exists(fileId))
			files.remove(fileId);

		return true;
	}

	public void Append(String fileId, String fileData) {
		if (exists(fileId))
			files.put(fileId, files.get(fileId) + fileData);
		else
			files.put(fileId, fileData);
	}

	public void log(Severity severity, String msg) {
		{
			if (msg == null || msg.isEmpty()) {
				System.out.println("");
			} else {
				System.out.println(String.format("%s: %2$s", severity, msg));
			}
		}
	}

	boolean connected = true;

	public boolean getConnnected() {
		return this.connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public RequestResponse WebServiceRequest(RequestSettings requestSettings) {
		RequestResponse result = new RequestResponse(requestSettings);

		if (connected) {
			result.responseCode = 200;
			result.body = gson.toJson(gson.fromJson("{"
					+ "\"authToken\": \"5a26cb5ac8b102008b41472b5a30078bc8b102008b4147589108928341\", "
					+ "\"actor\": { \"account\": { \"homePage\": \"http://a2:3000/\", \"name\": \"Anonymous\"}, \"name\": \"test-animal-name\"}, "
					+ "\"playerAnimalName\": \"test-animal-name\", "
					+ "\"playerId\": \"5a30078bc8b102008b41475769103\", "
					+ "\"objectId\": \"http://a2:3000/api/proxy/gleaner/games/5a26cb5ac8b102008b41472a/5a26cb5ac8b102008b41472b\", "
					+ "\"session\": 1, "
					+ "\"firstSessionStarted\": \"2017-12-12T16:44:59.273Z\", "
					+ "\"currentSessionStarted\": \"2017-12-12T16:44:59.273Z\" "
					+ "}", HashMap.class));

			this.Append("netstorage", requestSettings.body);
		} else {
			result.responseCode = 0;
		}

		return result;
	}
}