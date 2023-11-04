package com.tom.peripherals.gpu;

import java.util.HashMap;
import java.util.Map;

import com.tom.peripherals.api.LuaException;
import com.tom.peripherals.gpu.VRAM.VRAMObject;
import com.tom.peripherals.math.MathHelper;
import com.tom.peripherals.util.Image;
import com.tom.peripherals.util.ParamCheck;

public class TextureManager {
	private final VRAM vram;
	private Map<Integer, TextureManager.Texture> textureMap = new HashMap<>();
	private int lastTxID = 1;
	private TextureManager.Texture boundTexture;
	private boolean texEnabled;

	public TextureManager(VRAM vram) {
		this.vram = vram;
	}

	public int genTextureID(Object[] a) throws LuaException {
		int c = ParamCheck.optionalInt(a, 0, 1);
		int id = lastTxID;
		for (int i = 0; i < c; i++) {
			var t = new Texture(i + id);
			vram.allocEx(t, 1);
			textureMap.put(i + id, t);
		}
		lastTxID += c;
		return id;
	}

	public int sample(double u, double v, Triangle tri, double r, double g, double b, double a) {
		Object id = tri.uniforms[Uniform.TEXTURE.ordinal()];
		TextureManager.Texture tx = id == null ? null : textureMap.get((int) id);
		if (tx == null || !texEnabled) {
			int ri = (int) (r * 0xff) & 0xff;
			int gi = (int) (g * 0xff) & 0xff;
			int bi = (int) (b * 0xff) & 0xff;
			int ai = (int) (a * 0xff) & 0xff;
			return ai << 24 | ri << 16 | gi << 8 | bi;
		} else {
			int w = tx.pixels.getWidth();
			int h = tx.pixels.getHeight();
			int iu = (int) (u * w);
			int iv = (int) (v * h);
			if (iu < 0) iu += Math.ceil((-iu) / (double) w) * w;
			if (iv < 0) iv += Math.ceil((-iv) / (double) h) * h;
			iu = iu % w;
			iv = iv % h;
			int c = tx.pixels.getRGB(iu, iv);
			int ri = colorMul(c >> 16, r);
			int gi = colorMul(c >> 8, g);
			int bi = colorMul(c, b);
			int ai = colorMul(c >>> 24, a);
			return ai << 24 | ri << 16 | gi << 8 | bi;
		}
	}

	private static int colorMul(int c1, double v) {
		double c = (c1 & 0xFF) / 255d;
		double r = MathHelper.clamp(c * v, 0, 1);
		return (int) (r * 255);
	}

	public void deleteTextures(Object[] a) throws LuaException {
		if (a.length == 0) throw new LuaException("Too few arguments: number texture ids...");
		ParamCheck.ints(a, 0).forEach(t -> {
			Texture tx = textureMap.remove(t);
			if (tx != null)vram.free(tx);
		});
	}

	public void bindTexture(Object[] a) throws LuaException {
		if (a.length == 0) throw new LuaException("Too few arguments: number id");
		int id = ParamCheck.getInt(a, 0);
		boundTexture = textureMap.get(id);
	}

	public void texImage(Object[] a) throws LuaException {
		if (a.length < 1) throw new LuaException("Too few arguments: image ref or number width, numbers texture...");
		if (boundTexture == null) throw new LuaException("No bound texture");
		if (a.length == 1 && a[0] instanceof String r) {
			if (!(vram.getRefMngr().getByReference(r) instanceof LuaImage img))
				throw new LuaException("Invalid reference");
			vram.reallocEx(boundTexture, img.getSize());
			boundTexture.pixels = new Image(img.getImage());
		} else {
			int w = ParamCheck.getInt(a, 0);
			int[] d = ParamCheck.uints(a, 1).toArray();
			int h = d.length / w;
			vram.reallocEx(boundTexture, w * h * 4);
			boundTexture.pixels = new Image(d, w);
		}
	}

	public int getTextureID() {
		return !texEnabled || boundTexture == null ? 0 : boundTexture.id;
	}

	public void setTexEnabled(boolean texEnabled) {
		this.texEnabled = texEnabled;
	}

	public static class Texture implements VRAMObject {
		private final int id;

		public Texture(int i) {
			this.id = i;
		}

		private Image pixels;

		@Override
		public long getSize() {
			return pixels != null ? pixels.getWidth() * pixels.getHeight() * 4 : 1;
		}
	}
}