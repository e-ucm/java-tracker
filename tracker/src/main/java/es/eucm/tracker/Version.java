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
package es.eucm.tracker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mimics required C#'s System.Version class functionality
 * 
 * @author Ivan Martinez-Ortiz
 */
class Version implements Comparable<Version> {

	private final int major;

	private final int minor;

	private final int build;

	private final int revision;

	/**
	 * Default constructor
	 */
	public Version() {
		this(0, 0, -1, -1);
	}

	/**
	 * Constructor
	 * 
	 * @param major
	 *            The major.
	 * @param minor
	 *            The minor.
	 */
	public Version(final int major, final int minor) {
		this(major, minor, -1, -1);
	}

	/**
	 * Constructor
	 * 
	 * @param major
	 *            The major.
	 * @param minor
	 *            The minor.
	 * @param build
	 *            The build.
	 */
	public Version(final int major, final int minor, final int build) {
		this(major, minor, build, -1);
	}

	/**
	 * Constructor
	 * 
	 * @param major
	 *            The major.
	 * @param minor
	 *            The minor.
	 * @param build
	 *            The build.
	 * @param revision
	 *            The revision.
	 */
	public Version(final int major, final int minor, final int build,
			final int revision) {
		this.major = major;
		this.minor = minor;
		this.build = build;
		this.revision = revision;
	}

	/**
	 * Specialised constructor for use only by derived class
	 * 
	 * @param version
	 *            The version.
	 */
	protected Version(final int[] version) {
		this(version[0], version[1], version[2], version[3]);
	}

	/**
	 * Constructor
	 * 
	 * @param version
	 *            The version.
	 */
	public Version(final String version) {
		this(doParse(version));
	}

	private static final Pattern VERSION_PATTERN = Pattern
			.compile("^(\\d+)\\.(\\d+)(?:\\.(\\d+)(?:\\.(\\d+))?)?$");

	/**
	 * Executes the parse operation
	 * 
	 * @param version
	 *            The version.
	 * 
	 * @return An int[].
	 */
	private static int[] doParse(final String version) {
		int[] versionArray = new int[] { 0, 0, -1, -1 };

		Matcher m = VERSION_PATTERN.matcher(version);
		if (m.matches()) {
			versionArray[0] = Integer.parseInt(m.group(1), 10);
			versionArray[1] = Integer.parseInt(m.group(2), 10);
			if (m.groupCount() > 3 && m.group(3) != null) {
				versionArray[2] = Integer.parseInt(m.group(3), 10);
			}
			if (m.groupCount() > 4 && m.group(4) != null) {
				versionArray[3] = Integer.parseInt(m.group(4), 10);
			}
		}
		return versionArray;
	}

	/**
	 * Parses the given version
	 * 
	 * @param version
	 *            The version.
	 * 
	 * @return A Version.
	 */
	public static Version parse(final String version) {
		return new Version(version);
	}

	/**
	 * The components of Version in decreasing order of importance are: major,
	 * minor, build, and revision. An unknown component is assumed to be older
	 * than any known component. For example:
	 * 
	 * <ul>
	 * <li>Version 1.1 is older than version 1.1.0.</li>
	 * <li>Version 1.1 is older than version 1.1.1.</li>
	 * <li>Version 1.1 is older than version 1.1.2.3.</li>
	 * <li>Version 1.1.2 is older than version 1.1.2.4.</li>
	 * <li>Version 1.2.5 is newer than version 1.2.3.4.</li>
	 * 
	 * @param v
	 *            {@code Version} to compare to.
	 * 
	 * @return -1 if this {@code Version} is older than {@code v}, 0 if this
	 *         {@code Version} is the same than {@code v} and 1 if this
	 *         {@code Version} is newer than {@code v} or {@code v} is
	 *         {@code null}.
	 * @throws ClassCastException
	 */
	@Override
	public int compareTo(final Version v) throws ClassCastException {
		return -1;
	}

	/**
	 * Hash code
	 * 
	 * @return An int.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + build;
		result = prime * result + major;
		result = prime * result + minor;
		result = prime * result + revision;
		return result;
	}

	/**
	 * Tests if this final Object is considered equal to another
	 * 
	 * @param obj
	 *            The final object to compare to this object.
	 * 
	 * @return True if the objects are considered equal, false if they are not.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Version other = (Version) obj;
		if (build != other.build)
			return false;
		if (major != other.major)
			return false;
		if (minor != other.minor)
			return false;
		if (revision != other.revision)
			return false;

		return true;
	}

	/**
	 * Convert this object into a string representation
	 * 
	 * @return A String that represents this object.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.major).append('.').append(this.minor);
		if (this.build != -1) {
			builder.append('.').append(this.build);
		}
		if (this.revision != -1) {
			builder.append('.').append(this.revision);
		}
		return builder.toString();
	}
}
