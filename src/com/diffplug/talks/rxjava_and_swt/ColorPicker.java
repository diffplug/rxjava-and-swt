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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

import com.diffplug.common.rx.Rx;
import com.diffplug.common.rx.RxBox;
import com.diffplug.common.swt.ControlWrapper;
import com.diffplug.common.swt.Layouts;
import com.diffplug.common.swt.SwtRx;

public class ColorPicker extends ControlWrapper.AroundControl<Composite> {
	final RxBox<RGB> rgb = RxBox.of(new RGB(0, 0, 0));
	final RxBox<RGB> yCbCr = rgb.map(rgb -> YCbCrControl.toYCbCr(rgb), ycbcr -> YCbCrControl.fromYCbCr(ycbcr));
	final XkcdColors xkcd = XkcdColors.load();

	public ColorPicker(Composite parent, RGB initRGB) {
		super(new Composite(parent, SWT.NONE));
		RGB initYCbCr = YCbCrControl.toYCbCr(initRGB);

		RxBox<Integer> luminance = RxBox.of(initYCbCr.red);
		Scale scale = new Scale(wrapped, SWT.HORIZONTAL);
		scale.setMinimum(0);
		scale.setMaximum(255);
		Rx.subscribe(luminance, scale::setSelection);
		scale.addListener(SWT.Selection, e -> {
			yCbCr.modify(val -> {
				return new RGB(scale.getSelection(), val.green, val.blue);
			});
		});

		YCbCrControl cbcrPanel = new YCbCrControl(wrapped);
		Text name = new Text(wrapped, SWT.BORDER | SWT.SINGLE);

		RxBox<String> nameTxt = SwtRx.textImmediate(name);
		Rx.subscribe(nameTxt, txt -> {
			xkcd.rgbForName(txt).ifPresent(rgb::set);
		});

		Layouts.setGrid(wrapped);
		Layouts.setGridData(scale).grabHorizontal();
		Layouts.setGridData(cbcrPanel).grabAll();
		Layouts.setGridData(name).grabHorizontal();
	}
}
