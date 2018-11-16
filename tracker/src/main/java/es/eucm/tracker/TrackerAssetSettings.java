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
//
// Translated by CS2J (http://www.cs2j.com): 05/11/2018 15:29:16
//

package es.eucm.tracker;

import eu.rageproject.asset.manager.*;

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

/**
 * A tracker asset settings.
 */
public class TrackerAssetSettings extends BaseSettings {
	/**
	 * Initializes a new instance of the AssetPackage.TrackerAssetSettings
	 * class.
	 */
	public TrackerAssetSettings() {
		super();
		// Apply 'Factory' defaults.
		//
		setHost("localhost");
		setPort(3000);
		setSecure(false);
		setBatchSize(2);
		String timestamp = String.valueOf(System.currentTimeMillis());
		setLogFile(timestamp + ".log");
		setBackupStorage(true);
		setBackupFile(timestamp + "_backup.log");
	}

	/**
	 * Gets or sets the host.
	 * 
	 * The host.
	 */
	private String __Host = "";

	public String getHost() {
		return __Host;
	}

	public void setHost(String value) {
		__Host = value;
	}

	/**
	 * Gets or sets the host.
	 * 
	 * The host.
	 */
	private int __Port = -1;

	public int getPort() {
		return __Port;
	}

	public void setPort(int value) {
		__Port = value;
	}

	/**
	 * Gets or sets a value indicating whether to use http or https.
	 * 
	 * true if secure, false if not.
	 */
	private boolean __Secure = false;

	public boolean getSecure() {
		return __Secure;
	}

	public void setSecure(boolean value) {
		__Secure = value;
	}

	/**
	 * Gets or sets the full pathname of the base file.
	 * 
	 * Should either be empty or else start with a /. Should not include a
	 * trailing /.
	 * 
	 * The full pathname of the base file.
	 */
	private String __BasePath = new String();

	public String getBasePath() {
		return __BasePath;
	}

	public void setBasePath(String value) {
		__BasePath = value;
	}

	/**
	 * Gets or sets the user Authentication token.
	 * 
	 * The user token.
	 */
	private String __UserToken = new String();

	public String getUserToken() {
		return __UserToken;
	}

	public void setUserToken(String value) {
		__UserToken = value;
	}

	/**
	 * Gets or sets the user PlayerId (Anonymous or Identified)
	 * 
	 * The user token.
	 */
	private String __PlayerId = new String();

	public String getPlayerId() {
		return __PlayerId;
	}

	public void setPlayerId(String value) {
		__PlayerId = value;
	}

	/**
	 * Gets or sets the game tracking code.
	 * 
	 * The tracking code.
	 */
	private String __TrackingCode = new String();

	public String getTrackingCode() {
		return __TrackingCode;
	}

	public void setTrackingCode(String value) {
		__TrackingCode = value;
	}

	/**
	 * Gets or sets the type of the storage.
	 * 
	 * The type of the storage.
	 */
	private TrackerAsset.StorageTypes __StorageType = TrackerAsset.StorageTypes.net;

	public TrackerAsset.StorageTypes getStorageType() {
		return __StorageType;
	}

	public void setStorageType(TrackerAsset.StorageTypes value) {
		__StorageType = value;
	}

	/**
	 * Save in Backup Storage
	 * 
	 * If true, backup storage is enabled.
	 */
	private boolean __BackupStorage = true;

	public boolean getBackupStorage() {
		return __BackupStorage;
	}

	public void setBackupStorage(boolean value) {
		__BackupStorage = value;
	}

	/**
	 * Gets or sets the trace format.
	 * 
	 * The trace format.
	 */
	private TrackerAsset.TraceFormats __TraceFormat = TrackerAsset.TraceFormats.json;

	public TrackerAsset.TraceFormats getTraceFormat() {
		return __TraceFormat;
	}

	public void setTraceFormat(TrackerAsset.TraceFormats value) {
		__TraceFormat = value;
	}

	/**
	 * Gets or sets the maximum size of the batch to be flushed.
	 * 
	 * A value of 0 results in no limit on the batch size.
	 * 
	 * The maximum size of the batch.
	 */
	private Integer __BatchSize = 4;

	public Integer getBatchSize() {
		return __BatchSize;
	}

	public void setBatchSize(Integer value) {
		__BatchSize = value;
	}

	/**
	 * Gets or sets the log file.
	 * 
	 * The log file.
	 */
	private String __LogFile = new String();

	public String getLogFile() {
		return __LogFile;
	}

	public void setLogFile(String value) {
		__LogFile = value;
	}

	/**
	 * Gets or sets the log's backup file.
	 * 
	 * The log file.
	 */
	private String __BackupFile = new String();

	public String getBackupFile() {
		return __BackupFile;
	}

	public void setBackupFile(String value) {
		__BackupFile = value;
	}

}
