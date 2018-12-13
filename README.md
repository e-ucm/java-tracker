# Java xAPI Tracker

[![Build Status](https://travis-ci.org/e-ucm/java-tracker.svg)](https://travis-ci.org/e-ucm/java-tracker) [![Coverage Status](https://coveralls.io/repos/e-ucm/java-tracker/badge.svg?branch=master&service=github)](https://coveralls.io/github/e-ucm/java-tracker?branch=master)

xAPI traces sent by games should comply with the [xAPI for serious games specification](https://github.com/e-ucm/xapi-seriousgames).

## Installation
1. Clone or download repository
1. Copy into your project folder
1. Import the Java Tracker into your code:
  ```java
  import es.eucm.tracker.*;
  import es.eucm.tracker.Exceptions.XApiException;
  import eu.rageproject.asset.manager.Severity;
  ```
4. Configure the tracker by:
  ```java
  TrackerAsset tracker = new TrackerAsset();
  // set bridge, for instance the one defined at the tracker tester app
  tracker.setBridge(new JavaBridge() {
			@Override
			public void Log(Severity severity, String msg) {
				super.Log(severity, msg);
				textArea.setText(textArea.getText() + "\n\n " + (++infoCount)
						+ " -> " + severity + ": " + msg);
			}
  });
  
   ```

5. Further configuration can be done:
  ```java
    
  // Configure the options
  TrackerAssetSettings settings = new TrackerAssetSettings();

  settings.setHost(hostField.getText());
  settings.setPort(443);
  settings.setSecure(true);
  settings.setTraceFormat(TrackerAsset.TraceFormats.xapi);
  settings.setBasePath("/api/");

  tracker.setSettings(settings);

  ```
6. Optionally, login with user
1. Start the tracker by using `tracker.start()`
1. You can start sending traces now.
   
## Tracker Login and Start

For tracker to send traces to the server, `tracker.start()` has to be called. If you want to use an authenticated user, you can login before starting the tracker.

You can login with a username and password by calling the method `tracker.login(username, password);`. 

Then, you can start the tracker with either:
   * `tracker.start(userToken, trakingCode)`
   * `tracker.start(trackingCode)` with the already extracted usertoken (from login).
   * `tracker.start()` with an already extracted userToken (from login) and trackingCode (shown at game on A2 server).
   
## Sending Traces to the Analytics Server

There are two possible ways for sending traces:
1. **Recommended**: Using the xAPI for serious games interfaces (Accessible, Alternative, Completable and GameObject) (more info in the [user guide](https://github.com/crisal24/java-tracker#user-guide) below).
1. Using `TrackerAsset.ActionTrace(verb,target_type,target_id)` method. This is **not recomended unless you have clear in mind what you're doing**. Remember that xAPI traces are focused on sending actions, not purely variable changes. If you want to track variables, you can add them as extensions using `TrackerAsset.setVar(key, value)`. See below an example of use:

```java
// Simple trace
TrackerAsset.getInstance().getGameObject().used("GameObjectID", GameObjectTracker.TrackedGameObject.Item);

// Trace with extensions
TrackerAsset.getInstance().setVar("extension1", "value1");
TrackerAsset.getInstance().getAccessible().skipped("AccesibleID2", AccessibleTracker.Accessible.Screen);

// Complex trace
TrackerAsset.getInstance().setResponse("TheResponse");
TrackerAsset.getInstance().setScore(0.123f);
TrackerAsset.getInstance().setSuccess(false);
TrackerAsset.getInstance().setCompletion(true);
TrackerAsset.getInstance().setVar("extension1", "value1");
TrackerAsset.getInstance().setVar("extension2", "value2");
TrackerAsset.getInstance().setVar("extension3", 3);
TrackerAsset.getInstance().setVar("extension4", 4.56f);
TrackerAsset.getInstance().actionTrace("selected", "zone", "ObjectID3");

//Sending the traces
TrackerAsset.getInstance().flush();
```

## User Guide

There are two storage types available: net and local. The tracker requires (if `net` mode is on) the [RAGE Analytics](https://github.com/e-ucm/rage-analytics) infrastructure up and running. Check out the [Quickstart guide](https://github.com/e-ucm/rage-analytics/wiki/Quickstart) and follow the `developer` and `teacher` steps in order to create a game and [setup a class](https://github.com/e-ucm/rage-analytics/wiki/Set-up-a-class). It also requires a:

* **Host**: where the server is at. This value usually looks like `<rage_server_hostmane>/api/proxy/gleaner/collector/`. The [collector](https://github.com/e-ucm/rage-analytics/wiki/Back-end-collector) is an endpoint designed to retrieve traces and send them to the analysis pipeline.
* **Tracking code**: an unique tracking code identifying the game. [This code is created in the frontend](https://github.com/e-ucm/rage-analytics/wiki/Tracking-code), when creating a new game.

The tracker exposes an API designed to collect, analyze and visualize the data. The  API consists on defining a set of **game objects**. A game object represents an element of the game on which players can perform one or several types of interactions. Some examples of player's interactions are:

* start or complete (interaction) a level (game object)
* increase or decrease (interaction) the number of coins (game object)
* select or unlock (interaction) a power-up (game object)

A **gameplay** is the flow of interactions that a player performs over these game objects in a sequential order.

The main typed of game objects supported are:

* [Completable](https://github.com/e-ucm/java-tracker/blob/master/tracker/src/main/java/es/eucm/tracker/CompletableTracker.java) - for Game, Session, Level, Quest, Stage, Combat, StoryNode, Race or any other generic Completable. Methods: `Initialized`, `Progressed` and `Completed`.
* [Accessible](https://github.com/e-ucm/java-tracker/blob/master/tracker/src/main/java/es/eucm/tracker/AccessibleTracker.java) - for Screen, Area, Zone, Cutscene or any other generic Accessible. Methods: `Accessed` and `Skipped`.
* [Alternative](https://github.com/e-ucm/java-tracker/blob/master/tracker/src/main/java/es/eucm/tracker/AlternativeTracker.java) - for Question, Menu, Dialog, Path, Arena or any other generic Alternative. Methods: `Selected` and `Unlocked`.
* [GameObject](https://github.com/e-ucm/java-tracker/blob/master/tracker/src/main/java/es/eucm/tracker/GameObjectTracker.java) for Enemy, Npc, Item or any other generic GameObject. Methods: `Interacted` and `Used`.

#### Completable

Usage example for the tracking of an in-game quest. We decided to use a [Completable](https://github.com/e-ucm/java-tracker/blob/master/tracker/src/main/java/es/eucm/tracker/CompletableTracker.java) game object for this use-case as the most suitable option:

```java
// Completable

// Initialized
TrackerAsset.getInstance().getCompletable().initialized("MyGameQuestId", CompletableTracker.Completable.Quest);

// Progressed
float progress = 0.5f;
TrackerAsset.getInstance().getCompletable().progressed("MyGameQuestId", CompletableTracker.Completable.Quest, progress);

// Completed
TrackerAsset.getInstance().getCompletable().completed("MyGameQuestId", CompletableTracker.Completable.Quest);

// Completed with success and score
boolean success = true;
float score = 08f;
TrackerAsset.getInstance().getCompletable().completed("MyGameQuestId", CompletableTracker.Completable.Quest, success, score);

```
#### Accessible

Usage example for the tracking the player's movement through some in-game screens and skipping the 'Intro' cutscene:

```java
// Accessible

// Accessed
// The player accessed the 'MainMenu' screen
TrackerAsset.getInstance().getAccessible().accessed("MainMenu", AccessibleTracker.Accessible.Screen);

// Skipped
// The player skipped the 'Intro' cutscene
TrackerAsset.getInstance().getAccessible().skipped("Intro", AccessibleTracker.Accessible.Cutscene);

```

#### Alternative

Usage example for the tracking the player's choices during a conversation and unlocking an option in a menu:

```java
// Alternative

// Selected 
// The player selected the 'Ivan' answer for the question 'What's his name?'
TrackerAsset.getInstance().getAlternative().selected("what's his name?", "Ivan", AlternativeTracker.Alternative.Question);

// Unlocked
// The player unlocked 'Combat Mode' for the menu 'Menus/Start'
TrackerAsset.getInstance().getAlternative().unlocked("Menus/Start", "Combat Mode", AlternativeTracker.Alternative.Menu);

```

#### Tracked Game Object

Usage example for the tracking the player's with a NPC villager and using a health potion (item):

```java
// GameObject

// Interacted 
// The player interacted with an NPC
TrackerAsset.getInstance().getGameObject().interacted("GameObjectID", GameObjectTracker.TrackedGameObject.Npc);

// Used
// The player used an item
TrackerAsset.getInstance().getGameObject().used("GameObjectID", GameObjectTracker.TrackedGameObject.Item);

```


Note that in order to track other type of user interactions it is required to perform a previous analysis to identify the most suitable game objects ([Completable](https://github.com/e-ucm/java-tracker/blob/master/tracker/src/main/java/es/eucm/tracker/CompletableTracker.java), [Accessible](https://github.com/e-ucm/java-tracker/blob/master/tracker/src/main/java/es/eucm/tracker/AccessibleTracker.java), [Alternative](https://github.com/e-ucm/java-tracker/blob/master/tracker/src/main/java/es/eucm/tracker/AlternativeTracker.java), [GameObject](https://github.com/e-ucm/java-tracker/blob/master/tracker/src/main/java/es/eucm/tracker/GameObjectTracker.java)) for the given case. For instance, in order to track conversations alternatives are the best choice.

### Tracker Tester App

An app to test the tracker by sending traces is available in the swing-example folder.

### Tracker and Collector Flow
If the storage type is `net`, the tracker will try to connect to a `Collector` [endpoint](https://github.com/e-ucm/rage-analytics/wiki/Back-end-collector), exposed by the [rage-analytics Backend](https://github.com/e-ucm/rage-analytics-backend). 

More information about the tracker can be found in the [official documentation of rage-analytics](https://github.com/e-ucm/rage-analytics/wiki/Tracker).

## Useful Maven goals

- `mvn clean install`: run tests, check correct headers and generate `tracker-jar-with-dependencies.jar` file inside `tracker/target` folder
- `mvn clean test`: run tests checking tracker output
- `mvn license:check`: verify if some files miss license header. This goal is attached to the verify phase if declared in your pom.xml like above.
- `mvn license:format`: add the license header when missing. If a header is existing, it is updated to the new one.
- `mvn license:remove`: remove existing license header
