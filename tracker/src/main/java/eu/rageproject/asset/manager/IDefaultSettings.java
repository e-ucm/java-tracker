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

/**
 * Interface for default settings.
 * <p>
 * This Interface is used to:
 * </p>
 * <ul>
 * <li>Check if an asset has default (application) settings that override
 * build-in default settings.</li>
 * <li>Load these settings from the game environment.</li>
 * <li>In certain environments write the actual settings as application
 * defaults. This could for instance be Unity in editor mode.&lt;</li>
 * </ul>
 * 
 * Default settings and application default settings are read-only at run-time.
 * If modification and storage is needed at run-time, the {@link #IDataStorage}
 * interface could be used i.c.m. {@link #ISettings} methods.
 */
public interface IDefaultSettings {

	/**
	 * Check if a {@code clazz} with {@code id} has default settings.
	 * 
	 * @param clazz
	 *            The classname.
	 * @param id
	 *            The identifier.
	 * 
	 * @return {@code true} if default settings, {@code false} otherwise.
	 */
	boolean hasDefaultSettings(final String clazz, final String id);

	/**
	 * Loads default settings for a {@code clazz} with {@code id}.
	 * 
	 * @param clazz
	 *            The classname.
	 * @param id
	 *            The identifier.
	 * 
	 * @return The default settings.
	 */
	String loadDefaultSettings(final String clazz, final String id);

	/**
	 * Saves a default settings for a {@code clazz} with {@code id}.
	 * 
	 * @param Class
	 *            The classname.
	 * @param Id
	 *            The identifier.
	 * @param fileData
	 *            Data to save.
	 */
	void saveDefaultSettings(final String Class, final String Id,
			final String fileData);
}
