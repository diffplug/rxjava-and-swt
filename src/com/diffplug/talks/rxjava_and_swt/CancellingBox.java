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

import java.util.concurrent.CompletableFuture;

import com.diffplug.common.base.Box;

public class CancellingBox<T> extends Box.Default<CompletableFuture<T>> {
	public CancellingBox() {
		super(new CompletableFuture<T>());
	}

	@Override
	public void set(CompletableFuture<T> obj) {
		this.obj.cancel(true);
		this.obj = obj;
	}

	@SuppressWarnings("unchecked")
	public <U extends T> CompletableFuture<U> filter(CompletableFuture<U> obj) {
		set((CompletableFuture<T>) obj);
		return obj;
	}
}
