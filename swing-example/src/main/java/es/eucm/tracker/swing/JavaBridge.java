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
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

public class JavaBridge implements IBridge, ILog, IWebServiceRequest {
	public JavaBridge() {
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

	public RequestResponse WebServiceRequest(RequestSettings requestSettings) {
		return executePost(requestSettings);
	}

	public static RequestResponse executePost(RequestSettings settings) {
		HttpURLConnection connection = null;
		RequestResponse response = new RequestResponse();
		response.requestHeaders = settings.requestHeaders;
		response.method = settings.method;
		response.uri = settings.uri;

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

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
				sb.append('\r');
			}
			rd.close();

			response.responseCode = connection.getResponseCode();
			response.body = sb.toString();
		} catch (UnknownHostException uhe) {
			response.responseCode = -1;
			response.responsMessage = "Unknown host: " + response.uri.getHost();
		} catch (Exception e) {

			try {
				InputStream is = connection.getErrorStream();
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
					sb.append('\r');
				}
				rd.close();

				response.responseCode = connection.getResponseCode();
				response.responsMessage = sb.toString();
				response.body = sb.toString();
			} catch (Exception e2) {
				response.responseCode = -1;
				response.responsMessage = e2.getMessage();
				response.body = "{\"bridge_msg\":\"" + e2.getMessage() + "\"}";
			}
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return response;
	}
}