package com.tom.peripherals.math;

public class Vec4d {
	public static final Vec4d ZERO = new Vec4d();
	public double x, y, z, w;

	public Vec4d() {
	}

	public Vec4d(Vec3d v, double w) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.w = w;
	}

	public Vec4d(Vec4d v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
		this.w = v.w;
	}

	public Vec4d(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public void mul(double v){
		this.x *= v;
		this.y *= v;
		this.z *= v;
		this.w *= v;
	}

	public Vec4d mulI(double v){
		Vec4d r = new Vec4d(x, y, z, w);
		r.x *= v;
		r.y *= v;
		r.z *= v;
		r.w *= v;
		return r;
	}

	public void clip(double min, double max){
		if(this.x > max)this.x = max;
		if(this.y > max)this.y = max;
		if(this.z > max)this.z = max;
		if(this.w > max)this.w = max;
		if(this.x < min)this.x = min;
		if(this.y < min)this.y = min;
		if(this.z < min)this.z = min;
		if(this.w < min)this.w = min;
	}

	@Override
	public String toString() {
		return String.format("vec4(%s, %s, %s, %s)", x, y, z, w);
	}
}