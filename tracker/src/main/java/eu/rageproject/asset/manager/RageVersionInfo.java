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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * Information about the rage version.
 * 
 * <p>
 * <strong>VERSION INFO EXAMPLE</strong>
 * </p>
 * {@code
   <version>
     <id>asset</id>
     <major>1</major>
     <minor>2</minor>
     <build>3</build>
     <revision></revision>
     <maturity>alpha</maturity>
     <dependencies>
       <depends minVersion = "1.2.3" > Logger </ depends >
     </dependencies >
    </version>
    }
 * 
 * @author Ivan Martinez-Ortiz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "version")
public class RageVersionInfo {

	@XmlElement(name = "id")
	private String id;

	@XmlElement(name = "major")
	private int major;

	@XmlElement(name = "minor")
	private int minor;

	@XmlElement(name = "build")
	private int build;

	@XmlElement(name = "revision", required=false)
	private int revision;

	@XmlElement(name = "maturity")
	private String maturity;

	@XmlElementWrapper(name = "dependencies")
	@XmlElement(name = "depends")
	private List<Dependency> dependencies;

	/**
	 * Initializes a new instance of the {@link #AssetManagerPackage
	 * .RageVersionInfo} class.
	 */
	public RageVersionInfo() {
		this.id = null;
		this.major = -1;
		this.minor = -1;
		this.build = -1;
		this.revision = -1;
		this.dependencies = new LinkedList<>();
	}

	/**
	 * Gets the identifier
	 *
	 * @return The identifier.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets an identifier
	 *
	 * @param id	The identifier.
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Gets the major
	 *
	 * @return The major.
	 */
	public int getMajor() {
		return major;
	}

	public void setMajor(final int major) {
		this.major = major;
	}

	/**
	 * Gets the minor
	 *
	 * @return The minor.
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * Sets a minor
	 *
	 * @param minor	The minor.
	 */
	public void setMinor(final int minor) {
		this.minor = minor;
	}

	/**
	 * Gets the build
	 *
	 * @return The build.
	 */
	public int getBuild() {
		return build;
	}

	/**
	 * Sets a build
	 *
	 * @param build	The build.
	 */
	public void setBuild(final int build) {
		this.build = build;
	}

	/**
	 * Gets the revision
	 *
	 * @return The revision.
	 */
	public int getRevision() {
		return revision;
	}

	/**
	 * Sets a revision
	 *
	 * @param revision	The revision.
	 */
	public void setRevision(final int revision) {
		this.revision = revision;
	}

	/**
	 * Gets the maturity
	 *
	 * @return The maturity.
	 */
	public String getMaturity() {
		return maturity;
	}

	public void setMaturity(final String maturity) {
		this.maturity = maturity;
	}

	/**
	 * Gets the dependencies
	 *
	 * @return The dependencies.
	 */
	public List<Dependency> getDependencies() {
		return dependencies;
	}

	/**
	 * Sets the dependencies
	 *
	 * @param dependencies	The dependencies.
	 */
	public void setDependencies(final List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	/**
	 * Loads version information..
	 *
	 * @param xml	The XML version representation.
	 *
	 * @return the loaded version info.
	 */
	public static RageVersionInfo loadVersionInfo(final String xml) {
		RageVersionInfo info = JAXB.unmarshal(new StringReader(xml), RageVersionInfo.class);
		return info;
	}

	/**
	 * Saves the version information.
	 *
	 * @return the XML version info.
	 */
	public String saveVersionInfo() {
		StringWriter buffer = new StringWriter();
		JAXB.marshal(this, buffer);
		return buffer.toString();
	}

	/**
	 * A dependency.
	 * 
	 * <p>
	 * <strong>DEPENDENCY EXAMPLE</strong>
	 * </p>
	 * {@code
	   <depends minVersion = "1.2.3" > Logger </ depends >
	    }
	 * 
	 * @author Ivan Martinez-Ortiz
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "depends")
	public static class Dependency {
		
		@XmlAttribute(name="minVersion", required=true)
		private String minVersion;
		
		@XmlAttribute(name="maxVersion", required=false)
		private String maxVersion;
		
		@XmlValue
		private String name;

		/**
		 * Initializes a new dependency
		 */
		public Dependency() {
		}

		/**
		 * Gets minimum version
		 *
		 * @return The minimum version.
		 */
		public String getMinVersion() {
			return minVersion;
		}

		/**
		 * Sets minimum version
		 *
		 * @param minVersion	The minimum version.
		 */
		public void setMinVersion(final String minVersion) {
			this.minVersion = minVersion;
		}

		/**
		 * Gets maximum version
		 *
		 * @return The maximum version.
		 */
		public String getMaxVersion() {
			return maxVersion;
		}

		/**
		 * Sets maximum version
		 *
		 * @param maxVersion	The maximum version.
		 */
		public void setMaxVersion(final String maxVersion) {
			this.maxVersion = maxVersion;
		}

		/**
		 * Gets the name
		 *
		 * @return The name.
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets a name
		 *
		 * @param name	The name.
		 */
		public void setName(final String name) {
			this.name = name;
		}
	}
	
	/**
	 * Convert this object into a string representation
	 *
	 * @return A String that represents this object.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.major).append(".").append(this.minor).append(".").append(this.build);
		if (this.revision >= 0) {
			builder.append(".").append(this.revision);
		}
		return builder.toString();
	}
}