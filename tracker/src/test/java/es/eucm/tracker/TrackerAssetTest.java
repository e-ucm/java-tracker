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

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

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

}
