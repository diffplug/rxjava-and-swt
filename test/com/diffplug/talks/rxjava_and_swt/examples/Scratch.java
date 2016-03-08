/*
 * Copyright 2016 DiffPlug
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
package com.diffplug.talks.rxjava_and_swt.examples;

import rx.Subscription;

import com.diffplug.common.swt.SwtExec;

// @formatter:off
public class Scratch {
	public interface Observable<T> {
		Subscription subscribe(Observer<? super T> observer);
	}

	public interface Observer<T> {
		/** Gets called 0 to infinity times. */
		void onNext(T t);
		void onComplete();
		void onError(Throwable e);
	}

	public static void main(String[] args) {
		SwtExec.async();
		SwtExec.blocking();
		SwtExec.immediate();
	}
}
// @formatter:on
