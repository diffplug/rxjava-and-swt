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

import rx.Observable;

/** Represents the price of a given stock ticker. */
public class StockPrice implements AutoCloseable {
	/** Connects to the NYSE to get the price for the given stock. */
	public static StockPrice connectTo(String symbol) {
		return new StockPrice(symbol);
	}

	private final String symbol;
	private final RandomWalk price = new RandomWalk(0, 100);

	private StockPrice(String symbol) {
		this.symbol = symbol;
	}

	/** Tthe stock symbol. */
	public String getSymbol() {
		return symbol;
	}

	/** The realtime price of the stock. */
	public Observable<Double> rxPrice() {
		return price.asObservable();
	}

	@Override
	public void close() throws Exception {
		price.close();
	}
}
