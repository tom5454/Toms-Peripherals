package com.tom.peripherals.gpu;

import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import com.tom.peripherals.api.LuaException;
import com.tom.peripherals.math.Matrix4d;
import com.tom.peripherals.math.Vec2d;
import com.tom.peripherals.math.Vec3d;
import com.tom.peripherals.math.Vec4d;

public interface TriBuilder {
	public void append(Vec3d vert);
	public void setUV(Vec2d uv);
	public void setColor(Vec4d color);
	public void finish() throws LuaException;

	static TriBuilder builder(int mode, List<Triangle> list, Supplier<Matrix4d> mm, IntSupplier boundTexID) throws LuaException {
		switch (mode) {
		case GLConstants.GL_TRIANGLES:
			return new Default(list, mm, boundTexID);
	
		default:
			throw new LuaException("Unknown build mode: " + mode);
		}
	}

	public static class Default implements TriBuilder {
		private List<Triangle> list;
		private Supplier<Matrix4d> mm;
		private IntSupplier boundTexID;

		public Default(List<Triangle> list, Supplier<Matrix4d> mm, IntSupplier boundTexID) {
			this.list = list;
			this.mm = mm;
			this.boundTexID = boundTexID;
		}

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
	}
}