package com.tom.peripherals.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.tom.peripherals.math.Vec2i;

public interface IImageIO {
	Image read(InputStream is) throws IOException;
	void write(Image img, OutputStream os) throws IOException;
	Vec2i getSize(InputStream din) throws IOException;
}
