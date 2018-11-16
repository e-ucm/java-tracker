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
package es.eucm.tracker.swing;

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
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JavaBridge implements IBridge, ILog, IWebServiceRequest {
	private static final String StorageDir = String.format(".{0}TestStorage",
			'/');
	private static final Gson gson = new Gson();

	public JavaBridge() {
	}

	public void Log(Severity severity, String msg) {
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

		String body = executePost(requestSettings);

		if (body == null) {
			result.responseCode = 0;
			result.body = "{\"bridge_msg\" : \"Error on the request\"}";
		} else {
			result.responseCode = 200;
			result.body = body;
		}

		return result;
	}

	public static String executePost(RequestSettings settings) {
		HttpURLConnection connection = null;

		try {
			// Create connection
			URL url = new URL(settings.uri.toString());
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(settings.method.toUpperCase());
			connection.setUseCaches(false);
			connection.setDoInput(true);

			Iterator it = settings.requestHeaders.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry<String, String> header = (Map.Entry) it.next();
				connection.setRequestProperty(header.getKey(),
						header.getValue());
			}

			if (settings.method.equalsIgnoreCase("post")) {
				connection.setDoOutput(true);

				OutputStreamWriter wr = new OutputStreamWriter(
						connection.getOutputStream());
				wr.write(settings.body);
				wr.flush();
				wr.close();
			}

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); // or StringBuffer if
															// Java version 5+
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			// Get Response
			InputStream is = connection.getErrorStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); // or StringBuffer if
															// Java version 5+
			String line;
			try {
				while ((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				rd.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return response.toString();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}