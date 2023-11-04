package com.tom.peripherals.gpu;

import com.tom.peripherals.api.LuaException;
import com.tom.peripherals.api.LuaMethod;
import com.tom.peripherals.api.TMLuaObject;
import com.tom.peripherals.util.ParamCheck;

public class Rect extends TMLuaObject {
	private boolean modif;
	private int x;
	private int y;
	private int w;
	private int h;

	public Rect() {
		this.modif = true;
	}

	public Rect(int x, int y, int w, int h) {
		this.modif = true;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public Rect(int w, int h) {
		this.modif = false;
		this.w = w;
		this.h = h;
	}

	@LuaMethod
	public int getX() {
		return x;
	}

	@LuaMethod
	public void setX(Object[] args) throws LuaException {
		if (!modif)throw new LuaException("Can't modify");
		this.x = ParamCheck.getInt(args, 0);
	}

	@LuaMethod
	public int getY() {
		return y;
	}

	@LuaMethod
	public void setY(Object[] args) throws LuaException {
		if (!modif)throw new LuaException("Can't modify");
		this.y = ParamCheck.getInt(args, 0);
	}

	@LuaMethod
	public int getW() {
		return w;
	}

	@LuaMethod
	public void setW(Object[] args) throws LuaException {
		if (!modif)throw new LuaException("Can't modify");
		int n = ParamCheck.getInt(args, 0);
		if (n < 0)throw new LuaException("Width value is less than 0");
		this.w = n;
	}

	@LuaMethod
	public int getH() {
		return h;
	}

	@LuaMethod
	public void setH(Object[] args) throws LuaException {
		if (!modif)throw new LuaException("Can't modify");
		int n = ParamCheck.getInt(args, 0);
		if (n < 0)throw new LuaException("Height value is less than 0");
		this.h = n;
	}

	public static Rect parseRect(Object[] a) throws LuaException {
		if (a.length < 4) {
			throw new LuaException("Too few arguments (expected x,y,width,height)");
		}
		int xStart = ParamCheck.getInt(a, 0) - 1;
		int yStart = ParamCheck.getInt(a, 1) - 1;
		int w = ParamCheck.getInt(a, 2);
		int h = ParamCheck.getInt(a, 3);
		if (w > 0 && h > 0) {
			return new Rect(xStart, yStart, w, h);
		} else {
			throw new LuaException("Out of boundary");
		}
	}
}