package com.tom.peripherals.screen.gpu;

import java.io.IOException;

import com.tom.peripherals.PeripheralsMod;
import com.tom.peripherals.screen.gpu.BaseGPU.GPUContext;
import com.tom.peripherals.screen.gpu.VRAM.VRAMObject;
import com.tom.peripherals.util.ITMPeripheral.LuaException;
import com.tom.peripherals.util.ITMPeripheral.LuaMethod;
import com.tom.peripherals.util.ITMPeripheral.ReferenceableLuaObject;
import com.tom.peripherals.util.Image;
import com.tom.peripherals.util.ParamCheck;

public class LuaImage extends ReferenceableLuaObject implements GPUContext, VRAMObject {
	private final VRAM vram;
	private Image image;

	public LuaImage(VRAM vram, Image image) {
		super(vram.getRefMngr());
		this.vram = vram;
		this.image = image;
	}

	@Override
	@LuaMethod
	public int getWidth() {
		if (image == null)throw new LuaException("Error: Use after free");
		return image.getWidth();
	}

	@Override
	@LuaMethod
	public int getHeight() {
		if (image == null)throw new LuaException("Error: Use after free");
		return image.getHeight();
	}

	@LuaMethod
	public Object[] getAsBuffer() {
		if (image == null)throw new LuaException("Error: Use after free");
		Object[] a = new Object[image.getWidth() * image.getHeight()];
		int[] d = image.getData();
		for (int i = 0; i < d.length; i++) {
			a[i] = d[i];
		}
		return a;
	}

	@Override
	@LuaMethod
	public Object ref() {
		if (image == null)throw new LuaException("Error: Use after free");
		return super.ref();
	}

	@Override
	public void set(int x, int y, int c) {
		if (image == null)throw new LuaException("Error: Use after free");
		image.setRGB(x, y, c);
	}

	@Override
	public void sync() {}

	@LuaMethod
	public Object gpuDraw() throws LuaException {
		if (image == null)throw new LuaException("Error: Use after free");
		return new GPUImpl(this);
	}

	@Override
	public VRAM getVRam() {
		return vram;
	}

	@Override
	public long getSize() {
		return getWidth() * getHeight() * 4;
	}

	public Image getImage() throws LuaException {
		if (image == null)throw new LuaException("Error: Use after free");
		return image;
	}

	@LuaMethod
	public void free() throws LuaException {
		if (image == null)return;
		vram.getRefMngr().remove(this);
		vram.free(this);
		image = null;
	}

	@LuaMethod
	public Object[] saveImage() throws LuaException {
		if (image == null)throw new LuaException("Error: Use after free");
		LuaByteBuffer buf = new LuaByteBuffer(vram);
		try {
			PeripheralsMod.imageIO.write(image, buf.asOutputStream());
		} catch (IOException e) {
			throw new LuaException(e.getMessage());
		}
		return new Object[] {buf};
	}

	@LuaMethod
	public void setRGB(Object[] a) throws LuaException {
		int x = ParamCheck.getInt(a, 0) + 1;
		int y = ParamCheck.getInt(a, 1) + 1;
		int rgb = ParamCheck.toColor(a, 2);
		image.setRGB(x, y, rgb);
	}

	@LuaMethod
	public int getRGB(Object[] a) throws LuaException {
		int x = ParamCheck.getInt(a, 0) + 1;
		int y = ParamCheck.getInt(a, 1) + 1;
		return image.getRGB(x, y);
	}
}