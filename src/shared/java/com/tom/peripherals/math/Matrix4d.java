package com.tom.peripherals.math;

public class Matrix4d {
	public double m00, m01, m02, m03;
	public double m10, m11, m12, m13;
	public double m20, m21, m22, m23;
	public double m30, m31, m32, m33;

	public Vec3d mul(Vec3d i){
		Vec3d o = new Vec3d();
		o.x = i.x * m00 + i.y * m10 + i.z * m20 + m30;
		o.y = i.x * m01 + i.y * m11 + i.z * m21 + m31;
		o.z = i.x * m02 + i.y * m12 + i.z * m22 + m32;
		double w = i.x * m03 + i.y * m13 + i.z * m23 + m33;

		if (w != 0.0f) {
			o.x /= w; o.y /= w; o.z /= w;
		}
		return o;
	}

	public double mul(Vec3d i, Vec3d o){
		o.x = i.x * m00 + i.y * m10 + i.z * m20 + m30;
		o.y = i.x * m01 + i.y * m11 + i.z * m21 + m31;
		o.z = i.x * m02 + i.y * m12 + i.z * m22 + m32;
		double w = i.x * m03 + i.y * m13 + i.z * m23 + m33;

		if (w != 0.0f) {
			o.x /= w; o.y /= w; o.z /= w;
		}
		return w;
	}

	public void mul(Vec4d i, Vec4d o){
		o.x = i.x * m00 + i.y * m10 + i.z * m20 + m30;
		o.y = i.x * m01 + i.y * m11 + i.z * m21 + m31;
		o.z = i.x * m02 + i.y * m12 + i.z * m22 + m32;
		o.w = i.x * m03 + i.y * m13 + i.z * m23 + m33;
	}

	public Matrix4d(Matrix4d m) {
		m00 = m.m00;
		m01 = m.m01;
		m02 = m.m02;
		m03 = m.m03;
		m10 = m.m10;
		m11 = m.m11;
		m12 = m.m12;
		m13 = m.m13;
		m20 = m.m20;
		m21 = m.m21;
		m22 = m.m22;
		m23 = m.m23;
		m30 = m.m30;
		m31 = m.m31;
		m32 = m.m32;
		m33 = m.m33;
	}
	public Matrix4d() {
	}

	public Matrix4d identity(){
		m00 = 1.0;
		m10 = 0.0;
		m20 = 0.0;
		m30 = 0.0;
		m01 = 0.0;
		m11 = 1.0;
		m21 = 0.0;
		m31 = 0.0;
		m02 = 0.0;
		m12 = 0.0;
		m22 = 1.0;
		m32 = 0.0;
		m03 = 0.0;
		m13 = 0.0;
		m23 = 0.0;
		m33 = 1.0;
		return this;
	}

	public Matrix4d translate(double x, double y, double z) {
		Matrix4d c = this;
		c.m30 = c.m00 * x + c.m10 * y + c.m20 * z + c.m30;
		c.m31 = c.m01 * x + c.m11 * y + c.m21 * z + c.m31;
		c.m32 = c.m02 * x + c.m12 * y + c.m22 * z + c.m32;
		c.m33 = c.m03 * x + c.m13 * y + c.m23 * z + c.m33;
		return this;
	}

	public Matrix4d scale(double x, double y, double z) {
		this.m00 = m00 * x;
		this.m01 = m01 * x;
		this.m02 = m02 * x;
		this.m03 = m03 * x;
		this.m10 = m10 * y;
		this.m11 = m11 * y;
		this.m12 = m12 * y;
		this.m13 = m13 * y;
		this.m20 = m20 * z;
		this.m21 = m21 * z;
		this.m22 = m22 * z;
		this.m23 = m23 * z;
		return this;
	}

	public Matrix4d rotate(double ang, double x, double y, double z) {
		double s = Math.sin(ang);
		double c = Math.cos(ang);
		double C = 1.0 - c;
		double xx = x * x, xy = x * y, xz = x * z;
		double yy = y * y, yz = y * z;
		double zz = z * z;
		double rm00 = xx * C + c;
		double rm01 = xy * C + z * s;
		double rm02 = xz * C - y * s;
		double rm10 = xy * C - z * s;
		double rm11 = yy * C + c;
		double rm12 = yz * C + x * s;
		double rm20 = xz * C + y * s;
		double rm21 = yz * C - x * s;
		double rm22 = zz * C + c;
		double nm00 = m00 * rm00 + m10 * rm01 + m20 * rm02;
		double nm01 = m01 * rm00 + m11 * rm01 + m21 * rm02;
		double nm02 = m02 * rm00 + m12 * rm01 + m22 * rm02;
		double nm03 = m03 * rm00 + m13 * rm01 + m23 * rm02;
		double nm10 = m00 * rm10 + m10 * rm11 + m20 * rm12;
		double nm11 = m01 * rm10 + m11 * rm11 + m21 * rm12;
		double nm12 = m02 * rm10 + m12 * rm11 + m22 * rm12;
		double nm13 = m03 * rm10 + m13 * rm11 + m23 * rm12;
		this.m20 = m00 * rm20 + m10 * rm21 + m20 * rm22;
		this.m21 = m01 * rm20 + m11 * rm21 + m21 * rm22;
		this.m22 = m02 * rm20 + m12 * rm21 + m22 * rm22;
		this.m23 = m03 * rm20 + m13 * rm21 + m23 * rm22;
		this.m00 = nm00;
		this.m01 = nm01;
		this.m02 = nm02;
		this.m03 = nm03;
		this.m10 = nm10;
		this.m11 = nm11;
		this.m12 = nm12;
		this.m13 = nm13;
		return this;
	}
}