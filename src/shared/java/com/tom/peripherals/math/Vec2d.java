package com.tom.peripherals.math;

public class Vec2d {
	public static final Vec2d ZERO = new Vec2d();
	public double x, y;

	public Vec2d() {
	}

	public Vec2d(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return String.format("vec2(%s, %s)", x, y);
	}
}