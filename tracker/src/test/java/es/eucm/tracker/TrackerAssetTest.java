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

import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Bool;
import es.eucm.tracker.Exceptions.*;
import es.eucm.tracker.Utils.RefSupport;
import es.eucm.tracker.Utils.TrackerAssetUtils;
import eu.rageproject.asset.manager.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link es.eucm.tracker.TrackerAsset}
 */
public class TrackerAssetTest {

	@Test
	public void test() throws IOException {

		TrackerAsset tracker = new TrackerAsset();

		// TODO Tests with junit
		int expected = 5;
		int actual = 5;

		assertEquals("Value should be equal", expected, actual);
	}

	// asdsa
	private static final Gson gson = new Gson();
	private static final ArrayList<HashMap<String, Object>> arraymap = new ArrayList<>();
	TrackerAssetSettings settings = new TrackerAssetSettings();
	IDataStorage storage;
	IAppend append_storage;
	ILog log;
	TesterBridge bridge;

	private void initTracker(String format) throws Exception{
		initTracker(format, TrackerAsset.StorageTypes.local, null);
	}

	private void initTracker(String format, TrackerAsset.StorageTypes st, TesterBridge bridge) throws Exception{
		TrackerAsset.getInstance().stop();
		Path current = Paths.get(System.getProperty("user.dir"));

		TrackerAsset.TraceFormats f = TrackerAsset.TraceFormats.json;
		RefSupport<TrackerAsset.TraceFormats> rv = new RefSupport<TrackerAsset.TraceFormats>();
		if (TrackerAssetUtils.ParseEnum(format, rv, TrackerAsset.TraceFormats.class))
		{
			settings.setTraceFormat(rv.getValue());
		}

		settings.setStorageType(st);
		TrackerAsset.getInstance().setSettings(settings);
		if (bridge != null)
			this.bridge = bridge;
		else
			this.bridge = new TesterBridge();
		TrackerAsset.getInstance().setBridge(this.bridge);
		storage = getInterface(IDataStorage.class);
		append_storage = getInterface(IAppend.class);
		log = (ILog)TrackerAsset.getInstance().getBridge();
		TrackerAsset.getInstance().setStrictMode(true);
		TrackerAsset.getInstance().clear();
		TrackerAsset.getInstance().start();
	}

	@Test
	public void actionTraceTest() throws Exception {
		initTracker("csv");
		TrackerAsset.getInstance().setStrictMode(false);
		TrackerAsset.getInstance().actionTrace("Verb","Type","ID");
		checkCSVTrace("Verb,Type,ID");
		TrackerAsset.getInstance().actionTrace("Verb","Ty,pe","ID");
		checkCSVTrace("Verb,Ty\\,pe,ID");
		TrackerAsset.getInstance().actionTrace("Verb","Type","I,D");
		checkCSVTrace("Verb,Type,I\\,D");
		TrackerAsset.getInstance().actionTrace("Ve,rb","Type","ID");
		checkCSVTrace("Ve\\,rb,Type,ID");
		initTracker("csv");
		TrackerAsset.getInstance().setStrictMode(true);

		try{ TrackerAsset.getInstance().actionTrace(null, "Type", "ID"); } catch (TraceException te){ assertNotNull(te); }
		try{ TrackerAsset.getInstance().actionTrace("Verb", null, "ID"); } catch (TraceException te){ assertNotNull(te); };
		try{ TrackerAsset.getInstance().actionTrace("Verb", "Type", null); } catch (TraceException te){ assertNotNull(te); };
		try{ TrackerAsset.getInstance().actionTrace("", "Type", "ID"); } catch (TraceException te){ assertNotNull(te); };
		try{ TrackerAsset.getInstance().actionTrace("Verb", "", "ID"); } catch (TraceException te){ assertNotNull(te); };
		try{ TrackerAsset.getInstance().actionTrace("Verb", "Type", ""); } catch (TraceException te){ assertNotNull(te); };


		cleanStorage();
		initTracker("xapi");
		TrackerAsset.getInstance().setStrictMode(false);
		TrackerAsset.getInstance().actionTrace("Verb","Type","ID");
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());
		if (text.indexOf("M\n") != -1)
			text = text.substring(text.indexOf("M\n") + 2);


		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(file.size() -1 );
		assertEquals(tracejson.entrySet().size(), 4);
		assertEquals(((Map) tracejson.get("object")).get("id"), "ID");
		assertEquals(((Map) ((Map) tracejson.get("object")).get("definition")).get("type"), "Type");
		assertEquals(((Map) tracejson.get("verb")).get("id"), "Verb");
	}

	@Test
	public void testNullImputs() throws Exception {
		initTracker("xapi");
		Exception exception = null;

		try { TrackerAsset.getInstance().actionTrace(null, "Type", "ID"); }catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().actionTrace("Verb", null, "ID"); }catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().actionTrace("Verb", "Type", null); }catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;

		try { TrackerAsset.getInstance().actionTrace("", "Type", "ID"); }catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().actionTrace("Verb", "", "ID"); }catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().actionTrace("Verb", "Type", ""); }catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;

		try { TrackerAsset.getInstance().actionTrace("Verb", "Type", "ID"); }catch(VerbXApiException e){ exception = e; };
		assertNotNull(exception); exception = null;

		try { TrackerAsset.getInstance().getCompletable().initialized(null); }catch(TargetXApiException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().getCompletable().progressed(null, 0.1f); }catch(TargetXApiException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().getCompletable().completed(null); }catch(TargetXApiException e){ exception = e; };
		assertNotNull(exception); exception = null;

		try { TrackerAsset.getInstance().getAccessible().accessed(null); }catch(TargetXApiException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().getAccessible().skipped(null); }catch(TargetXApiException e){ exception = e; };
		assertNotNull(exception); exception = null;

		try { TrackerAsset.getInstance().getAlternative().selected(null, null); }catch(TargetXApiException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().getAlternative().selected(null, "o"); }catch(TargetXApiException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().getAlternative().selected("k", null); }catch(ValueExtensionException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().getAlternative().unlocked(null, null); }catch(TargetXApiException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().getAlternative().unlocked(null, "o"); }catch(TargetXApiException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().getAlternative().unlocked("k", null); }catch(ValueExtensionException e){ exception = e; };
		assertNotNull(exception); exception = null;

		try { TrackerAsset.getInstance().getGameObject().interacted(null); }catch(TargetXApiException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().getGameObject().used(null); }catch(TargetXApiException e){ exception = e; };
		assertNotNull(exception); exception = null;

		try { TrackerAsset.getInstance().setVar("", ""); }catch(KeyExtensionException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().setVar(null, "v"); }catch(KeyExtensionException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try { TrackerAsset.getInstance().setVar("k", ""); }catch(ValueExtensionException e){ exception = e; };
		assertNotNull(exception); exception = null;

		try { TrackerAsset.getInstance().setVar("k", "v"); }catch(Exception e){ exception = e; };
		assertNull(exception);
	}

	@Test
	public void testObsoleteMethods() throws Exception {
		initTracker("xapi");
		Exception exception = null;
		try{ TrackerAsset.getInstance().trace(""); } catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try{ TrackerAsset.getInstance().trace("1"); } catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try{ TrackerAsset.getInstance().trace("1,2"); } catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try{ TrackerAsset.getInstance().trace("1,2,3,4"); } catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try{ TrackerAsset.getInstance().trace("1", "2"); } catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try{ TrackerAsset.getInstance().trace("1", "2", null); } catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try{ TrackerAsset.getInstance().trace("1", "2", ""); } catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try{ TrackerAsset.getInstance().trace("", "", ""); } catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try{ TrackerAsset.getInstance().trace("1", "2", "3", "4"); } catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try{ TrackerAsset.getInstance().trace(null, null); } catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try{ TrackerAsset.getInstance().trace("1,2,3,4"); } catch(TraceException e){ exception = e; };
		assertNotNull(exception); exception = null;
		try{ TrackerAsset.getInstance().trace("1,2,3"); TrackerAsset.getInstance().requestFlush(); } catch(VerbXApiException e){ exception = e; };
		assertNotNull(exception); exception = null;

		initTracker("csv");
		TrackerAsset.getInstance().setStrictMode(false);
		TrackerAsset.getInstance().actionTrace("Verb","Type","ID");
		checkCSVTrace("Verb,Type,ID");
		TrackerAsset.getInstance().actionTrace("Verb","Ty,pe","ID");
		checkCSVTrace("Verb,Ty\\,pe,ID");
		TrackerAsset.getInstance().actionTrace("Verb","Type","I,D");
		checkCSVTrace("Verb,Type,I\\,D");
		TrackerAsset.getInstance().actionTrace("Ve,rb","Type","ID");
		checkCSVTrace("Ve\\,rb,Type,ID");
		TrackerAsset.getInstance().trace("Verb,Type,ID");
		checkCSVTrace("Verb,Type,ID");
		TrackerAsset.getInstance().trace("Verb,Ty\\,pe,ID");
		checkCSVTrace("Verb,Ty\\,pe,ID");
		TrackerAsset.getInstance().trace("Verb,Type,I\\,D");
		checkCSVTrace("Verb,Type,I\\,D");
		TrackerAsset.getInstance().trace("Ve\\,rb,Type,ID");
		checkCSVTrace("Ve\\,rb,Type,ID");
		try { TrackerAsset.getInstance().actionTrace("Verb", "Type", "ID"); } catch(Exception e){ exception = e; };
		assertNull(exception); exception = null;
		initTracker("csv");
		TrackerAsset.getInstance().setStrictMode(true);
		try { TrackerAsset.getInstance().setVar("k", ""); } catch(ValueExtensionException e){ exception = e; };
		assertNotNull(exception); exception = null;

		try { TrackerAsset.getInstance().setVar("k", 1); } catch(Exception e){ exception = e; };
		assertNull(exception); exception = null;
		try { TrackerAsset.getInstance().setVar("k", 1.1f); } catch(Exception e){ exception = e; };
		assertNull(exception); exception = null;
		try { TrackerAsset.getInstance().setVar("k", 1.1d); } catch(Exception e){ exception = e; };
		assertNull(exception); exception = null;
		try { TrackerAsset.getInstance().setVar("k", "v"); } catch(Exception e){ exception = e; };
		assertNull(exception); exception = null;
	}

	@Test
	public void alternativeTraceTest() throws Exception {
		initTracker("csv");
		TrackerAsset.getInstance().getAlternative().selected("question","alternative");
		checkCSVTrace("selected,alternative,question,response,alternative");
	}

	@Test
	public void testTrace_Generic_Csv_Stored_01() throws Exception {
		initTracker("csv");
		enqueueTrace01();
		TrackerAsset.getInstance().flush();
		checkCSVStoredTrace("accessed,gameobject,ObjectID");
	}

	@Test
	public void testTrace_Generic_Csv_Stored_02() throws Exception {
		initTracker("csv");
		enqueueTrace02();
		TrackerAsset.getInstance().flush();
		checkCSVStoredTrace("initialized,game,ObjectID2,response,TheResponse,score,0.123");
	}

	@Test
	public void testTrace_Generic_Csv_Stored_03() throws Exception {
		initTracker("csv");
		enqueueTrace03();
		TrackerAsset.getInstance().flush();
		checkCSVStoredTrace("selected,zone,ObjectID3,success,false,completion,true,response,AnotherResponse,score,123.456,extension1,value1,extension2,value2,extension3,3,extension4,4.56");
	}

	@Test
	public void testTrace_Generic_Csv_Stored_WithComma() throws Exception {
		initTracker("csv");
		TrackerAsset.getInstance().setStrictMode(false);
		TrackerAsset.getInstance().setVar("e1","ex,2");
		TrackerAsset.getInstance().setVar("e,1","ex,2,");
		TrackerAsset.getInstance().setVar("e3","e3");
		TrackerAsset.getInstance().actionTrace("verb","target","id");
		TrackerAsset.getInstance().flush();
		checkCSVStoredTrace("verb,target,id,e1,ex\\,2,e\\,1,ex\\,2\\,,e3,e3");
	}

	@Test
	public void testTrace_Generic_XApi_Stored_01() throws Exception {
		cleanStorage();
		initTracker("xapi");
		enqueueTrace01();
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());
		if (text.indexOf("M\n") != -1)
			text = text.substring(text.indexOf("M\n") + 2);


		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(file.size() -1 );
		assertEquals(tracejson.entrySet().size(), 4);
		assertEquals(((Map) tracejson.get("object")).get("id"), "ObjectID");
		assertEquals(((Map) ((Map) tracejson.get("object")).get("definition")).get("type"), "https://w3id.org/xapi/seriousgames/activity-types/game-object");
		assertEquals(((Map) tracejson.get("verb")).get("id"), "https://w3id.org/xapi/seriousgames/verbs/accessed");
	}

	@Test
	public void testTrace_Generic_XApi_Stored_02() throws Exception {
		cleanStorage();
		initTracker("xapi");
		enqueueTrace02();
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		if (text.indexOf("M\n") != -1)
			text = text.substring(text.indexOf("M\n") + 2);

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(file.size() -1 );
		assertEquals(tracejson.entrySet().size(), 5);
		assertEquals(((Map) tracejson.get("object")).get("id"), "ObjectID2");
		assertEquals(((Map) ((Map) tracejson.get("object")).get("definition")).get("type"), "https://w3id.org/xapi/seriousgames/activity-types/serious-game");
		assertEquals(((Map) tracejson.get("verb")).get("id"), "http://adlnet.gov/expapi/verbs/initialized");
		assertEquals(((Map) tracejson.get("result")).size(), 2);
		assertEquals(((Map) tracejson.get("result")).get("response"), "TheResponse");
		assertEquals(((Map) ((Map) tracejson.get("result")).get("score")).get("raw"), 0.123);
	}

	@Test
	public void testTrace_Generic_XApi_Stored_03() throws Exception {
		cleanStorage();
		initTracker("xapi");
		enqueueTrace03();
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());

		if (text.indexOf("M\n") != -1)
			text = text.substring(text.indexOf("M\n") + 2);

		ArrayList file = gson.fromJson(text, arraymap.getClass());
		Map tracejson = (Map) file.get(file.size() -1 );
		assertEquals(tracejson.entrySet().size(), 5);
		assertEquals(((Map) tracejson.get("object")).get("id"), "ObjectID3");
		assertEquals(((Map) ((Map) tracejson.get("object")).get("definition")).get("type"), "https://w3id.org/xapi/seriousgames/activity-types/zone");
		assertEquals(((Map) tracejson.get("verb")).get("id"), "https://w3id.org/xapi/adb/verbs/selected");
		assertEquals(((Map) tracejson.get("result")).size(), 5);
		assertEquals(((Map) tracejson.get("result")).get("response"), "AnotherResponse");
		assertEquals(((Map) ((Map) tracejson.get("result")).get("score")).get("raw"), 123.456);
		assertEquals(((Map) tracejson.get("result")).get("completion"), true);
		assertEquals(((Map) tracejson.get("result")).get("success"), false);
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions")).entrySet().size(), 4);
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions")).get("extension1"), "value1");
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions")).get("extension2"), "value2");
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions")).get("extension3"), 3);
		assertEquals(((Map) ((Map) tracejson.get("result")).get("extensions")).get("extension4"), 4.56);
	}
 /*
	public void testTrace_Generic_XApi_All() throws Exception {
		cleanStorage();
		initTracker("xapi");
		enqueueTrace01();
		enqueueTrace02();
		enqueueTrace03();
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());
		if (text.IndexOf("M\n") != -1)
			text = text.Substring(text.IndexOf("M\n") + 2);

		JSONNode file = JSON.parse(text);
		Assert.AreEqual((new List<JSONNode>(file.getChildren())).Count, 3);
		JSONNode tracejson = file.get___idx(0);
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 4);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "ObjectID");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/game-object");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "https://w3id.org/xapi/seriousgames/verbs/accessed");
		//CHECK THE 2ND TRACE
		tracejson = file.get___idx(1);
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 5);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "ObjectID2");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/serious-game");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "http://adlnet.gov/expapi/verbs/initialized");
		Assert.AreEqual((new List<JSONNode>(tracejson.get___idx("result").getChildren())).Count, 2);
		Assert.AreEqual(tracejson.get___idx("result").get___idx("response").getValue(), "TheResponse");
		Assert.AreEqual(tracejson.get___idx("result").get___idx("score").get___idx("raw").getAsFloat(), 0.123f);
		//CHECK THE 3RD TRACE
		tracejson = file.get___idx(2);
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 5);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "ObjectID3");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/zone");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "https://w3id.org/xapi/adb/verbs/selected");
		Assert.AreEqual((new List<JSONNode>(tracejson.get___idx("result").getChildren())).Count, 5);
		Assert.AreEqual(tracejson.get___idx("result").get___idx("response").getValue(), "AnotherResponse");
		Assert.AreEqual(tracejson.get___idx("result").get___idx("score").get___idx("raw").getAsFloat(), 123.456f);
		Assert.AreEqual(tracejson.get___idx("result").get___idx("completion").getAsBool(), true);
		Assert.AreEqual(tracejson.get___idx("result").get___idx("success").getAsBool(), false);
		Assert.AreEqual((new List<JSONNode>(tracejson.get___idx("result").get___idx("extensions").getChildren())).Count, 4);
		Assert.AreEqual(tracejson.get___idx("result").get___idx("extensions").get___idx("extension1").getValue(), "value1");
		Assert.AreEqual(tracejson.get___idx("result").get___idx("extensions").get___idx("extension2").getValue(), "value2");
		Assert.AreEqual(tracejson.get___idx("result").get___idx("extensions").get___idx("extension3").getAsInt(), 3);
		Assert.AreEqual(tracejson.get___idx("result").get___idx("extensions").get___idx("extension4").getAsFloat(), 4.56f);
	}

	public void testAccesible_Csv_01() throws Exception {
		initTracker("csv");
		TrackerAsset.getInstance().getAccessible().accessed("AccesibleID",AccessibleTracker.Accessible.Cutscene);
		checkCSVTrace("accessed,cutscene,AccesibleID");
	}

	public void testAccesible_Csv_02_WithExtensions() throws Exception {
		initTracker("csv");
		TrackerAsset.getInstance().setVar("extension1","value1");
		TrackerAsset.getInstance().getAccessible().skipped("AccesibleID2",AccessibleTracker.Accessible.Screen);
		checkCSVTrace("skipped,screen,AccesibleID2,extension1,value1");
	}

	public void testAccesible_XApi_01() throws Exception {
		cleanStorage();
		initTracker("xapi");
		TrackerAsset.getInstance().getAccessible().accessed("AccesibleID",AccessibleTracker.Accessible.Cutscene);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());
		if (text.IndexOf("M\n") != -1)
			text = text.Substring(text.IndexOf("M\n") + 2);

		JSONNode file = JSON.parse(text);
		JSONNode tracejson = file[(new List<JSONNode>(file.getChildren())).Count - 1];
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 4);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "AccesibleID");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/cutscene");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "https://w3id.org/xapi/seriousgames/verbs/accessed");
	}

	public void testAccesible_XApi_02_WithExtensions() throws Exception {
		cleanStorage();
		initTracker("xapi");
		TrackerAsset.getInstance().setVar("extension1","value1");
		TrackerAsset.getInstance().getAccessible().skipped("AccesibleID2",AccessibleTracker.Accessible.Screen);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());
		if (text.IndexOf("M\n") != -1)
			text = text.Substring(text.IndexOf("M\n") + 2);

		JSONNode file = JSON.parse(text);
		JSONNode tracejson = file[(new List<JSONNode>(file.getChildren())).Count - 1];
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 5);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "AccesibleID2");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/screen");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "http://id.tincanapi.com/verb/skipped");
		Assert.AreEqual(tracejson.get___idx("result").get___idx("extensions").get___idx("extension1").getValue(), "value1");
	}

	public void testAlternative_Csv_01() throws Exception {
		initTracker("csv");
		TrackerAsset.getInstance().getAlternative().selected("AlternativeID","SelectedOption",AlternativeTracker.Alternative.Path);
		checkCSVTrace("selected,path,AlternativeID,response,SelectedOption");
	}

	public void testAlternative_Csv_02_WithExtensions() throws Exception {
		initTracker("csv");
		TrackerAsset.getInstance().setVar("SubCompletableScore",0.8);
		TrackerAsset.getInstance().getAlternative().unlocked("AlternativeID2","Answer number 3",AlternativeTracker.Alternative.Question);
		checkCSVTrace("unlocked,question,AlternativeID2,response,Answer number 3,SubCompletableScore,0.8");
	}

	public void testAlternative_XApi_01() throws Exception {
		cleanStorage();
		initTracker("xapi");
		TrackerAsset.getInstance().getAlternative().selected("AlternativeID","SelectedOption",AlternativeTracker.Alternative.Path);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());
		if (text.IndexOf("M\n") != -1)
			text = text.Substring(text.IndexOf("M\n") + 2);

		JSONNode file = JSON.parse(text);
		JSONNode tracejson = file[(new List<JSONNode>(file.getChildren())).Count - 1];
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 5);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "AlternativeID");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/path");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "https://w3id.org/xapi/adb/verbs/selected");
		Assert.AreEqual(tracejson.get___idx("result").get___idx("response").getValue(), "SelectedOption");
	}

	public void testAlternative_XApi_02_WithExtensions() throws Exception {
		cleanStorage();
		initTracker("xapi");
		TrackerAsset.getInstance().setVar("SubCompletableScore",0.8);
		TrackerAsset.getInstance().getAlternative().unlocked("AlternativeID2","Answer number 3",AlternativeTracker.Alternative.Question);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());
		if (text.IndexOf("M\n") != -1)
			text = text.Substring(text.IndexOf("M\n") + 2);

		JSONNode file = JSON.parse(text);
		JSONNode tracejson = file[(new List<JSONNode>(file.getChildren())).Count - 1];
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 5);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "AlternativeID2");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "http://adlnet.gov/expapi/activities/question");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "https://w3id.org/xapi/seriousgames/verbs/unlocked");
		Assert.AreEqual(tracejson.get___idx("result").get___idx("response").getValue(), "Answer number 3");
		Assert.AreEqual(tracejson.get___idx("result").get___idx("extensions").get___idx("SubCompletableScore").getAsFloat(), 0.8f);
	}

	public void testCompletable_Csv_01() throws Exception {
		initTracker("csv");
		TrackerAsset.getInstance().getCompletable().initialized("CompletableID",CompletableTracker.Completable.Quest);
		checkCSVTrace("initialized,quest,CompletableID");
	}

	public void testCompletable_Csv_02() throws Exception {
		initTracker("csv");
		TrackerAsset.getInstance().getCompletable().Progressed("CompletableID2", CompletableTracker.Completable.Stage, 0.34f);
		checkCSVTrace("progressed,stage,CompletableID2,progress,0.34");
	}

	public void testCompletable_Csv_03() throws Exception {
		initTracker("csv");
		TrackerAsset.getInstance().getCompletable().Completed("CompletableID3", CompletableTracker.Completable.Race, true, 0.54f);
		checkCSVTrace("completed,race,CompletableID3,success,true,score,0.54");
	}

	public void testCompletable_XApi_01() throws Exception {
		cleanStorage();
		initTracker("xapi");
		TrackerAsset.getInstance().getCompletable().initialized("CompletableID",CompletableTracker.Completable.Quest);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());
		if (text.IndexOf("M\n") != -1)
			text = text.Substring(text.IndexOf("M\n") + 2);

		JSONNode file = JSON.parse(text);
		JSONNode tracejson = file[(new List<JSONNode>(file.getChildren())).Count - 1];
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 4);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "CompletableID");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/quest");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "http://adlnet.gov/expapi/verbs/initialized");
	}

	public void testCompletable_XApi_02() throws Exception {
		cleanStorage();
		initTracker("xapi");
		TrackerAsset.getInstance().getCompletable().Progressed("CompletableID2", CompletableTracker.Completable.Stage, 0.34f);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());
		if (text.IndexOf("M\n") != -1)
			text = text.Substring(text.IndexOf("M\n") + 2);

		JSONNode file = JSON.parse(text);
		JSONNode tracejson = file[(new List<JSONNode>(file.getChildren())).Count - 1];
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 5);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "CompletableID2");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/stage");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "http://adlnet.gov/expapi/verbs/progressed");
		Assert.AreEqual(tracejson.get___idx("result").get___idx("extensions").get___idx("https://w3id.org/xapi/seriousgames/extensions/progress").getAsFloat(), 0.34f);
	}

	public void testCompletable_XApi_03() throws Exception {
		cleanStorage();
		initTracker("xapi");
		TrackerAsset.getInstance().getCompletable().Completed("CompletableID3", CompletableTracker.Completable.Race, true, 0.54f);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());
		if (text.IndexOf("M\n") != -1)
			text = text.Substring(text.IndexOf("M\n") + 2);

		JSONNode file = JSON.parse(text);
		JSONNode tracejson = file[(new List<JSONNode>(file.getChildren())).Count - 1];
		Assert.AreEqual(tracejson.getCount(), 5);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "CompletableID3");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/race");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "http://adlnet.gov/expapi/verbs/completed");
		Assert.AreEqual(tracejson.get___idx("result").get___idx("success").getAsBool(), true);
		Assert.AreEqual(tracejson.get___idx("result").get___idx("score").get___idx("raw").getAsFloat(), 0.54f);
	}

	public void testGameObject_Csv_01() throws Exception {
		initTracker("csv");
		TrackerAsset.getInstance().getGameObject().interacted("GameObjectID",GameObjectTracker.TrackedGameObject.Npc);
		checkCSVTrace("interacted,npc,GameObjectID");
	}

	public void testGameObject_Csv_02() throws Exception {
		initTracker("csv");
		TrackerAsset.getInstance().getGameObject().used("GameObjectID2",GameObjectTracker.TrackedGameObject.Item);
		checkCSVTrace("used,item,GameObjectID2");
	}

	public void testGameObject_XApi_01() throws Exception {
		cleanStorage();
		initTracker("xapi");
		TrackerAsset.getInstance().getGameObject().interacted("GameObjectID",GameObjectTracker.TrackedGameObject.Npc);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());
		if (text.IndexOf("M\n") != -1)
			text = text.Substring(text.IndexOf("M\n") + 2);

		JSONNode file = JSON.parse(text);
		JSONNode tracejson = file[(new List<JSONNode>(file.getChildren())).Count - 1];
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 4);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "GameObjectID");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/non-player-character");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "http://adlnet.gov/expapi/verbs/interacted");
	}

	public void testGameObject_XApi_02() throws Exception {
		cleanStorage();
		initTracker("xapi");
		TrackerAsset.getInstance().getGameObject().used("GameObjectID2",GameObjectTracker.TrackedGameObject.Item);
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getLogFile());
		if (text.IndexOf("M\n") != -1)
			text = text.Substring(text.IndexOf("M\n") + 2);

		JSONNode file = JSON.parse(text);
		JSONNode tracejson = file[(new List<JSONNode>(file.getChildren())).Count - 1];
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 4);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "GameObjectID2");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/item");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "https://w3id.org/xapi/seriousgames/verbs/used");
	}

*/

	private void enqueueTrace01() throws Exception {
		TrackerAsset.getInstance().actionTrace("accessed","gameobject","ObjectID");
	}

	private void enqueueTrace02() throws Exception {
		TrackerAsset.getInstance().setResponse("TheResponse");
		TrackerAsset.getInstance().setScore(0.123f);
		TrackerAsset.getInstance().actionTrace("initialized","game","ObjectID2");
	}

	private void enqueueTrace03() throws Exception {
		TrackerAsset.getInstance().setResponse("AnotherResponse");
		TrackerAsset.getInstance().setScore(123.456f);
		TrackerAsset.getInstance().setSuccess(false);
		TrackerAsset.getInstance().setCompletion(true);
		TrackerAsset.getInstance().setVar("extension1","value1");
		TrackerAsset.getInstance().setVar("extension2","value2");
		TrackerAsset.getInstance().setVar("extension3",3);
		TrackerAsset.getInstance().setVar("extension4",4.56f);
		TrackerAsset.getInstance().actionTrace("selected","zone","ObjectID3");
	}

	private void checkCSVTrace(String trace) throws Exception {
		//TODO: this method should access the queue directly.
		TrackerAsset.getInstance().flush();
		checkCSVStoredTrace(trace);
	}

	private void checkCSVStoredTrace(String trace) throws Exception {
		String[] lines = storage.load(settings.getLogFile()).split("\r\n");
		String traceWithoutTimestamp = removeTimestamp(lines[lines.length - 1]);
		compareCSV(traceWithoutTimestamp, trace);
	}

	private void checkXAPIStoredTrace(String trace, String file) throws Exception {
		if ((file.equals("")))
			file = settings.getLogFile();

		String[] lines = storage.load(file).split("\r\n");
		String traceWithoutTimestamp = removeTimestamp(lines[lines.length - 1]);
		compareCSV(traceWithoutTimestamp,trace);
	}

	private void compareCSV(String t1, String t2) throws Exception {
		List<String> sp1 = TrackerAssetUtils.parseCSV(t1);
		List<String> sp2 = TrackerAssetUtils.parseCSV(t2);
		assertEquals(sp1.size(), sp2.size());
		for (int i = 0;i < 3;i++)
			Assert.assertEquals(sp1.get(i), sp2.get(i));
		Map<String, String> d1 = new HashMap<>();
		if (sp1.size() > 3)
		{
			for (int i = 3;i < sp1.size();i += 2)
			{
				d1.put(sp1.get(i), sp1.get(i + 1));
			}
			for (int i = 3;i < sp2.size();i += 2)
			{
				Assert.assertTrue(d1.containsKey(sp2.get(i)));
				assertEquals(d1.get(sp2.get(i)), sp2.get(i + 1));
			}
		}

	}

	private String removeTimestamp(String trace) throws Exception {
		return trace.substring(trace.indexOf(',') + 1);
	}

	private void cleanStorage() throws Exception {
		if (settings != null && storage != null && settings.getLogFile() != null && storage.exists(settings.getLogFile()))
		{
			storage.delete(settings.getLogFile());
		}

	}

	/*
	public void testTraceSendingSync() throws Exception {
		initTracker("xapi", AssetPackage.TrackerAsset.StorageTypes.net);
		storage.Delete("netstorage");
		enqueueTrace01();
		TrackerAsset.getInstance().flush();
		String text = storage.Load("netstorage");
		if (text.IndexOf("M\n") != -1)
			text = text.Substring(text.IndexOf("M\n") + 2);

		JSONNode file = JSON.parse(text);
		JSONNode tracejson = file[(new List<JSONNode>(file.getChildren())).Count - 1];
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 4);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "http://a2:3000/api/proxy/gleaner/games/5a26cb5ac8b102008b41472a/5a26cb5ac8b102008b41472b/ObjectID");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/game-object");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "https://w3id.org/xapi/seriousgames/verbs/accessed");
		append("netstorage",",");
		enqueueTrace02();
		enqueueTrace03();
		TrackerAsset.getInstance().flush();
		text = storage.Load("netstorage");
		text = "[" + text + "]";
		file = JSON.parse(text);
		Assert.AreEqual((new List<JSONNode>(file.getChildren())).Count, 2);
		Assert.AreEqual(file.get___idx(0).getCount(), 1);
		Assert.AreEqual(file.get___idx(1).getCount(), 2);
	}

	public void testBackupSync() throws Exception {
		if (storage != null)
			storage.delete(settings.getBackupFile());

		testTraceSendingSync();
		String text = storage.load(settings.getBackupFile());
		String[] file = text.Split('\n');
		Assert.AreEqual(file.Length, 4);
	}

	public void testTraceSending_IntermitentConnection() throws Exception {
		initTracker("xapi", AssetPackage.TrackerAsset.StorageTypes.net);
		storage.Delete("netstorage");
		enqueueTrace01();
		TrackerAsset.getInstance().flush();
		String text = storage.Load("netstorage");
		if (text.IndexOf("M\n") != -1)
			text = text.Substring(text.IndexOf("M\n") + 2);

		JSONNode file = JSON.parse(text);
		JSONNode tracejson = file[(new List<JSONNode>(file.getChildren())).Count - 1];
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 4);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "http://a2:3000/api/proxy/gleaner/games/5a26cb5ac8b102008b41472a/5a26cb5ac8b102008b41472b/ObjectID");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/game-object");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "https://w3id.org/xapi/seriousgames/verbs/accessed");
		bridge.setConnnected(false);
		enqueueTrace02();
		enqueueTrace03();
		TrackerAsset.getInstance().flush();
		text = storage.Load("netstorage");
		file = JSON.parse(text);
		Assert.AreEqual((new List<JSONNode>(file.getChildren())).Count, 1);
		Assert.AreEqual(file.getCount(), 1);
		bridge.setConnnected(true);
		append("netstorage",",");
		TrackerAsset.getInstance().flush();
		text = storage.Load("netstorage");
		text = "[" + text + "]";
		file = JSON.parse(text);
		Assert.AreEqual((new List<JSONNode>(file.getChildren())).Count, 2);
		Assert.AreEqual(file.get___idx(0).getCount(), 1);
		Assert.AreEqual(file.get___idx(1).getCount(), 2);
	}

	public void testBackupSync_IntermitentConnection() throws Exception {
		initTracker("xapi", AssetPackage.TrackerAsset.StorageTypes.net);
		storage.Delete("netstorage");
		storage.delete(settings.getBackupFile());
		enqueueTrace01();
		TrackerAsset.getInstance().flush();
		String text = storage.load(settings.getBackupFile());
		String[] file = text.Split('\n');
		Assert.AreEqual(file.Length, 2);
		bridge.setConnnected(false);
		enqueueTrace02();
		enqueueTrace03();
		TrackerAsset.getInstance().flush();
		text = storage.load(settings.getBackupFile());
		file = text.Split('\n');
		Assert.AreEqual(file.Length, 4);
		bridge.setConnnected(true);
		TrackerAsset.getInstance().flush();
		text = storage.load(settings.getBackupFile());
		file = text.Split('\n');
		Assert.AreEqual(file.Length, 4);
	}

	public void testTraceSending_WithoutStart() throws Exception {
		TrackerAsset.getInstance().stop();
		Assert.Throws(TrackerException.class);
	}

	public void testTraceSendingStartFailed() throws Exception {
		TrackerAsset.getInstance().stop();
		bridge = new TesterBridge();
		bridge.setConnnected(false);
		initTracker("xapi",AssetPackage.TrackerAsset.StorageTypes.net,bridge);
		storage.Delete("netstorage");
		storage.delete(settings.getBackupFile());
		Assert.DoesNotThrow();
		TrackerAsset.getInstance().flush();
		Assert.AreEqual(storage.Load("netstorage"), String.Empty);
		Assert.AreNotEqual(storage.load(settings.getBackupFile()), String.Empty);
		bridge.setConnnected(true);
		enqueueTrace02();
		enqueueTrace03();
		append("netstorage",",");
		TrackerAsset.getInstance().flush();
		String text = storage.Load("netstorage");
		text = "[" + text + "]";
		JSONNode file = JSON.parse(text);
		Assert.AreEqual((new List<JSONNode>(file.getChildren())).Count, 2);
		Assert.AreEqual(file.get___idx(0).getCount(), 1);
		Assert.AreEqual(file.get___idx(1).getCount(), 2);
		JSONNode tracejson = file.get___idx(0).get___idx(0);
		Assert.AreEqual((new List<JSONNode>(tracejson.getChildren())).Count, 4);
		Assert.AreEqual(tracejson.get___idx("object").get___idx("id").getValue(), "http://a2:3000/api/proxy/gleaner/games/5a26cb5ac8b102008b41472a/5a26cb5ac8b102008b41472b/ObjectID");
		Assert.AreEqual(tracejson.get___idx("object").get___idx("definition").get___idx("type").getValue(), "https://w3id.org/xapi/seriousgames/activity-types/game-object");
		Assert.AreEqual(tracejson.get___idx("verb").get___idx("id").getValue(), "https://w3id.org/xapi/seriousgames/verbs/accessed");
		text = storage.load(settings.getBackupFile());
		String[] backup = text.Split('\n');
		Assert.AreEqual(backup.Length, 4);
	}

	public void testEmptyQueueFlush() throws Exception {
		TrackerAsset.getInstance().stop();
		bridge = new TesterBridge();
		bridge.setConnnected(false);
		initTracker("csv",AssetPackage.TrackerAsset.StorageTypes.net,bridge);
		storage.Delete("netstorage");
		storage.delete(settings.getBackupFile());
		//Flush sin connected
		TrackerAsset.getInstance().flush();
		Assert.AreEqual(storage.Load("netstorage"), String.Empty);
		//Flush porque si
		TrackerAsset.getInstance().flush();
		Assert.AreEqual(storage.Load("netstorage"), String.Empty);
		//Flush tras conectar
		bridge.setConnnected(true);
		TrackerAsset.getInstance().flush();
		String net = storage.Load("netstorage");
		Assert.AreEqual(storage.Load("netstorage"), String.Empty);
		bridge.setConnnected(false);
		enqueueTrace01();
		TrackerAsset.getInstance().flush();
		TrackerAsset.getInstance().flush();
		bridge.setConnnected(true);
		TrackerAsset.getInstance().flush();
		TrackerAsset.getInstance().flush();
		String[] text = storage.Load("netstorage").Split('\n');
		Assert.AreEqual(text.Length, 2);
		String[] backup = storage.load(settings.getBackupFile()).Split('\n');
		Assert.AreEqual(backup.Length, 2);
	}
	*/

	private void append(String file, String text) throws Exception {
		if (append_storage != null)
		{
			append_storage.Append(file, text);
		}
		else
		{
			storage.save(storage.load(file), text);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getInterface(final Class<T> adapter) {
		if (this.bridge != null && adapter.isAssignableFrom(this.bridge.getClass())) {
			return (T) this.bridge;
		}

		IBridge assetManagerBridge = AssetManager.getInstance().getBridge();
		if (assetManagerBridge != null && adapter.isAssignableFrom(assetManagerBridge.getClass())) {
			return (T) assetManagerBridge;
		}

		return null;
	}
}
