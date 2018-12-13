# Java xAPI Tracker

[![Build Status](https://travis-ci.org/e-ucm/java-tracker.svg)](https://travis-ci.org/e-ucm/java-tracker) [![Coverage Status](https://coveralls.io/repos/e-ucm/java-tracker/badge.svg?branch=master&service=github)](https://coveralls.io/github/e-ucm/java-tracker?branch=master)

xAPI traces sent by games should comply with the [xAPI for serious games specification](https://github.com/e-ucm/xapi-seriousgames).

## Installation
1. Clone or download repository
1. Copy into your project folder
1. Import the Java Tracker into your code:
  ```java
  import es.eucm.tracker.*;
  ```
4. Configure the tracker by:
  ```java
  TrackerAsset tracker = new TrackerAsset();

  ```
5. Start the tracker by using either
   * `tracker.start(userToken, trakingCode)`
   * `tracker.start(trakingCode)` with the already extracted usertoken
   * `tracker.start()` with an already extracted userToken and trackingCode

## User Guide

The tracker exposes an API designed to collect, analyze and visualize the data. The  API consists on defining a set of **game objects**. A game object represents an element of the game on which players can perform one or several types of interactions. Some examples of player's interactions are:

* start or complete (interaction) a level (game object)
* increase or decrease (interaction) the number of coins (game object)
* select or unlock (interaction) a power-up (game object)

A **gameplay** is the flow of interactions that a player performs over these game objects in a sequential order.

The main typed of game objects supported are:

* [Completable](https://github.com/e-ucm/csharp-tracker/blob/3c56f43a53e69c10b031887419113ac2817afd96/TrackerAsset/Interfaces/CompletableTracker.cs) - for Game, Session, Level, Quest, Stage, Combat, StoryNode, Race or any other generic Completable. Methods: `Initialized`, `Progressed` and `Completed`.
* [Accessible](https://github.com/e-ucm/csharp-tracker/blob/3c56f43a53e69c10b031887419113ac2817afd96/TrackerAsset/Interfaces/AccessibleTracker.cs) - for Screen, Area, Zone, Cutscene or any other generic Accessible. Methods: `Accessed` and `Skipped`.
* [Alternative](https://github.com/e-ucm/csharp-tracker/blob/3c56f43a53e69c10b031887419113ac2817afd96/TrackerAsset/Interfaces/AlternativeTracker.cs) - for Question, Menu, Dialog, Path, Arena or any other generic Alternative. Methods: `Selected` and `Unlocked`.
* [TrackedGameObject](https://github.com/e-ucm/csharp-tracker/blob/3c56f43a53e69c10b031887419113ac2817afd96/TrackerAsset/Interfaces/GameObjectTracker.cs) for Enemy, Npc, Item or any other generic GameObject. Methods: `Interacted` and `Used`.

#### Completable

Usage example for the tracking of an in-game quest. We decided to use a [Completable](https://github.com/e-ucm/csharp-tracker/blob/3c56f43a53e69c10b031887419113ac2817afd96/TrackerAsset/Interfaces/CompletableTracker.cs) game object for this use-case as the most suitable option:

#### Accessible

Usage example for the tracking the player's movement through some in-game screens and skipping the `Intro` cutscene:

#### Alternative

Usage example for the tracking the player's choices during a conversation:

#### Tracked Game Object

Usage example for the tracking the player's with a NPC villager and using a health potion (item):

Note that in order to track other type of user interactions it is required to perform a previous analysis to identify the most suitable game objects ([Completable](https://github.com/e-ucm/csharp-tracker/blob/3c56f43a53e69c10b031887419113ac2817afd96/TrackerAsset/Interfaces/CompletableTracker.cs), [Accessible](https://github.com/e-ucm/csharp-tracker/blob/3c56f43a53e69c10b031887419113ac2817afd96/TrackerAsset/Interfaces/AccessibleTracker.cs), [Alternative](https://github.com/e-ucm/csharp-tracker/blob/3c56f43a53e69c10b031887419113ac2817afd96/TrackerAsset/Interfaces/AlternativeTracker.cs), [TrackedGameObject](https://github.com/e-ucm/csharp-tracker/blob/3c56f43a53e69c10b031887419113ac2817afd96/TrackerAsset/Interfaces/GameObjectTracker.cs)) for the given case. For instance, in order to track conversations [alternatives](https://github.com/e-ucm/csharp-tracker/blob/3c56f43a53e69c10b031887419113ac2817afd96/TrackerAsset/Interfaces/AlternativeTracker.cs) are the best choice.

## Tracker Tester App

swing-example..

## Useful Maven goals

- `mvn clean install`: run tests, check correct headers and generate `tracker-jar-with-dependencies.jar` file inside `tracker/target` folder
- `mvn clean test`: run tests checking tracker output
- `mvn license:check`: verify if some files miss license header. This goal is attached to the verify phase if declared in your pom.xml like above.
- `mvn license:format`: add the license header when missing. If a header is existing, it is updated to the new one.
- `mvn license:remove`: remove existing license header
