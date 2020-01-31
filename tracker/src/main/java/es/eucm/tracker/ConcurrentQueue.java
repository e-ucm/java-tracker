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
//
// Translated by CS2J (http://www.cs2j.com): 05/11/2018 15:29:15
//

package es.eucm.tracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ConcurrentQueue<T> {
	private final Object syncLock = new Object();
	private LinkedList<T> queue = new LinkedList<T>();

	public int getCount() {
		synchronized (syncLock) {
			{
				return queue.size();
			}
		}
	}

	public ConcurrentQueue() {
		this.queue = new LinkedList<T>();
	}

	public List<T> peek(Integer n) {
		synchronized (syncLock) {
			{
				n = Math.min((Integer) queue.size(), n);
				ArrayList<T> tmp = new ArrayList<T>();
				Iterator<T> it = queue.iterator();
				while (it.hasNext()) {
					tmp.add(it.next());
				}

				// puede fallar
				return tmp;
			}
		}
	}

	public void enqueue(T obj) {
		synchronized (syncLock) {
			{
				queue.addLast(obj);
			}
		}
	}

	public T dequeue() {
		synchronized (syncLock) {
			{
				T tmp = queue.getFirst();
				queue.removeFirst();
				return tmp;
			}
		}
	}

	public void dequeue(int n) {
		synchronized (syncLock) {
			{
				for (int i = 0; i < n; i++) {
					queue.removeFirst();
				}
			}
		}
	}

	public void clear() {
		synchronized (syncLock) {
			{
				queue.clear();
			}
		}
	}

	public T[] copyToArray() {
		synchronized (syncLock) {
			{
				return (T[]) queue.toArray();
			}
		}
	}

	public ConcurrentQueue<T> initFromArray(List<T> initValues) {
		this.queue = new LinkedList<T>();
		this.queue.addAll(initValues);

		return this;
	}

}
