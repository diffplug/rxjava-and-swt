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
package com.diffplug.talks.rxjava_and_swt.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.diffplug.common.base.Either;
import com.diffplug.common.rx.Rx;
import com.diffplug.common.rx.RxGetter;
import com.diffplug.common.swt.InteractiveTest;
import com.diffplug.common.swt.Layouts;
import com.diffplug.common.swt.SwtRx;

@Category(InteractiveTest.class)
public class EventVsFrp {
	private String msgForValue(int value) {
		return "Value = " + value;
	}

	private String msgForError(Exception error) {
		return "Error = " + error.getMessage();
	}

	@Test
	public void eventBased() {
		InteractiveTest.testCoat("Event-based example", 20, 0, cmp -> {
			Text inputField = new Text(cmp, SWT.BORDER | SWT.SINGLE);
			Label outputField = new Label(cmp, SWT.NONE);
			inputField.addListener(SWT.Modify, e -> {
				try {
					int parsed = Integer.parseInt(inputField.getText());
					outputField.setText(msgForValue(parsed));
				} catch (Exception error) {
					outputField.setText(msgForError(error));
				}
			});

			Layouts.setGrid(cmp);
			Layouts.setGridData(inputField).grabHorizontal();
			Layouts.setGridData(outputField).grabHorizontal();
		});
	}

	@Test
	public void frpBased() {
		InteractiveTest.testCoat("Event-based example", 20, 0, cmp -> {
			Text inputField = new Text(cmp, SWT.BORDER | SWT.SINGLE);
			Label outputField = new Label(cmp, SWT.NONE);
			RxGetter<Either<Integer, Exception>> value = SwtRx.textImmediate(inputField).map(text -> {
				try {
					int parsed = Integer.parseInt(inputField.getText());
					return Either.createLeft(parsed);
				} catch (Exception error) {
					return Either.createRight(error);
				}
			});
			RxGetter<String> message = value.map(either -> {
				return either.fold(this::msgForValue, this::msgForError);
			});
			Rx.subscribe(message, outputField::setText);

			Layouts.setGrid(cmp);
			Layouts.setGridData(inputField).grabHorizontal();
			Layouts.setGridData(outputField).grabHorizontal();
		});
	}
}
