package com.tom.peripherals.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tom.peripherals.math.Vec2i;

public class ImageIO {
	public static IImageIO handler = new AWTImageIO();

	public static Image read(InputStream is) throws IOException {
		return handler.read(is);
	}

	public static void write(Image img, OutputStream os) throws IOException {
		handler.write(img, os);
	}

	public static Vec2i getSize(InputStream din) throws IOException {
		return handler.getSize(din);
	}
}
