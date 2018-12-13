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

## Sending traces to the Analytics Server

## User Guide

### Completable

### Accessible

### Alternative

### Tracked Game Object

## Useful Maven goals

- `mvn clean install`: run tests, check correct headers and generate `tracker-jar-with-dependencies.jar` file inside `tracker/target` folder
- `mvn clean test`: run tests checking tracker output
- `mvn license:check`: verify if some files miss license header. This goal is attached to the verify phase if declared in your pom.xml like above.
- `mvn license:format`: add the license header when missing. If a header is existing, it is updated to the new one.
- `mvn license:remove`: remove existing license header
