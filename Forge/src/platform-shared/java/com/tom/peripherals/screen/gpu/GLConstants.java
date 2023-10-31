package com.tom.peripherals.screen.gpu;

import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import com.tom.peripherals.util.ITMPeripheral.LuaException;

public class GLConstants {
	public static class Matrix4d {
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

	public static class Vec2d {
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

	public static class Vec4d {
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

	public static class Vec3d {
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
	public static class Vec2i {
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

	public static enum Uniform {
		TEXTURE,
		;
		public static final int all = values().length;
	}

	//Begin modes
	public static final int
	GL_LINES          = 0x1,
	GL_LINE_LOOP      = 0x2,
	GL_LINE_STRIP     = 0x3,
	GL_TRIANGLES      = 0x4,
	GL_TRIANGLE_STRIP = 0x5,
	GL_TRIANGLE_FAN   = 0x6,
	GL_QUADS          = 0x7,
	GL_QUAD_STRIP     = 0x8,
	GL_POLYGON        = 0x9;

	public static final int
	GL_TEXTURE_2D              = 0xDE1;

	public static interface TriBuilder {
		public void append(Vec3d vert);
		public void setUV(Vec2d uv);
		public void setColor(Vec4d color);
		public void finish() throws LuaException;
	}

	public static TriBuilder builder(int mode, List<Triangle> list, Supplier<Matrix4d> mm, IntSupplier boundTexID) throws LuaException {
		switch (mode) {
		case GL_TRIANGLES:
			return new TriBuilder() {
				private int p = -1;
				private Vec3d[] v = new Vec3d[3];
				private Vec4d[] c = new Vec4d[3];
				private Vec2d[] t = new Vec2d[3];
				@Override
				public void setUV(Vec2d uv) {
					if(p != -1)t[p] = uv;
				}

				@Override
				public void setColor(Vec4d color) {
					if(p != -1)c[p] = color;
				}

				@Override
				public void append(Vec3d vert) {
					if(p >= 2)buildTri();
					v[++p] = mm.get().mul(vert);
				}

				@Override
				public void finish() throws LuaException {
					if(p != -1 && p != 2)throw new LuaException("Incomplete triangle");
					buildTri();
				}

				private void buildTri(){
					list.add(new Triangle(v, c, t, boundTexID.getAsInt()));
					v = new Vec3d[3];
					c = new Vec4d[3];
					t = new Vec2d[3];
					p = -1;
				}
			};

		default:
			throw new LuaException("Unknown build mode: " + mode);
		}
	}
}
