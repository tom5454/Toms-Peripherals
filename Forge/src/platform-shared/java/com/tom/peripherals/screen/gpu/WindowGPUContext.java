package com.tom.peripherals.screen.gpu;

import com.tom.peripherals.screen.gpu.BaseGPU.GPUContext;
import com.tom.peripherals.screen.gpu.VRAM.VRAMObject;
import com.tom.peripherals.util.ITMPeripheral.LuaException;

public class WindowGPUContext implements GPUContext, VRAMObject {
	private final GPUContext ctx;
	private final Rect rect;
	private int[][] screen;
	private Runnable recalc;

	public WindowGPUContext(GPUContext ctx, Rect rect) {
		this.ctx = ctx;
		this.rect = rect;
		screen = new int[rect.getW()][rect.getH()];
	}

	@Override
	public void set(int x, int y, int c) {
		if(x < screen.length && x >= 0 && y >= 0 && y < screen[0].length)
			screen[x][y] = c;
	}

	@Override
	public void sync() throws LuaException {
		int xs = rect.getX();
		int ys = rect.getY();
		int iw = rect.getW();
		int ih = rect.getH();
		if(screen.length != iw || screen[0].length != ih){
			if(iw < 1 || ih < 1)throw new LuaException("Invalid window size");
			ctx.getVRam().reallocEx(this, iw * ih * 4);
			screen = new int[iw][ih];
			if(recalc != null)recalc.run();
			return;
		}
		int w = ctx.getWidth();
		int h = ctx.getHeight();
		for (int x = Math.max(-xs, 0);x + xs < w && x < iw;x++) {
			int[] js = screen[x];
			for (int y = Math.max(-ys, 0);y + ys < h && y < ih;y++) {
				int c = js[y];
				ctx.set(x + xs, y + ys, c);
			}
		}
	}

	@Override
	public int getWidth() {
		return rect.getW();
	}

	@Override
	public int getHeight() {
		return rect.getH();
	}

	@Override
	public VRAM getVRam() {
		return ctx.getVRam();
	}

	public int[][] getScreen() {
		return screen;
	}

	public void setRecalc(Runnable recalc) {
		this.recalc = recalc;
	}

	@Override
	public long getSize() {
		return rect.getW() + rect.getH();
	}

	@Override
	public Rect getBounds() {
		return rect;
	}
}