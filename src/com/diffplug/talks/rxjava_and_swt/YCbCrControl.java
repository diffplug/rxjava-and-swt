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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import com.diffplug.common.swt.ControlWrapper;

/** Shows a pane of CbCr at constant Y. */
public class YCbCrControl extends ControlWrapper.AroundControl<Canvas> {
	int luminance = 128;

	public YCbCrControl(Composite parent) {
		super(new Canvas(parent, SWT.DOUBLE_BUFFERED));
		wrapped.addListener(SWT.Paint, e -> {
			Point size = wrapped.getSize();

			double dCbdX = _256 / size.x;
			double dCrdY = _256 / size.y;

			for (int x = 0; x < size.x; ++x) {
				int cb = limitFloor(dCbdX * x);
				for (int y = 0; y < size.y; ++y) {
					int cr = limitFloor(dCrdY * y);
					RGB rgb = fromYCbCr(new RGB(luminance, cb, cr));
					e.gc.setForeground(new Color(e.display, rgb));
					e.gc.drawPoint(x, y);
				}
			}
		});
	}

	private static final double _256 = 256.0;

	/** Adds a listener to the underlying Canvas. */
	public void addListener(int eventType, Listener listener) {
		wrapped.addListener(eventType, listener);
	}

	/** Returns the RGB value at the given x/y point. */
	public RGB rgbForPoint(int x, int y) {
		Point size = wrapped.getSize();
		int cb = limitInt(256 * x / size.x);
		int cr = limitInt(256 * y / size.y);
		return fromYCbCr(luminance, cb, cr);
	}

	/** Sets the luminance value of the pane. */
	public void setY(int y) {
		this.luminance = limitInt(y);
		wrapped.redraw();
	}

	/** Converts an RGB value to YCrCb. */
	public static RGB toYCbCr(RGB rgb) {
		return toYCbCr(rgb.red, rgb.green, rgb.blue);
	}

	/** Converts an RGB value to YCrCb. */
	public static RGB toYCbCr(double r, double g, double b) {
		int y = limitRound(0.299 * r + 0.587 * g + 0.114 * b);
		int cb = limitRound(128 - 0.168736 * r - 0.331264 * g + 0.5 * b);
		int cr = limitRound(128 + 0.5 * r - 0.418688 * g - 0.081312 * b);
		return new RGB(y, cb, cr);
	}

	/** Converts a YCrCb value to RGB. */
	public static RGB fromYCbCr(RGB YCbCr) {
		return fromYCbCr(YCbCr.red, YCbCr.green, YCbCr.blue);
	}

	/** Converts a YCrCb value to RGB. */
	public static RGB fromYCbCr(double y, double cb, double cr) {
		int r = limitRound(y + 1.402 * (cr - 128));
		int g = limitRound(y - 0.34414 * (cb - 128) - 0.71414 * (cr - 128));
		int b = limitRound(y + 1.772 * (cb - 128));
		return new RGB(r, g, b);
	}

	/** Rounds and limits to the range 0-255. */
	private static int limitRound(double value) {
		return limitInt((int) Math.round(value));
	}

	/** Floors and limits to the range 0-255. */
	private static int limitFloor(double value) {
		return limitInt((int) Math.floor(value));
	}

	/** Limits to the range 0-255. */
	private static int limitInt(int value) {
		if (value < 0) {
			return 0;
		} else if (value > 255) {
			return 255;
		} else {
			return value;
		}
	}
}
