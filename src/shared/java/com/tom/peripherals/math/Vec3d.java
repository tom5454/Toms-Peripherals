package com.tom.peripherals.math;

public class Vec3d {
	public static final Vec3d ZERO = new Vec3d();
	public double x, y, z;

	public Vec3d() {
	}

	public Vec3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3d(Vec2d v, double z){
		this.x = v.x;
		this.y = v.y;
		this.z = z;
	}

	public Vec3d(Vec3d v){
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public Vec3d(Vec4d v){
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public Vec3d normalize(){
		double l = Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z);
		this.x /= l; this.y /= l; this.z /= l;
		return this;
	}

	public double dotProduct(Vec3d other){
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}

	public void mul(double v){
		this.x *= v;
		this.y *= v;
		this.z *= v;
	}
	public Vec3d mulI(double v){
		Vec3d r = new Vec3d(x, y, z);
		r.x *= v;
		r.y *= v;
		r.z *= v;
		return r;
	}
	public Vec3d addI(Vec3d o){
		Vec3d r = new Vec3d(x, y, z);
		r.x += o.x;
		r.y += o.y;
		r.z += o.z;
		return r;
	}
	public Vec3d subI(Vec3d o){
		Vec3d r = new Vec3d(x, y, z);
		r.x -= o.x;
		r.y -= o.y;
		r.z -= o.z;
		return r;
	}

	public static Vec4d vectorIntersectPlane(Vec3d plane_p, Vec3d plane_n, Vec4d lineStartIn, Vec4d lineEndIn, double[] t) {
		Vec3d lineStart = new Vec3d(lineStartIn);
		Vec3d lineEnd = new Vec3d(lineEndIn);
		plane_n = new Vec3d(plane_n).normalize();
		double plane_d = -plane_n.dotProduct(plane_p);
		double ad = lineStart.dotProduct(plane_n);
		double bd = lineEnd.dotProduct(plane_n);
		t[0] = (-plane_d - ad) / (bd - ad);
		Vec3d lineStartToEnd = lineEnd.subI(lineStart);
		Vec3d lineToIntersect = lineStartToEnd.mulI(t[0]);
		return new Vec4d(lineStart.addI(lineToIntersect), 0);
	}

	@Override
	public String toString() {
		return String.format("vec3(%s, %s, %s)", x, y, z);
	}
}