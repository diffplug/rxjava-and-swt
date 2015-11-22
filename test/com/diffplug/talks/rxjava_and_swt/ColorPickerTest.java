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

import org.eclipse.swt.graphics.RGB;
import org.junit.Test;

import com.diffplug.common.swt.InteractiveTest;
import com.diffplug.common.swt.Layouts;

public class ColorPickerTest {
	/** Feature for picking colors. */
	@Test
	public void colorPicker() {
		InteractiveTest.testCoat("Selects colors in a fancy way.", cmp -> {
			Layouts.setFill(cmp).margin(0);
			new ColorPicker(cmp, new RGB(100, 50, 150));
		});
	}
}
