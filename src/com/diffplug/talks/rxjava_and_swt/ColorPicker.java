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

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Widget;

import rx.Observable;

import com.google.common.collect.Maps;

import com.diffplug.common.base.Errors;
import com.diffplug.common.rx.Rx;
import com.diffplug.common.rx.RxBox;
import com.diffplug.common.swt.ControlWrapper;
import com.diffplug.common.swt.Layouts;
import com.diffplug.common.swt.SwtExec;

public class ColorPicker extends ControlWrapper.AroundControl<Composite> {
	final RxBox<RGB> rgb = RxBox.of(new RGB(0, 0, 0));
	final RxBox<RGB> yCbCr = rgb.map(YCbCrControl::toYCbCr, YCbCrControl::fromYCbCr);
	final XkcdColors xkcd = XkcdColors.load();

	public ColorPicker(Composite parent, RGB initRGB) {
		super(new Composite(parent, SWT.NONE));
		RGB initYCbCr = YCbCrControl.toYCbCr(initRGB);

		// create a scale and bind it to an RxBox<Integer>
		Scale scale = new Scale(wrapped, SWT.HORIZONTAL);
		scale.setMinimum(0);
		scale.setMaximum(255);
		RxBox<Integer> luminance = RxBox.of(initYCbCr.red);
		Rx.subscribe(luminance, scale::setSelection);
		scale.addListener(SWT.Selection, e -> {
			luminance.set(scale.getSelection());
		});

		// create a color panel
		YCbCrControl cbcrPanel = new YCbCrControl(wrapped);
		Rx.subscribe(luminance, cbcrPanel::setY);

		// panel at the bottom
		Composite bottomCmp = new Composite(wrapped, SWT.NONE);
		Layouts.setGrid(wrapped);
		Layouts.setGridData(scale).grabHorizontal();
		Layouts.setGridData(cbcrPanel).grabAll();
		Layouts.setGridData(bottomCmp).grabHorizontal();

		// populate the bottom
		Layouts.setGrid(bottomCmp).numColumns(2).margin(0);
		XkcdLookup xkcdLookup = new XkcdLookup(bottomCmp);

		Group hoverGrp = new Group(bottomCmp, SWT.SHADOW_ETCHED_IN);
		hoverGrp.setText("Hover");
		createGroup(hoverGrp, cbcrPanel.rxMouseMove(), xkcdLookup);

		Group clickGrp = new Group(bottomCmp, SWT.SHADOW_ETCHED_IN);
		clickGrp.setText("Click");
		createGroup(clickGrp, cbcrPanel.rxMouseDown(), xkcdLookup);
	}

	private void createGroup(Composite parent, Observable<RGB> rxRgb, XkcdLookup xkcdLookup) {
		Layouts.setFill(parent);
		ColorCompareBox colorCompare = new ColorCompareBox(parent);
		CancellingBox<Object> cancelling = new CancellingBox<>();

		SwtExec.Guarded guarded = SwtExec.async().guardOn(parent);
		guarded.subscribe(rxRgb, rgb -> {
			// set raw
			colorCompare.setActual(rgb);
			// clear empty, then start to look for the answer
			colorCompare.setNearestEmpty();
			guarded.subscribe(cancelling.filter(xkcdLookup.get(rgb)), entry -> {
				colorCompare.setNearest(entry.getKey(), entry.getValue());
			});
		});
	}

	public static class XkcdLookup {
		final XkcdColors colors = XkcdColors.load();
		final ExecutorService executor;

		public XkcdLookup(Widget lifecycle) {
			this.executor = Executors.newSingleThreadExecutor();
			lifecycle.addListener(SWT.Dispose, e -> {
				executor.shutdown();
			});
		}

		/** Returns the XkcdColor which is closest to the given RGB. */
		public CompletableFuture<Map.Entry<String, RGB>> get(RGB rgb) {
			return CompletableFuture.supplyAsync(() -> closestTo(rgb), executor);
		}

		/** Iterates over all XkcdColor entries to find the closest color by brute-force. */
		private Map.Entry<String, RGB> closestTo(RGB rgb) {
			Errors.log().run(() -> Thread.sleep(10));
			return colors.all().stream()
					.map(entry -> Maps.immutableEntry(distance(entry.getValue(), rgb), entry))
					.min(Comparator.comparing(Map.Entry::getKey))
					.get().getValue();
		}

		/** Computes the distance-squared between the two colors. */
		private static int distance(RGB a, RGB b) {
			int deltaR = a.red - b.red;
			int deltaG = a.green - b.green;
			int deltaB = a.blue - b.blue;
			return (deltaR * deltaR) + deltaG * deltaG + deltaB * deltaB;
		}
	}
}
