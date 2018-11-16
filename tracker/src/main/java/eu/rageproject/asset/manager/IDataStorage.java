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
 * Interface for data storage.
 * 
 * @author Ivan Martinez-Ortiz
 */
public interface IDataStorage {

	/**
	 * Deletes the given fileId.
	 * 
	 * @param fileId
	 *            The file identifier to delete.
	 * 
	 * @return {@code true} if it succeeds, {@code false} otherwise.
	 */
	public boolean delete(final String fileId);

	/**
	 * Checks if exits a file with <code>fileId</code> identifier.
	 * 
	 * @param fileId
	 *            The file identifier to check.
	 * 
	 * @return {@code true} if exits, {@code false} otherwise.
	 */
	public boolean exists(final String fileId);

	/**
	 * Get file identifiers stored in this storage.
	 * 
	 * @return An array of fileIds.
	 */
	public String[] files();

	/**
	 * Loads content of the <code>fileId</code>.
	 * 
	 * @param fileId
	 *            file identifier to load.
	 * 
	 * @return file contents or {@code null} if not exits.
	 */
	public String load(final String fileId);

	/**
	 * Saves the given file.
	 * 
	 * @param fileId
	 *            file identifier to save.
	 * @param fileData
	 *            file content to save.
	 */
	public void save(final String fileId, final String fileData);
}
