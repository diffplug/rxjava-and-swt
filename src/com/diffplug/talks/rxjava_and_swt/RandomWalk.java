/*
 * Copyright 2015 DiffPlug
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
package com.diffplug.talks.rxjava_and_swt;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.subjects.BehaviorSubject;

import com.google.common.base.Preconditions;

/** Models a random walk. */
class RandomWalk implements AutoCloseable {
	private final double min, max;
	private final BehaviorSubject<Double> subject;
	private boolean isOpen = true;

	RandomWalk(double min, double max) {
		Preconditions.checkArgument(max > min);
		this.min = min;
		this.max = max;
		subject = BehaviorSubject.create(Math.random() * (max - min) + min);
		update();
	}

	/** Updates the value according to a random walk. */
	private void update() {
		if (isOpen) {
			double nextValue = subject.getValue() + (max - min) * Math.random() * VOLATILITY;
			nextValue = Math.max(min, Math.min(max, nextValue));
			subject.onNext(nextValue);
			scheduler.schedule(this::update, UPDATE_TIME, UPDATE_UNIT);
		}
	}

	private static final int UPDATE_TIME = 100;
	private static final TimeUnit UPDATE_UNIT = TimeUnit.MILLISECONDS;
	private static final double VOLATILITY = 0.1;
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	/** Returns the current value of this RandomWalk. */
	public Observable<Double> asObservable() {
		return subject.asObservable();
	}

	@Override
	public void close() throws Exception {
		isOpen = false;
	}
}
