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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <strong>IMPLEMENTATION NOTE</strong>
 *
 * This class might not be completely thread-safe.
 *
 * 1) Singleton (enum based) pattern implementation however is thread-safe, compiler enforced and correct.
 * 2) assets Map was changed into a ConcurrentMap (untested however).
 * 3) idGenerator is not thread safe.
 *
 * @author Ivan Martinez-Ortiz
 * @author Wim van der Vegt (ounl)
 */
public enum AssetManager {

	INSTANCE;

	public static AssetManager getInstance() {
		return AssetManager.INSTANCE;
	}

    private static final Pattern CLASS_PATTERN = Pattern.compile("^([^_]+)_\\d+$");

	private int idGenerator;

	private ILog logger = null;

	/**
	 * was Map
	 */
	private ConcurrentMap<String, IAsset> assets;

	private IBridge bridge;

	/**
	 * Avoid manual instantiation
	 */
	private AssetManager() {
		this.idGenerator = 0;
		/**
		 * was HashMap
		 */
		this.assets = new ConcurrentHashMap<>();
		initEventSystem();
	}

	/**
	 * Initializes the event system
	 */
	private void initEventSystem() {
		Messages.getInstance().define("EventSystem.Init");
		Messages.getInstance().broadcast("EventSystem.Init', 'hello event!");
	}

	/**
	 * Searches for the first asset by class
	 *
	 * @tparam T	Generic type parameter.
	 * @param clazz	The clazz.
	 *
	 * @return The found asset by class.
	 */
	@SuppressWarnings("unchecked")
	public <T> T findAssetByClass(final String clazz) {
		for (Map.Entry<String, IAsset> e : this.assets.entrySet()) {
			Matcher m = CLASS_PATTERN.matcher(e.getKey());
			if (m.matches() && m.group(1).equals(clazz)) {
				return (T) e.getValue();
			}
		}
		return null;
	}

	/**
	 * Searches for the first <code>Asset</code> by identifier.
	 *
	 * @param id
	 *            The asset identifier.
	 *
	 * @return return the <code>Asset</code> or <code>null</code> if the
	 *         <code>id</code> is not found.
	 */
	public IAsset findAssetById(final String id) {
		return this.assets.get(id);
	}

	/**
	 * Searches for assets by class.
	 *
	 * @param clazz	The class name.
	 *
	 * @return The found assets by class.
	 */
	public Iterable<IAsset> findAssetsByClass(final String clazz) {
		List<IAsset> results = new LinkedList<>();
		for (Map.Entry<String, IAsset> e : this.assets.entrySet()) {
			Matcher m = CLASS_PATTERN.matcher(e.getKey());
			if (m.matches() && m.group(1).equals(clazz)) {
				results.add(e.getValue());
			}
		}
		return results;
	}

	/**
	 * Registers the asset instance
	 *
	 * @param asset	The asset.
	 * @param clazz	The clazz.
	 *
	 * @return A String.
	 */
	public String registerAssetInstance(final IAsset asset, final String clazz) {
		for (Map.Entry<String, IAsset> e : assets.entrySet()) {
			if (e.getValue() == asset) {
				return e.getKey();
			}
		}

		String id;
		synchronized (this) {
			id = String.format("%s_%d", clazz, idGenerator++);
		}

		Log(Severity.Verbose,"Registering Asset %s/%s as %s", asset.getClassName(), clazz, id);

		assets.put(id, asset);

		Log(Severity.Verbose,"Registered %d Asset(s)", assets.size());

		return id;
	}

	/**
	 * Gets the bridge
	 *
	 * @return The bridge.
	 */
	public IBridge getBridge() {
		return bridge;
	}

	/**
	 * Sets a bridge
	 *
	 * @param bridge	The bridge.
	 */
	public void setBridge(final IBridge bridge) {
		this.bridge = bridge;
	}

	/**
	 * Logs a message using the Bridge.
	 *
	 * @param loglevel	The loglevel.
	 * @param format  	Describes the format to use.
	 * @param args	  	Variable arguments providing the arguments.
	 */
	public void Log(Severity loglevel, String format, Object... args) {
		Log(loglevel,String.format(format, args));
	}

	/**
	 * Logs a message using the Bridge.
	 *
	 * @param loglevel	The loglevel.
	 * @param msg	  	The message.
	 */
	public void Log(Severity loglevel, String msg) {
		logger = getInterface(ILog.class);

		if (logger != null) {
			logger.Log(loglevel, msg);
		}
	}

	/**
	 * Returns an object which is an instance of the given class associated with this object.
	 * Returns null if no such object can be found.
	 *
	 * @param parameter1	the adapter class to look up.
	 *
	 * @return a object of the given class, or null if this object does not have an adapter
	 * for the given class.
	 */
	@SuppressWarnings("unchecked")
	protected <T> T getInterface(final Class<T> adapter) {
		if (this.bridge != null && adapter.isAssignableFrom(this.bridge.getClass())) {
			return (T) this.bridge;
		}

		return null;
	}

	/**
	 * Reports version and dependencies.
	 *
	 * @return The version and dependencies report.
	 */
    public String getVersionAndDependenciesReport() {
            int col1w = 40;
            int col2w = 32;

            StringBuilder report = new StringBuilder();

            // Get system dependant end of line separators
            String eol = System.getProperty("line.separator");

            report.append(padRight("Asset", col1w - "Asset".length()));
            report.append(padRight("| Depends on", col2w)).append(eol);
            report.append(padRight("", col1w, '-'));
            report.append("+");
            report.append(padRight("", col2w-1, '-')).append(eol);

            for (Map.Entry<String, IAsset> e : this.assets.entrySet()) {
            	IAsset asset = e.getValue();
            	String artifact = String.format("%s v%s", asset.getClassName(), asset.getVersion());
                report.append(padRight(artifact, col1w - artifact.length()));

                int cnt = 0;
                for (Map.Entry<String, String> dependency : asset.getDependencies().entrySet()) {
                    //! Better version matches (see Microsoft).
                    //
                    //! https://msdn.microsoft.com/en-us/library/system.version(v=vs.110).aspx
                    //
                    //! dependency.value has min-max format (inclusive) like:
                    //
                    //? v1.2.3-*        (v1.2.3 or higher)
                    //? v0.0-*          (all versions)
                    //? v1.2.3-v2.2     (v1.2.3 or higher less than or equal to v2.1)
                    //
                	String depencyVersion = dependency.getValue();
                    String[] vrange = depencyVersion.split("-");

                    Version low = null;

                    Version hi = null;

                    switch (vrange.length) {
                        case 1:
                            low = new Version(vrange[0]);
                            hi = low;
                            break;
                        case 2:
                            low = new Version(vrange[0]);
                            if ("*".equals(vrange[1])) {
                                hi = new Version(99, 99);
                            } else {
                                hi = new Version(vrange[1]);
                            }
                            break;

                        default:
                            break;
                    }

                    Boolean found = false;

                    if (low != null) {
                        for (IAsset dep : findAssetsByClass(dependency.getKey())) {
                            Version vdep = new Version(dep.getVersion());
                            if (low.compareTo(vdep) <= 0 && vdep.compareTo(hi) <= 0) {
                                found = true;
                                break;
                            }
                        }

                        report.append(String.format("| %s v%s [%s]", dependency.getKey(), dependency.getValue(), found ? "resolved" : "missing")).append(eol);
                    } else {
                        report.append("error");
                    }

                    if (cnt != 0) {
                        report.append(padRight("", col1w - 1));
                    }

                    cnt++;
                }

                if (cnt == 0) {
                    report.append(String.format("| %s", "No dependencies")).append(eol);
                }
            }

            report.append(padRight("", col1w, '-'));
            report.append("+");
            report.append(padRight("", col2w-1, '-')).append(eol);

            return report.toString();
    }

	/**
	 * Pad right
	 *
	 * @param base	  	The base.
	 * @param quantity	The quantity.
	 *
	 * @return A String.
	 */
	private String padRight(final String base, final int quantity) {
		return padRight(base, quantity, ' ');
	}

	/**
	 * Pad right
	 *
	 * @param base		 	The base.
	 * @param quantity   	The quantity.
	 * @param paddingChar	The padding character.
	 *
	 * @return A String.
	 */
	private String padRight(final String base, final int quantity, final char paddingChar) {
		StringBuilder buffer = new StringBuilder(base.length() + quantity);
		buffer.append(base);
		for (int i = 0; i < quantity; i++) {
			buffer.append(paddingChar);
		}

		return buffer.toString();
	}

	/// <summary>
	/// Clears the registration.
	// This method can be used in test units to reset and
	/// </summary>
	/// <remarks>Used for cleaning up in test suites (as static readonly _instance member cannot be destroyed).</remarks>
	@SuppressWarnings("unused")
	private void ClearRegistration()
	{
		idGenerator = 0;

		assets.clear();
	}

}
