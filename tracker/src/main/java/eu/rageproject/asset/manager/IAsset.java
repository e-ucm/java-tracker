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

import java.util.Map;

/**
 * Interface for asset, used to enforce type safety.
 * 
 * @author Ivan Martinez-Ortiz
 */
public interface IAsset {

	/**
	 * Gets class name
	 * 
	 * @return The class name.
	 */
	String getClassName();

	/**
	 * Gets the identifier
	 * 
	 * @return The identifier.
	 */
	String getId();

	/**
	 * Gets the dependencies
	 * 
	 * @return The dependencies.
	 */
	Map<String, String> getDependencies();

	/**
	 * Gets the maturity
	 * 
	 * @return The maturity.
	 */
	String getMaturity();

	/**
	 * Gets the settings
	 * 
	 * @return The settings.
	 */
	ISettings getSettings();

	/**
	 * Sets the settings
	 * 
	 * @param settings
	 *            Options for controlling the operation.
	 */
	void setSettings(final ISettings settings);

	/**
	 * Gets the version
	 * 
	 * @return The version.
	 */
	String getVersion();

	/**
	 * Gets the bridge
	 * 
	 * @return The bridge.
	 */
	IBridge getBridge();

	/**
	 * Sets a bridge
	 * 
	 * @param bridge
	 *            The bridge.
	 */
	void setBridge(final IBridge bridge);
}
