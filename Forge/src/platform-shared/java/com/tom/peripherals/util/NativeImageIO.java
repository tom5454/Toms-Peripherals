package com.tom.peripherals.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;

import com.tom.peripherals.screen.gpu.GLConstants.Vec2i;

public class NativeImageIO implements ImageIO {

	@Override
	public Image read(InputStream f) throws IOException {
		try(NativeImage ni = NativeImage.read(f)) {
			Image i = new Image(ni.getWidth(), ni.getHeight());
			for(int y = 0;y<ni.getHeight();y++) {
				for(int x = 0;x<ni.getWidth();x++) {
					int rgb = ni.getPixelRGBA(x, y);
					int a = (rgb >> 24 & 255);
					int b = (rgb >> 16 & 255);
					int g = (rgb >> 8 & 255);
					int r = (rgb & 255);
					i.setRGB(x, y, (a << 24) | (r << 16) | (g << 8) | b);
				}
			}
			return i;
		}
	}

	@Override
	public Vec2i getSize(InputStream din) throws IOException {
		ByteBuffer byteBufferIn = null;
		try (MemoryStack memorystack = MemoryStack.stackPush()) {
			byteBufferIn = TextureUtil.readResource(din);
			((Buffer)byteBufferIn).rewind();
			IntBuffer intbuffer = memorystack.mallocInt(1);
			IntBuffer intbuffer1 = memorystack.mallocInt(1);
			IntBuffer intbuffer2 = memorystack.mallocInt(1);
			if(!STBImage.stbi_info_from_memory(byteBufferIn, intbuffer, intbuffer1, intbuffer2)) {
				throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
			}
			return new Vec2i(intbuffer.get(0), intbuffer1.get(0));
		} finally {
			MemoryUtil.memFree(byteBufferIn);
		}
	}

	@Override
	public void write(Image img, OutputStream f) throws IOException {
		try (NativeImage ni = createFromBufferedImage(img)) {
			f.write(ni.asByteArray());
		}
	}

	public static NativeImage createFromBufferedImage(Image texture) {
		NativeImage ni = new NativeImage(texture.getWidth(), texture.getHeight(), false);
		for(int y = 0;y<texture.getHeight();y++) {
			for(int x = 0;x<texture.getWidth();x++) {
				int rgb = texture.getRGB(x, y);
				int a = (rgb >> 24 & 255);
				int r = (rgb >> 16 & 255);
				int g = (rgb >> 8 & 255);
				int b = (rgb & 255);
				ni.setPixelRGBA(x, y, (a << 24) | (b << 16) | (g << 8) | r);
			}
		}
		return ni;
	}
}
