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

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import javax.xml.bind.JAXB;

import eu.rageproject.asset.manager.RageVersionInfo.Dependency;

/**
 * A base asset.
 * 
 * @author Ivan Martinez-Ortiz
 */
public abstract class BaseAsset implements IAsset {

	private String id;

	private IBridge bridge;

	private RageVersionInfo versionInfo;

	protected ISettings settings;

	private ILog logger = null;

	/**
	 * Specialised default constructor for use only by derived class
	 */
	protected BaseAsset() {
		this.id = AssetManager.getInstance().registerAssetInstance(this,
				this.getClassName());
		String xml = getVersionAndDependencies();
		if (!"".equals(xml)) {
			this.versionInfo = RageVersionInfo.loadVersionInfo(xml);
		} else {
			this.versionInfo = new RageVersionInfo();
		}

	}

	/**
	 * Logs
	 * 
	 * @param loglevel
	 *            The loglevel.
	 * @param format
	 *            Describes the format to use.
	 * @param args
	 *            Variable arguments providing the arguments.
	 */
	public void Log(Severity loglevel, String format, Object... args) {
		Log(loglevel, String.format(Locale.ROOT, format, args));
	}

	/**
	 * Logs
	 * 
	 * @param loglevel
	 *            The loglevel.
	 * @param msg
	 *            The message.
	 */
	public void Log(Severity loglevel, String msg) {
		logger = getInterface(ILog.class);

		if (logger != null) {
			logger.Log(loglevel, msg);
		}
	}

	/**
	 * Version and dependencies file content.
	 * 
	 * @return The version and dependencies.
	 */
	private String getVersionAndDependencies() {
		// ! <package>.Resources.<AssetType>.VersionAndDependencies.xml
		String xml = getEmbeddedResource(getClass().getPackage().getName(),
				String.format("%s.VersionAndDependencies.xml", getClass()
						.getSimpleName()));
		return xml;
	}

	/**
	 * Gets embedded resource.
	 * 
	 * @param pkg
	 *            The package.
	 * @param res
	 *            The resource name.
	 * 
	 * @return The embedded resource.
	 */
	protected String getEmbeddedResource(final String pkg, final String res) {
		String path = String.format("/%s/%s", pkg.replaceAll("\\.", "/"), res);

		InputStream in = getClass().getResourceAsStream(path);
		if (in != null) {
			try (Scanner s = new Scanner(in)) {
				return s.useDelimiter("\\Z").next();
			}
		}

		return "";
	}

	/**
	 * Constructor
	 * 
	 * @param bridge
	 *            The bridge.
	 */
	public BaseAsset(final IBridge bridge) {
		this();
		this.bridge = bridge;
	}

	/**
	 * Gets class name
	 * 
	 * @return The class name.
	 */
	@Override
	public String getClassName() {
		return getClass().getSimpleName();
	}

	/**
	 * Gets the identifier
	 * 
	 * @return The identifier.
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Gets the bridge
	 * 
	 * @return The bridge.
	 */
	@Override
	public IBridge getBridge() {
		return this.bridge;
	}

	/**
	 * Sets a bridge
	 * 
	 * @param bridge
	 *            The bridge.
	 */
	@Override
	public void setBridge(final IBridge bridge) {
		this.bridge = bridge;
	}

	/**
	 * Gets the maturity
	 * 
	 * @return The maturity.
	 */
	@Override
	public String getMaturity() {
		return this.versionInfo.getMaturity();
	}

	/**
	 * Gets the settings
	 * 
	 * @return The settings.
	 */
	public ISettings getSettings() {
		return settings;
	}

	/**
	 * Sets the settings
	 * 
	 * @param settings
	 *            Options for controlling the operation.
	 */
	public void setSettings(final ISettings settings) {
		this.settings = settings;
	}

	/**
	 * Checks if the asset has settings
	 * 
	 * @return {@code true} if this {@link Asset} has settings, {@code false}
	 *         otherwhise.
	 */
	public boolean hasSettings() {
		return this.settings != null;
	}

	/**
	 * Gets the version
	 * 
	 * @return The version.
	 */
	@Override
	public String getVersion() {
		return this.versionInfo.toString();
	}

	/**
	 * Gets version information
	 * 
	 * @return The version information.
	 */
	public RageVersionInfo getVersionInfo() {
		return this.versionInfo;
	}

	/**
	 * Sets version information
	 * 
	 * @param versionInfo
	 *            Information describing the version.
	 */
	@SuppressWarnings("unused")
	private void setVersionInfo(final RageVersionInfo versionInfo) {
		this.versionInfo = versionInfo;
	}

	/**
	 * Gets the dependencies
	 * 
	 * @return The dependencies.
	 */
	public Map<String, String> getDependencies() {
		Map<String, String> result = new TreeMap<>();

		for (Dependency dep : this.versionInfo.getDependencies()) {
			String minv = "0.0";
			String depMinv = dep.getMinVersion();
			if (depMinv != null) {
				minv = depMinv;
			}
			String maxv = "*";
			String depMaxv = dep.getMaxVersion();
			if (depMaxv != null) {
				maxv = depMaxv;
			}

			result.put(dep.getName(), String.format("%s-%s", minv, maxv));
		}

		return result;
	}

	/**
	 * Returns an object which is an instance of the given class associated with
	 * this object. Returns null if no such object can be found.
	 * 
	 * @param parameter1
	 *            the adapter class to look up.
	 * 
	 * @return a object of the given class, or null if this object does not have
	 *         an adapter for the given class.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getInterface(final Class<T> adapter) {
		if (this.bridge != null
				&& adapter.isAssignableFrom(this.bridge.getClass())) {
			return (T) this.bridge;
		}

		IBridge assetManagerBridge = AssetManager.getInstance().getBridge();
		if (assetManagerBridge != null
				&& adapter.isAssignableFrom(assetManagerBridge.getClass())) {
			return (T) assetManagerBridge;
		}

		return null;
	}

	/**
	 * Loads Settings object from Default (Design-time) Settings.
	 * 
	 * @return {@code true} if it succeeds, {@code false} otherwise.
	 */
	public Boolean loadDefaultSettings() {
		IDefaultSettings ds = getInterface(IDefaultSettings.class);

		if (ds != null && hasSettings()
				&& ds.hasDefaultSettings(getClassName(), getId())) {
			String xml = ds.loadDefaultSettings(getClassName(), getId());
			this.settings = settingsFromXml(xml);
			return true;
		}

		return false;
	}

	/**
	 * Loads Settings object as Run-time Settings.
	 * 
	 * @param filename
	 *            Filename of the file.
	 * 
	 * @return {@code true} if it succeeds, {@code false} otherwise.
	 */
	public Boolean loadSettings(final String filename) {
		IDataStorage ds = getInterface(IDataStorage.class);

		if (ds != null && hasSettings() && ds.exists(filename)) {
			String xml = ds.load(filename);
			this.settings = this.settingsFromXml(xml);
			return true;
		}

		return false;
	}

	/**
	 * Saves Settings object as Default (Design-time) Settings.
	 * 
	 * @param force
	 *            Force to save settings even if the asset has default settings.
	 * 
	 * @return {@code true} if it succeeds, {@code false} otherwise.
	 */
	public Boolean saveDefaultSettings(final boolean force) {
		IDefaultSettings ds = getInterface(IDefaultSettings.class);

		if (ds != null && hasSettings()
				&& (force || !ds.hasDefaultSettings(getClassName(), getId()))) {
			ds.saveDefaultSettings(getClassName(), getId(), settingsToXml());

			return true;
		}

		return false;
	}

	/**
	 * Save asset's settings.
	 * 
	 * @param filename
	 *            Force to save settings even if the asset has default settings.
	 * 
	 * @return {@code true} if it succeeds, {@code false} otherwise.
	 */
	public Boolean SaveSettings(final String filename) {
		IDataStorage ds = getInterface(IDataStorage.class);

		if (ds != null && hasSettings()) {
			ds.save(filename, settingsToXml());

			return true;
		}

		return false;
	}

	/**
	 * <strong>IMPLEMENTATION NOTE</strong>
	 * <p>
	 * ISettings implementations must be annotated and support JAXB required
	 * contracts to marshall and unmarshall classes.
	 * </p>
	 * 
	 * @param xml
	 *            Xml to unmarshal.
	 * 
	 * @return a {@link ISettings} object implementation.
	 */
	protected ISettings settingsFromXml(final String xml) {
		return JAXB.unmarshal(new StringReader(xml), this.settings.getClass());
	}

	/**
	 * <strong>IMPLEMENTATION NOTE</strong>
	 * <p>
	 * ISettings implementations must be annotated and support JAXB required
	 * contracts to marshall and unmarshall classes.
	 * </p>
	 * 
	 * @return Xml representation.
	 */
	protected String settingsToXml() {
		StringWriter writer = new StringWriter();
		JAXB.marshal(this.settings, writer);
		return writer.toString();
	}
}
