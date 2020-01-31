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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

import es.eucm.tracker.exceptions.KeyExtensionException;
import es.eucm.tracker.exceptions.TargetXApiException;
import es.eucm.tracker.exceptions.TraceException;
import es.eucm.tracker.exceptions.TrackerException;
import es.eucm.tracker.exceptions.ValueExtensionException;
import es.eucm.tracker.exceptions.VerbXApiException;
import eu.rageproject.asset.manager.AssetManager;
import eu.rageproject.asset.manager.IAppend;
import eu.rageproject.asset.manager.IBridge;
import eu.rageproject.asset.manager.IDataStorage;
import eu.rageproject.asset.manager.ILog;

/**
 * Tests the {@link es.eucm.tracker.TrackerAsset}
 */
public class TrackerAssetTest {

	private static final Gson gson = new Gson();
	private static final ArrayList<HashMap<String, Object>> arraymap = new ArrayList<>();

	private static final CsvTrackerEventUnmarshaller CSV_UNMARSHALLER = new CsvTrackerEventUnmarshaller();

	TrackerAssetSettings settings = new TrackerAssetSettings();
	IDataStorage storage;
	IAppend append_storage;
	ILog log;
	TesterBridge bridge;

	private void initTracker(String format) throws Exception {
		initTracker(format, TrackerAssetSettings.StorageTypes.LOCAL, null);
	}

	private void initTracker(String format,
			TrackerAssetSettings.StorageTypes st, TesterBridge bridge)
			throws Exception {
		TrackerAsset.getInstance().stop();
		Path current = Paths.get(System.getProperty("user.dir"));

		settings.setTraceFormat(
				TrackerAssetSettings.TraceFormats.valueOf(format));

		settings.setStorageType(st);
		TrackerAsset.getInstance().setSettings(settings);
		if (bridge != null)
			this.bridge = bridge;
		else
			this.bridge = new TesterBridge();
		TrackerAsset.getInstance().setBridge(this.bridge);
		storage = getInterface(IDataStorage.class);
		append_storage = getInterface(IAppend.class);
		log = (ILog) TrackerAsset.getInstance().getBridge();
		TrackerUtils.setStrictMode(true);
		TrackerAsset.getInstance().clear();
		TrackerAsset.getInstance().start();
	}

	@Test
	public void testNullImputs() throws Exception {
		initTracker("XAPI");
		Exception exception = null;

		try {
			TrackerAsset.getInstance().trace(null, "Type", "ID");
		} catch (TraceException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().trace("Verb", null, "ID");
		} catch (TraceException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().trace("Verb", "Type", null);
		} catch (TraceException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;

		try {
			TrackerAsset.getInstance().trace("", "Type", "ID");
		} catch (TraceException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().trace("Verb", "", "ID");
		} catch (TraceException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().trace("Verb", "Type", "");
		} catch (TraceException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;

		try {
			TrackerAsset.getInstance().trace("Verb", "Type", "ID");
		} catch (VerbXApiException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;

		try {
			TrackerAsset.getInstance().getCompletable().initialized(null);
		} catch (TargetXApiException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().getCompletable().progressed(null, 0.1f);
		} catch (TargetXApiException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().getCompletable().completed(null);
		} catch (TargetXApiException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;

		try {
			TrackerAsset.getInstance().getAccessible().accessed(null);
		} catch (TargetXApiException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().getAccessible().skipped(null);
		} catch (TargetXApiException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;

		try {
			TrackerAsset.getInstance().getAlternative().selected(null, null);
		} catch (TargetXApiException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().getAlternative().selected(null, "o");
		} catch (TargetXApiException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().getAlternative().selected("k", null);
		} catch (ValueExtensionException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().getAlternative().unlocked(null, null);
		} catch (TargetXApiException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().getAlternative().unlocked(null, "o");
		} catch (TargetXApiException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().getAlternative().unlocked("k", null);
		} catch (ValueExtensionException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;

		try {
			TrackerAsset.getInstance().getGameObject().interacted(null);
		} catch (TargetXApiException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().getGameObject().used(null);
		} catch (TargetXApiException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;

		try {
			TrackerAsset.getInstance().setVar("", "");
		} catch (KeyExtensionException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().setVar(null, "v");
		} catch (KeyExtensionException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().setVar("k", "");
		} catch (ValueExtensionException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;

		try {
			TrackerAsset.getInstance().setVar("k", "v");
		} catch (Exception e) {
			exception = e;
		}
		;
		assertNull(exception);
	}

	@Test
	public void testObsoleteMethods() throws Exception {
		initTracker("XAPI");
		Exception exception = null;

		exception = null;
		try {
			TrackerAsset.getInstance().trace("1", "2", null);
		} catch (TraceException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().trace("1", "2", "");
		} catch (TraceException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().trace("", "", "");
		} catch (TraceException e) {
			exception = e;
		}
		;
		assertNotNull(exception);

		exception = null;

		initTracker("CSV");
		TrackerUtils.setStrictMode(false);

		exception = null;
		initTracker("CSV");
		TrackerUtils.setStrictMode(true);
		try {
			TrackerAsset.getInstance().setVar("k", "");
		} catch (ValueExtensionException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
		exception = null;

		try {
			TrackerAsset.getInstance().setVar("k", 1);
		} catch (Exception e) {
			exception = e;
		}
		;
		assertNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().setVar("k", 1.1f);
		} catch (Exception e) {
			exception = e;
		}
		;
		assertNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().setVar("k", 1.1d);
		} catch (Exception e) {
			exception = e;
		}
		;
		assertNull(exception);
		exception = null;
		try {
			TrackerAsset.getInstance().setVar("k", "v");
		} catch (Exception e) {
			exception = e;
		}
		;
		assertNull(exception);
		exception = null;
	}

	@Test
	public void alternativeTraceTest() throws Exception {
		initTracker("CSV");
		TrackerAsset.getInstance().getAlternative().selected("question",
				"alternative");
		checkCSVTrace("0,selected,alternative,question,response,alternative");
	}

	@Test
	public void testTrace_Generic_Csv_Stored_01() throws Exception {
		initTracker("CSV");
		enqueueTrace01();
		TrackerAsset.getInstance().flush();
		checkCSVStoredTrace("0,accessed,gameobject,ObjectID");
	}

	@Test
	public void testTrace_Generic_Csv_Stored_02() throws Exception {
		initTracker("CSV");
		enqueueTrace02();
		TrackerAsset.getInstance().flush();
		checkCSVStoredTrace(
				"0,initialized,game,ObjectID2,response,TheResponse,score,0.123");
	}

	@Test
	public void testTrace_Generic_Csv_Stored_03() throws Exception {
		initTracker("CSV");
		enqueueTrace03();
		TrackerAsset.getInstance().flush();
		checkCSVStoredTrace(
				"0,selected,zone,ObjectID3,success,false,completion,true,response,AnotherResponse,score,123.456,extension1,value1,extension2,value2,extension3,3,extension4,4.56");
	}

	@Test
	public void testTrace_Generic_XApi_Stored_01() throws Exception {
		cleanStorage();
		initTracker("XAPI");
		enqueueTrace01();
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(file.size() - 1);
		assertEquals(tracejson.entrySet().size(), 4);
		assertEquals(((Map) tracejson.get("object")).get("id"), "ObjectID");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/game-object");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"https://w3id.org/xapi/seriousgames/verbs/accessed");
	}

	@Test
	public void testTrace_Generic_XApi_Stored_02() throws Exception {
		cleanStorage();
		initTracker("XAPI");
		enqueueTrace02();
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(file.size() - 1);
		assertEquals(tracejson.entrySet().size(), 5);
		assertEquals(((Map) tracejson.get("object")).get("id"), "ObjectID2");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/serious-game");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"http://adlnet.gov/expapi/verbs/initialized");
		assertEquals(((Map<?, ?>) tracejson.get("result")).size(), 2);
		assertEquals(((Map) tracejson.get("result")).get("response"),
				"TheResponse");
		assertEquals(
				((Map) ((Map) tracejson.get("result")).get("score")).get("raw"),
				0.123);
	}

	@Test
	public void testTrace_Generic_XApi_Stored_03() throws Exception {
		cleanStorage();
		initTracker("XAPI");
		enqueueTrace03();
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(file.size() - 1);
		assertEquals(tracejson.entrySet().size(), 5);
		assertEquals(((Map) tracejson.get("object")).get("id"), "ObjectID3");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/zone");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"https://w3id.org/xapi/adb/verbs/selected");
		assertEquals(((Map) tracejson.get("result")).size(), 5);
		assertEquals(((Map) tracejson.get("result")).get("response"),
				"AnotherResponse");
		assertEquals(
				((Map) ((Map) tracejson.get("result")).get("score")).get("raw"),
				123.456);
		assertEquals(((Map) tracejson.get("result")).get("completion"), true);
		assertEquals(((Map) tracejson.get("result")).get("success"), false);
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions"))
				.entrySet().size(), 4);
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions"))
				.get("extension1"), "value1");
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions"))
				.get("extension2"), "value2");
		// TODO should be 3, not 3.0
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions"))
				.get("extension3"), 3.0);
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions"))
				.get("extension4"), 4.56);
	}

	@Test
	public void testTrace_Generic_XApi_All() throws Exception {
		cleanStorage();
		initTracker("XAPI");
		enqueueTrace01();
		enqueueTrace02();
		enqueueTrace03();
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(0);
		assertEquals(tracejson.entrySet().size(), 4);
		assertEquals(((Map) tracejson.get("object")).get("id"), "ObjectID");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/game-object");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"https://w3id.org/xapi/seriousgames/verbs/accessed");
		// CHECK THE 2ND TRACE
		tracejson = (Map) file.get(1);
		assertEquals(tracejson.entrySet().size(), 5);
		assertEquals(((Map) tracejson.get("object")).get("id"), "ObjectID2");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/serious-game");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"http://adlnet.gov/expapi/verbs/initialized");
		assertEquals(((Map) tracejson.get("result")).size(), 2);
		assertEquals(((Map) tracejson.get("result")).get("response"),
				"TheResponse");
		assertEquals(
				((Map) ((Map) tracejson.get("result")).get("score")).get("raw"),
				0.123);
		// CHECK THE 3RD TRACE
		tracejson = (Map) file.get(2);
		assertEquals(tracejson.entrySet().size(), 5);
		assertEquals(((Map) tracejson.get("object")).get("id"), "ObjectID3");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/zone");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"https://w3id.org/xapi/adb/verbs/selected");
		assertEquals(((Map) tracejson.get("result")).size(), 5);
		assertEquals(((Map) tracejson.get("result")).get("response"),
				"AnotherResponse");
		assertEquals(
				((Map) ((Map) tracejson.get("result")).get("score")).get("raw"),
				123.456);
		assertEquals(((Map) tracejson.get("result")).get("completion"), true);
		assertEquals(((Map) tracejson.get("result")).get("success"), false);
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions"))
				.entrySet().size(), 4);
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions"))
				.get("extension1"), "value1");
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions"))
				.get("extension2"), "value2");
		// TODO should be 3, not 3.0
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions"))
				.get("extension3"), 3.0);
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions"))
				.get("extension4"), 4.56);
	}

	@Test
	public void testAccesible_Csv_01() throws Exception {
		initTracker("CSV");
		TrackerAsset.getInstance().getAccessible().accessed("AccesibleID",
				AccessibleTracker.Accessible.Cutscene);
		checkCSVTrace("0,accessed,cutscene,AccesibleID");
	}

	@Test
	public void testAccesible_Csv_02_WithExtensions() throws Exception {
		initTracker("CSV");
		TrackerAsset.getInstance().setVar("extension1", "value1");
		TrackerAsset.getInstance().getAccessible().skipped("AccesibleID2",
				AccessibleTracker.Accessible.Screen);
		checkCSVTrace("0,skipped,screen,AccesibleID2,extension1,value1");
	}

	@Test
	public void testAccesible_XApi_01() throws Exception {
		cleanStorage();
		initTracker("XAPI");
		TrackerAsset.getInstance().getAccessible().accessed("AccesibleID",
				AccessibleTracker.Accessible.Cutscene);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(0);
		assertEquals(tracejson.entrySet().size(), 4);
		assertEquals(((Map) tracejson.get("object")).get("id"), "AccesibleID");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/cutscene");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"https://w3id.org/xapi/seriousgames/verbs/accessed");
	}

	@Test
	public void testAccesible_XApi_02_WithExtensions() throws Exception {
		cleanStorage();
		initTracker("XAPI");
		TrackerAsset.getInstance().setVar("extension1", "value1");
		TrackerAsset.getInstance().getAccessible().skipped("AccesibleID2",
				AccessibleTracker.Accessible.Screen);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(0);
		assertEquals(tracejson.entrySet().size(), 5);
		assertEquals(((Map) tracejson.get("object")).get("id"), "AccesibleID2");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/screen");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"http://id.tincanapi.com/verb/skipped");
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions"))
				.get("extension1"), "value1");
	}

	@Test
	public void testAlternative_Csv_01() throws Exception {
		initTracker("CSV");
		TrackerAsset.getInstance().getAlternative().selected("AlternativeID",
				"SelectedOption", AlternativeTracker.Alternative.Path);
		checkCSVTrace("0,selected,path,AlternativeID,response,SelectedOption");
	}

	@Test
	public void testAlternative_Csv_02_WithExtensions() throws Exception {
		initTracker("CSV");
		TrackerAsset.getInstance().setVar("SubCompletableScore", 0.8);
		TrackerAsset.getInstance().getAlternative().unlocked("AlternativeID2",
				"Answer number 3", AlternativeTracker.Alternative.Question);
		checkCSVTrace(
				"0,unlocked,question,AlternativeID2,response,Answer number 3,SubCompletableScore,0.8");
	}

	public void testAlternative_XApi_01() throws Exception {
		cleanStorage();
		initTracker("XAPI");
		TrackerAsset.getInstance().getAlternative().selected("AlternativeID",
				"SelectedOption", AlternativeTracker.Alternative.Path);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(0);
		assertEquals(tracejson.entrySet().size(), 5);
		assertEquals(((Map) tracejson.get("object")).get("id"),
				"AlternativeID");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/path");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"https://w3id.org/xapi/adb/verbs/selected");
		assertEquals(((Map) tracejson.get("result")).get("response"),
				"SelectedOption");
	}

	@Test
	public void testAlternative_XApi_02_WithExtensions() throws Exception {
		cleanStorage();
		initTracker("XAPI");
		TrackerAsset.getInstance().setVar("SubCompletableScore", 0.8);
		TrackerAsset.getInstance().getAlternative().unlocked("AlternativeID2",
				"Answer number 3", AlternativeTracker.Alternative.Question);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(0);
		assertEquals(tracejson.entrySet().size(), 5);
		assertEquals(((Map) tracejson.get("object")).get("id"),
				"AlternativeID2");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"http://adlnet.gov/expapi/activities/question");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"https://w3id.org/xapi/seriousgames/verbs/unlocked");
		assertEquals(((Map) tracejson.get("result")).get("response"),
				"Answer number 3");
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions"))
				.get("SubCompletableScore"), 0.8);
	}

	@Test
	public void testCompletable_Csv_01() throws Exception {
		initTracker("CSV");
		TrackerAsset.getInstance().getCompletable().initialized("CompletableID",
				CompletableTracker.Completable.Quest);
		checkCSVTrace("0,initialized,quest,CompletableID");
	}

	@Test
	public void testCompletable_Csv_02() throws Exception {
		initTracker("CSV");
		TrackerAsset.getInstance().getCompletable().progressed("CompletableID2",
				CompletableTracker.Completable.Stage, 0.34f);
		checkCSVTrace("0,progressed,stage,CompletableID2,progress,0.34");
	}

	@Test
	public void testCompletable_Csv_03() throws Exception {
		initTracker("CSV");
		TrackerAsset.getInstance().getCompletable().completed("CompletableID3",
				CompletableTracker.Completable.Race, true, 0.54f);
		checkCSVTrace(
				"0,completed,race,CompletableID3,success,true,score,0.54");
	}

	@Test
	public void testCompletable_XApi_01() throws Exception {
		cleanStorage();
		initTracker("XAPI");
		TrackerAsset.getInstance().getCompletable().initialized("CompletableID",
				CompletableTracker.Completable.Quest);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(0);
		assertEquals(tracejson.entrySet().size(), 4);
		assertEquals(((Map) tracejson.get("object")).get("id"),
				"CompletableID");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/quest");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"http://adlnet.gov/expapi/verbs/initialized");
	}

	@Test
	public void testCompletable_XApi_02() throws Exception {
		cleanStorage();
		initTracker("XAPI");
		TrackerAsset.getInstance().getCompletable().progressed("CompletableID2",
				CompletableTracker.Completable.Stage, 0.34f);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(0);
		assertEquals(tracejson.entrySet().size(), 5);
		assertEquals(((Map) tracejson.get("object")).get("id"),
				"CompletableID2");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/stage");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"http://adlnet.gov/expapi/verbs/progressed");
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions"))
				.get("https://w3id.org/xapi/seriousgames/extensions/progress"),
				0.34);
	}

	public void testCompletable_XApi_03() throws Exception {
		cleanStorage();
		initTracker("XAPI");
		TrackerAsset.getInstance().getCompletable().completed("CompletableID3",
				CompletableTracker.Completable.Race, true, 0.54f);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(0);
		assertEquals(tracejson.entrySet().size(), 5);
		assertEquals(((Map) tracejson.get("object")).get("id"),
				"CompletableID3");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/race");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"http://adlnet.gov/expapi/verbs/completed");
		assertEquals(((Map) tracejson.get("result")).get("success"), true);
		assertEquals(
				((Map) ((Map) tracejson.get("result")).get("score")).get("raw"),
				0.54);
	}

	@Test
	public void testGameObject_Csv_01() throws Exception {
		initTracker("CSV");
		TrackerAsset.getInstance().getGameObject().interacted("GameObjectID",
				GameObjectTracker.TrackedGameObject.Npc);
		checkCSVTrace("0,interacted,npc,GameObjectID");
	}

	@Test
	public void testGameObject_Csv_02() throws Exception {
		initTracker("CSV");
		TrackerAsset.getInstance().getGameObject().used("GameObjectID2",
				GameObjectTracker.TrackedGameObject.Item);
		checkCSVTrace("0,used,item,GameObjectID2");
	}

	@Test
	public void testGameObject_XApi_01() throws Exception {
		cleanStorage();
		initTracker("XAPI");
		TrackerAsset.getInstance().getGameObject().interacted("GameObjectID",
				GameObjectTracker.TrackedGameObject.Npc);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		ArrayList file;
		try {
			file = gson.fromJson(text, ArrayList.class);
		} catch (Exception e) {
			System.err.println(text);
			fail("Error parsing json: " + e);
			throw e;
		}
		Map tracejson = (Map) file.get(0);
		for (Map.Entry<String, Object> e : ((Map<String, Object>) tracejson)
				.entrySet()) {
			System.err.println(e.getKey() + " -> " + e.getValue());
		}
		assertEquals(tracejson.entrySet().size(), 4);
		assertEquals(((Map) tracejson.get("object")).get("id"), "GameObjectID");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/non-player-character");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"http://adlnet.gov/expapi/verbs/interacted");
	}

	@Test
	public void testGameObject_XApi_02() throws Exception {
		cleanStorage();
		initTracker("XAPI");
		TrackerAsset.getInstance().getGameObject().used("GameObjectID2",
				GameObjectTracker.TrackedGameObject.Item);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(0);
		assertEquals(tracejson.entrySet().size(), 4);
		assertEquals(((Map) tracejson.get("object")).get("id"),
				"GameObjectID2");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/item");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"https://w3id.org/xapi/seriousgames/verbs/used");
	}

	private void enqueueTrace01() throws Exception {
		TrackerAsset.getInstance().trace("accessed", "gameobject", "ObjectID");
	}

	private void enqueueTrace02() throws Exception {
		TrackerAsset.getInstance().setResponse("TheResponse");
		TrackerAsset.getInstance().setScore(0.123f);
		TrackerAsset.getInstance().trace("initialized", "game", "ObjectID2");
	}

	private void enqueueTrace03() throws Exception {
		TrackerAsset.getInstance().setResponse("AnotherResponse");
		TrackerAsset.getInstance().setScore(123.456f);
		TrackerAsset.getInstance().setSuccess(false);
		TrackerAsset.getInstance().setCompletion(true);
		TrackerAsset.getInstance().setVar("extension1", "value1");
		TrackerAsset.getInstance().setVar("extension2", "value2");
		TrackerAsset.getInstance().setVar("extension3", 3);
		TrackerAsset.getInstance().setVar("extension4", 4.56f);
		TrackerAsset.getInstance().trace("selected", "zone", "ObjectID3");
	}

	private void checkCSVTrace(String trace) throws Exception {
		// TODO: this method should access the queue directly.
		TrackerAsset.getInstance().flush();
		checkCSVStoredTrace(trace);
	}

	private void checkCSVStoredTrace(String... traces) throws Exception {
		String[] lines = storage.load(settings.getLogFile())
				.split(TrackerEventMarshaller.LINE_SEPARATOR);
		List<TrackerEvent> actualEvents = new LinkedList<>();
		for (String l : lines) {
			actualEvents.add(CSV_UNMARSHALLER.unmarshal(l));
		}

		List<TrackerEvent> expectedEvents = new LinkedList<>();
		for (String l : traces) {
			expectedEvents.add(CSV_UNMARSHALLER.unmarshal(l));
		}

		assertEquals(expectedEvents.size(), actualEvents.size());

		for (TrackerEvent e1 : expectedEvents) {
			Iterator<TrackerEvent> it = actualEvents.iterator();
			boolean found = false;
			while (!found && it.hasNext()) {
				found = TrackerEvent.TIMESTAMP_IGNORED_COMPARATOR.compare(e1,
						it.next()) == 0;
			}
			assertTrue("Events are different", found);
		}

	}

	private void checkXAPIStoredTrace(String trace, String file)
			throws Exception {
		if ((file.equals("")))
			file = settings.getLogFile();

		String[] lines = storage.load(file)
				.split(TrackerEventMarshaller.LINE_SEPARATOR);
		String traceWithoutTimestamp = removeTimestamp(lines[lines.length - 1]);
		compareCSV(traceWithoutTimestamp, trace);
	}

	private void compareCSV(String t1, String t2) throws Exception {
		List<String> sp1 = TrackerUtils.parseCSV(t1);
		List<String> sp2 = TrackerUtils.parseCSV(t2);
		assertEquals(sp1.size(), sp2.size());
		for (int i = 0; i < 3; i++)
			Assert.assertEquals(sp1.get(i), sp2.get(i));
		Map<String, String> d1 = new HashMap<>();
		if (sp1.size() > 3) {
			for (int i = 3; i < sp1.size(); i += 2) {
				d1.put(sp1.get(i), sp1.get(i + 1));
			}
			for (int i = 3; i < sp2.size(); i += 2) {
				Assert.assertTrue(d1.containsKey(sp2.get(i)));
				assertEquals(d1.get(sp2.get(i)), sp2.get(i + 1));
			}
		}

	}

	private String removeTimestamp(String trace) throws Exception {
		return trace.substring(trace.indexOf(',') + 1);
	}

	private void cleanStorage() throws Exception {
		if (settings != null && storage != null && settings.getLogFile() != null
				&& storage.exists(settings.getLogFile())) {
			storage.delete(settings.getLogFile());
		}

	}

	@Test
	public void testTraceSendingSync() throws Exception {
		initTracker("XAPI", TrackerAssetSettings.StorageTypes.NET, null);
		storage.delete("netstorage");
		enqueueTrace01();
		TrackerAsset.getInstance().flush();
		String text = storage.load("netstorage");

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(0);
		assertEquals(tracejson.entrySet().size(), 4);
		assertEquals(((Map) tracejson.get("object")).get("id"),
				"http://a2:3000/api/proxy/gleaner/games/5a26cb5ac8b102008b41472a/5a26cb5ac8b102008b41472b/ObjectID");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/game-object");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"https://w3id.org/xapi/seriousgames/verbs/accessed");

		append("netstorage", ",");
		enqueueTrace02();
		enqueueTrace03();
		TrackerAsset.getInstance().flush();
		text = storage.load("netstorage");
		text = "[" + text + "]";
		file = gson.fromJson(text, arraymap.getClass());
		assertEquals(file.size(), 2);
		assertEquals(((ArrayList) file.get(0)).size(), 1);
		assertEquals(((ArrayList) file.get(1)).size(), 2);
	}

	@Test
	public void testBackupSync() throws Exception {
		if (storage != null)
			storage.delete(settings.getBackupFile());

		testTraceSendingSync();
		String text = storage.load(settings.getBackupFile());
		String[] file = text.split("\n");
		assertEquals(file.length, 3);
	}

	@Test
	public void testTraceSending_IntermitentConnection() throws Exception {
		initTracker("XAPI", TrackerAssetSettings.StorageTypes.NET, null);
		storage.delete("netstorage");
		enqueueTrace01();
		TrackerAsset.getInstance().flush();
		String text = storage.load("netstorage");

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(0);
		assertEquals(tracejson.entrySet().size(), 4);
		assertEquals(((Map) tracejson.get("object")).get("id"),
				"http://a2:3000/api/proxy/gleaner/games/5a26cb5ac8b102008b41472a/5a26cb5ac8b102008b41472b/ObjectID");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/game-object");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"https://w3id.org/xapi/seriousgames/verbs/accessed");
		bridge.setConnected(false);
		enqueueTrace02();
		enqueueTrace03();
		TrackerAsset.getInstance().flush();
		text = storage.load("netstorage");
		file = gson.fromJson(text, arraymap.getClass());
		assertEquals(file.size(), 1);
		bridge.setConnected(true);
		append("netstorage", ",");
		TrackerAsset.getInstance().flush();
		text = storage.load("netstorage");
		text = "[" + text + "]";
		file = gson.fromJson(text, arraymap.getClass());
		assertEquals(file.size(), 2);
		assertEquals(((ArrayList) file.get(0)).size(), 1);
		assertEquals(((ArrayList) file.get(1)).size(), 2);
	}

	@Test
	public void testBackupSync_IntermitentConnection() throws Exception {
		initTracker("XAPI", TrackerAssetSettings.StorageTypes.NET, null);
		storage.delete("netstorage");
		storage.delete(settings.getBackupFile());
		enqueueTrace01();
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getBackupFile());
		String[] file = text.split("\n");
		assertEquals(file.length, 1);
		bridge.setConnected(false);
		enqueueTrace02();
		enqueueTrace03();
		TrackerAsset.getInstance().flush();
		text = storage.load(settings.getBackupFile());
		file = text.split("\n");
		assertEquals(file.length, 3);
		bridge.setConnected(true);
		TrackerAsset.getInstance().flush();
		text = storage.load(settings.getBackupFile());
		file = text.split("\n");
		assertEquals(file.length, 3);
	}

	@Test
	public void testTraceSending_WithoutStart() throws Exception {
		TrackerAsset.getInstance().stop();

		Exception exception = null;
		try {
			TrackerAsset.getInstance().getAccessible().accessed("Exception");
		} catch (TrackerException e) {
			exception = e;
		}
		;
		assertNotNull(exception);
	}

	@Test
	public void testTraceSendingStartFailed() throws Exception {
		TrackerAsset.getInstance().stop();
		bridge = new TesterBridge();
		bridge.setConnected(false);
		initTracker("XAPI", TrackerAssetSettings.StorageTypes.NET, bridge);
		storage.delete("netstorage");
		storage.delete(settings.getBackupFile());

		Exception exception = null;
		try {
			enqueueTrace01();
		} catch (Exception e) {
			exception = e;
		}
		;
		assertNull(exception);

		TrackerAsset.getInstance().flush();
		assertEquals(storage.load("netstorage"), "");
		assertNotEquals(storage.load(settings.getBackupFile()), "");
		bridge.setConnected(true);
		enqueueTrace02();
		enqueueTrace03();
		TrackerAsset.getInstance().flush();
		String text = storage.load("netstorage");
		text = text.replace("][", "],[");
		text = "[" + text + "]";

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		file = gson.fromJson(text, arraymap.getClass());
		assertEquals(file.size(), 2);
		assertEquals(((ArrayList) file.get(0)).size(), 1);
		assertEquals(((ArrayList) file.get(1)).size(), 2);

		Map<String, Object> tracejson = (Map) ((ArrayList) file.get(0)).get(0);
		assertEquals(tracejson.entrySet().size(), 4);
		assertEquals(((Map) tracejson.get("object")).get("id"),
				"http://a2:3000/api/proxy/gleaner/games/5a26cb5ac8b102008b41472a/5a26cb5ac8b102008b41472b/ObjectID");
		assertEquals(
				((Map) ((Map) tracejson.get("object")).get("definition"))
						.get("type"),
				"https://w3id.org/xapi/seriousgames/activity-types/game-object");
		assertEquals(((Map) tracejson.get("verb")).get("id"),
				"https://w3id.org/xapi/seriousgames/verbs/accessed");
		text = storage.load(settings.getBackupFile());
		String[] backup = text.split("\n");
		assertEquals(backup.length, 3);
	}

	@Test
	public void testEmptyQueueFlush() throws Exception {
		TrackerAsset.getInstance().stop();
		bridge = new TesterBridge();
		bridge.setConnected(false);
		initTracker("CSV", TrackerAssetSettings.StorageTypes.NET, bridge);
		storage.delete("netstorage");
		storage.delete(settings.getBackupFile());
		// Flush sin connected
		TrackerAsset.getInstance().flush();
		assertEquals(storage.load("netstorage"), "");
		// Flush porque si
		TrackerAsset.getInstance().flush();
		assertEquals(storage.load("netstorage"), "");
		// Flush tras conectar
		bridge.setConnected(true);
		TrackerAsset.getInstance().flush();
		String net = storage.load("netstorage");
		assertEquals(storage.load("netstorage"), "");
		bridge.setConnected(false);
		enqueueTrace01();
		TrackerAsset.getInstance().flush();
		TrackerAsset.getInstance().flush();
		bridge.setConnected(true);
		TrackerAsset.getInstance().flush();
		TrackerAsset.getInstance().flush();
		String[] text = storage.load("netstorage").split("\n");
		assertEquals(text.length, 1);
		String[] backup = storage.load(settings.getBackupFile()).split("\n");
		assertEquals(backup.length, 1);
	}

	private void append(String file, String text) throws Exception {
		if (append_storage != null) {
			append_storage.Append(file, text);
		} else {
			storage.save(storage.load(file), text);
		}
	}

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
}
