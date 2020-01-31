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

/**
 * A tracker asset settings.
 */
public class TrackerSettings {

	/**
	 * Possible storage types
	 */
	public enum StorageTypes {
		/** network storage */
		NET,
		/** local storage */
		LOCAL
	}

	/**
	 * Possible trace formats
	 */
	public enum TraceFormats {
		/** json-formatted traces */
		JSON,
		/**
		 * An enum constant representing the xAPI option.
		 */
		XAPI,
		/** csv-formatted traces */
		CSV
	}

	// settings with defaults
	/** hostname for the analytics server */
	private String host = "localhost";
	/** port of the analytics server */
	private int port = 3000;
	/** use https? */
	private boolean secure = false;
	/** size of batches to send */
	private int batchSize = 2;
	/** endpoint to contact at the server host; such as "analytics" */
	private String basePath = "";
	/** how to store traces; StorageTypes.net sends them over the network */
	private StorageTypes storageType = StorageTypes.NET;
	/** trace format to use, such as xAPI, CSV, ... */
	private TraceFormats traceFormat = TraceFormats.JSON;
	/** use a backup storage or not */
	private boolean backupStorage = true;

	// settings with defaults set in constructor
	/** file for logs */
	private String logFile;
	/** backup log file */
	private String backupFile;

	// attributes that must be set prior to use
	/** token used to identify user; generated on login */
	private String userToken;
	/** id of player */
	private String playerId;
	/**
	 * analytics bucket for traces; identifies game version +
	 * experiment/class/whatever
	 */
	private String trackingCode;

	/**
	 * Constructor
	 */
	public TrackerSettings() {
		String timestamp = "" + System.currentTimeMillis();
		logFile = timestamp + ".log";
		backupFile = timestamp + "_backup.log";
	}

	// getters & setters - generated automatically

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public StorageTypes getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageTypes storageType) {
		this.storageType = storageType;
	}

	public TraceFormats getTraceFormat() {
		return traceFormat;
	}

	public void setTraceFormat(TraceFormats traceFormat) {
		this.traceFormat = traceFormat;
	}

	public boolean isBackupStorage() {
		return backupStorage;
	}

	public void setBackupStorage(boolean backupStorage) {
		this.backupStorage = backupStorage;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getBackupFile() {
		return backupFile;
	}

	public void setBackupFile(String backupFile) {
		this.backupFile = backupFile;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getTrackingCode() {
		return trackingCode;
	}

	public void setTrackingCode(String trackingCode) {
		this.trackingCode = trackingCode;
	}
}
