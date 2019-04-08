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
package es.eucm.tracker.formalz;

import java.util.Random;

import es.eucm.tracker.CompletableTracker.Completable;
import es.eucm.tracker.TrackerAsset;
import es.eucm.tracker.TrackerAssetSettings;
import es.eucm.tracker.AlternativeTracker.Alternative;
import eu.rageproject.asset.manager.Severity;

public class FormalZDemo {

	public static final int TOWER_COST = 20;
	public static void main(String[] args) {
		FormalZDemo demo = new FormalZDemo();
		demo.run();
	}
	

	private TrackerAssetSettings settings;
	
	private TrackerAsset tracker;
	
	private GameState gameState;
	
	private Random rnd = new Random();

	public void run() {
		this.tracker = new TrackerAsset();
		this.tracker.setBridge(new JavaBridge() {
			@Override
			public void log(Severity severity, String msg) {
				super.log(severity, msg);
				System.out.println("Severity: " + severity + " Message: " + msg);
			}
		});
		
		this.settings = createSettings();
		this.tracker.setSettings(settings);

		this.tracker.start();
		sendTraces();
		this.tracker.flush();
	}
	
	private TrackerAssetSettings createSettings() {
		TrackerAssetSettings settings = new TrackerAssetSettings();

		settings.setHost("analytics.e-ucm.es");
		settings.setPort(443);
		settings.setSecure(true);
		settings.setTraceFormat(TrackerAssetSettings.TraceFormats.XAPI);
		settings.setBasePath("/api/");
		settings.setTrackingCode("5c8fe8d75e3d6c0080bfa9cbnia2r6s4k39");

		return settings;
	}

	private void sendTraces() {
		this.gameState = new GameState();
		gameState.money = 100;
		gameState.towers = 0;
	  gameState.lives = 20;
		
		sendGameStart();

		int maxWaves = getRandomInt(4, 8);
		int accumulatedGameplayDuration = 0;		
		int previous_time = 0;
		for (int currentWave = 0; currentWave < maxWaves; currentWave++) {
			System.out.println("---------- wave: " + currentWave + " ----------");
			
			accumulatedGameplayDuration += getRandomInt(30, 120);
			
			int current_time = accumulatedGameplayDuration - previous_time;

			float progress = ((float)currentWave) / (maxWaves-1);
			float proximity = 1.0f - progress;

			// Build pre-condition
			tracker.setVar("time", accumulatedGameplayDuration);
			int prewritingtime =  getRandomInt(10, (int)Math.floor(current_time * 0.3));
			tracker.setVar("writing_time", prewritingtime);
			sendBuiltCondition("pre", modifyNumber(proximity, 0.2f));

			// Build post-codition
			tracker.setVar("time", accumulatedGameplayDuration);
			int postwritingtime =  getRandomInt(10, (int) Math.floor(current_time * 0.3));
			tracker.setVar("writing_time", postwritingtime);
			sendBuiltCondition("post", modifyNumber(proximity, 0.2f));

			// Build towers
			int possibletowers = getRandomInt(0, (gameState.money / TOWER_COST));
			for (int j = 0; j < possibletowers; j++) {
				buildTower();
			}

			// Check pre-post
			doWave(accumulatedGameplayDuration, current_time - prewritingtime - postwritingtime - 10, proximity);

			// Send game progress
			sendGameProgress(progress);
			previous_time = accumulatedGameplayDuration;
		}

		sendGameEnd();
	}

	private void sendGameEnd() {
		appendGameState();
		tracker.getCompletable().completed("level1", Completable.Level, true, 1);
	}

	private void sendGameProgress(float progress) {
		appendGameState();
		tracker.getCompletable().progressed("level1", Completable.Level, progress);
	}

	private void doWave(int time, int duration, float proximity) {
		sendWaveStart();

		int difficulty = 5;

		for (int i = 0; i < 5; i++) {
			tracker.setVar("time", time + ((duration/5) * i));

			if(gameState.lives == 1 || getRandomInt(0,Math.min(difficulty,gameState.towers)) > difficulty/3){
				sendEnemyKilled(i/5);
				continue;
			}
			sendLiveLost(i/5);
		}

		sendWaveEnd(proximity);
	}


	private void sendLiveLost(float progress) {
		gameState.lives--;

		appendGameState();
		tracker.getCompletable().progressed("wave", Completable.Level, progress);
	}

	private void sendEnemyKilled(float progress) {
		gameState.money+=10;
		appendGameState();		
		tracker.getCompletable().progressed("wave", Completable.Stage, progress);
	}

	private void sendWaveStart() {
		appendGameState();
		tracker.getCompletable().initialized("wave", Completable.Stage);
	}

	private void sendWaveEnd(float proximity) {
		appendGameState();
		tracker.getCompletable().completed("wave", Completable.Stage, proximity == 1);
	}
	
	private void buildTower() {
		if (gameState.money > TOWER_COST) {
			gameState.money -= TOWER_COST;
			gameState.towers++;
		}
	}

	private void sendBuiltCondition(String type, float proximity) {
		tracker.setSuccess(proximity == 0);
		tracker.setScore(proximity);

		int complexity = (int) (3 - Math.ceil(proximity * 3));
		tracker.getAlternative().selected(type, getRandomAssert(complexity), Alternative.Question);
	}

	private static final String[] options = new String[] {"a", "b", "c", "d", "e", "f", "g", "1", "2", "3", "4", "5", "6"};
	private static final String[] operators = new String[] {">", "<", ">=", "<=", "&&", "||", "+", "-", "=="};
	
	private String getRandomAssert(int complexity) {
		StringBuilder buffer = new StringBuilder();

		if(complexity > 0){
			if(getRandomInt(0, 1) == 1){
				buffer.append(getRandomAssert(getRandomInt(0, complexity-1)))
				.append(' ')
				.append(operators[getRandomInt(0,operators.length-1)])
				.append(' ')
				.append(getRandomAssert(complexity-1));
			}else{
				buffer.append(getRandomAssert(complexity-1))
				.append(' ')
				.append(operators[getRandomInt(0,operators.length-1)])
				.append(' ')
				.append(getRandomAssert(getRandomInt(0, complexity-1)));
			}
		}else{
			if(getRandomInt(0, 1) == 1){
				buffer.append(options[getRandomInt(0,operators.length-1)]);
			}else{
				buffer.append(options[getRandomInt(0,operators.length-1)])
				.append(' ')
				.append(operators[getRandomInt(0,operators.length-1)])
				.append(' ')
				.append(options[getRandomInt(0,operators.length-1)]);
			}
		}

		String result = (getRandomInt(0, 1) == 1) ? '(' + buffer.toString() + ')' : buffer.toString();
		return result;
	}
	
	private float modifyNumber(float number, float quantity) {
		if(number == 0.0f) {
			return number;
		}
			
		return ((float)getRandomInt((int)Math.max(number - quantity, 0)*100, (int)Math.min(number + quantity, 1)*100)) / 100;
	}

	private int getRandomInt(int min, int max) {
		return min + rnd.nextInt(max-min+1);
	}

	private void sendGameStart() {
		appendGameState();
		this.tracker.getCompletable().initialized("level1", Completable.Level);
	}

	private void appendGameState() {
		tracker.setVar("money", gameState.money);
		tracker.setVar("towers", gameState.towers);
		tracker.setVar("lives", gameState.lives);
	}

}

class GameState {
	public int money = 100;
	public int towers = 0;
	public int lives = 20;
}
