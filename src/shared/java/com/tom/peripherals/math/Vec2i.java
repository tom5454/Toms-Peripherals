package com.tom.peripherals.math;

public class Vec2i {
	public int x, y;

	public Vec2i(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vec2i(Vec3d v) {
		this.x = (int) v.x;
		this.y = (int) v.y;
	}

	public Vec2i(Vec4d v) {
		this.x = (int) v.x;
		this.y = (int) v.y;
	}

	public Vec2i(double x, double y) {
		this.x = (int) x;
		this.y = (int) y;
	}
}