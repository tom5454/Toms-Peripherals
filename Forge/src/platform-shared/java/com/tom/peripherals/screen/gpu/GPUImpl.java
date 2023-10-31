package com.tom.peripherals.screen.gpu;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import com.tom.peripherals.PeripheralsMod;
import com.tom.peripherals.screen.gpu.GLConstants.Vec2i;
import com.tom.peripherals.util.ITMPeripheral.LuaException;
import com.tom.peripherals.util.ITMPeripheral.LuaMethod;
import com.tom.peripherals.util.Image;
import com.tom.peripherals.util.ParamCheck;

public class GPUImpl extends BaseGPU {

	public GPUImpl(GPUContext ctx) {
		super(ctx);
	}

	public GPUImpl() {
	}

	@LuaMethod
	public Object[] decodeImage(Object[] a) throws LuaException {
		if (a.length < 1)throw new LuaException("Invalid arguments: expected buffer or unpacked numbers");
		Supplier<InputStream> str;
		if (a[0] instanceof String r) {
			if (ctx.getVRam().getRefMngr().getByReference(r) instanceof LuaByteBuffer buf) {
				str = () -> buf.asInputStream();
			} else {
				throw new LuaException("Invalid reference");
			}
		} else {
			int[] d = ParamCheck.ints(a, 0).toArray();
			str = () -> new IntInputStream(d);
		}

		try {
			Vec2i size = PeripheralsMod.imageIO.getSize(str.get());
			ctx.getVRam().checkSizeEx(size.x * size.y * 4);
			Image im = PeripheralsMod.imageIO.read(str.get());
			LuaImage i = new LuaImage(ctx.getVRam(), im);
			ctx.getVRam().alloc(i);
			return new Object[] {i};
		} catch (IOException e) {
			throw new LuaException(e.getMessage());
		}
	}

	@LuaMethod
	public Object[] imageFromBuffer(Object[] a) throws LuaException {
		int w = ParamCheck.getInt(a, 0);
		int[] d = ParamCheck.ints(a, 1).toArray();
		Image im = new Image(d, w);
		LuaImage i = new LuaImage(ctx.getVRam(), im);
		ctx.getVRam().allocEx(i, d.length * 4);
		return new Object[] {i};
	}

	@LuaMethod
	public Object[] newImage(Object[] a) throws LuaException {
		if (a.length > 1 && a[0] instanceof Double && a[1] instanceof Double) {
			int w = ParamCheck.getInt(a, 0);
			int h = ParamCheck.getInt(a, 1);
			if (w < 0 || h < 0)
				throw new LuaException("Image size out of bounds");
			ctx.getVRam().checkSizeEx(w * h * 4);
			LuaImage i = new LuaImage(ctx.getVRam(), new Image(w, h));
			ctx.getVRam().alloc(i);
			return new Object[] {i};
		} else
			throw new LuaException("Invalid Arguments, excepted (number w,number h)");
	}

	@LuaMethod
	public Object[] newBuffer(Object[] a) throws LuaException {
		int init = ParamCheck.optionalInt(a, 0, 32);
		return new Object[] {new LuaByteBuffer(ctx.getVRam(), init)};
	}

	private static class IntInputStream extends InputStream {
		private final int[] d;
		private int i;

		public IntInputStream(int[] d) {
			this.d = d;
		}

		@Override
		public int read() throws IOException {
			return i >= d.length ? -1 : d[i++];
		}
	}

	@LuaMethod
	public double getUsedMemory() {
		return ctx.getVRam().getUsedMemory();
	}

	@LuaMethod
	public double getMaxMemory() {
		return ctx.getVRam().getMaxMemory();
	}
}
