package com.tom.peripherals.gpu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import com.tom.peripherals.api.LuaException;
import com.tom.peripherals.api.LuaMethod;
import com.tom.peripherals.api.TMLuaObject;
import com.tom.peripherals.gpu.BaseGPU.GPUContext;
import com.tom.peripherals.gpu.VRAM.VRAMObject;
import com.tom.peripherals.math.Matrix4d;
import com.tom.peripherals.math.Vec2d;
import com.tom.peripherals.math.Vec3d;
import com.tom.peripherals.math.Vec4d;
import com.tom.peripherals.util.ParamCheck;

public class GPU3D extends TMLuaObject implements VRAMObject {
	protected TextureManager tm;
	protected TriBuilder buildingTri;
	protected Matrix4d mat = new Matrix4d().identity();
	protected Matrix4d proj = new Matrix4d();
	protected Stack<Matrix4d> mstack = new Stack<>();
	protected GPUContext ctx;
	protected List<Triangle> triangles = new ArrayList<>();
	protected Vec3d nearPlane;
	protected Vec3d camera = new Vec3d();
	protected Vec3d directionalLight = new Vec3d();
	protected Vec4d color = new Vec4d(1, 1, 1, 1);
	protected float[] depthBuffer;

	public GPU3D(GPUContext ctx) {
		this.ctx = ctx;
		this.tm = new TextureManager(ctx.getVRam());
		ctx.getVRam().alloc(this);
	}

	@LuaMethod
	public void glFrustum(Object[] a) throws LuaException {
		if (a.length < 3) throw new LuaException("Too few arguments, expected: number fov, number Znear, number Zfar");
		double fov = ParamCheck.getDouble(a, 0);
		double zNear = ParamCheck.getDouble(a, 1);
		double zFar = ParamCheck.getDouble(a, 2);
		double rtanFov = 1f / Math.tan(Math.toRadians(fov / 2));
		proj = new Matrix4d();
		proj.m00 = (ctx.getHeight() / (double) ctx.getWidth()) * rtanFov;
		proj.m11 = rtanFov;
		proj.m22 = zFar / (zFar - zNear);
		proj.m23 = 1;
		proj.m32 = (-zFar * zNear) / (zFar - zNear);
		nearPlane = new Vec3d(0, 0, zNear);
	}

	@LuaMethod
	public void glDirLight(Object[] a) throws LuaException {
		if (a.length < 3) throw new LuaException("Too few arguments, expected: number x, number y, number z");
		directionalLight.x = ParamCheck.getDouble(a, 0);
		directionalLight.y = ParamCheck.getDouble(a, 1);
		directionalLight.z = ParamCheck.getDouble(a, 2);
		directionalLight = mat.mul(directionalLight);
		directionalLight.normalize();
	}

	@LuaMethod
	public void render() throws LuaException {
		int W = ctx.getWidth();
		int H = ctx.getHeight();
		//System.out.println("3D: " + W + "x" + H);
		if (depthBuffer == null || depthBuffer.length != W * H) {
			ctx.getVRam().reallocEx(this, W * H * 4 + 1024);
			depthBuffer = new float[W * H];
		}
		List<Triangle> tris = new ArrayList<>();
		for (Triangle tri : triangles) {
			double light = 1;
			if (true) {
				Vec3d normal = tri.normal();

				double normalDot = normal.x * (tri.vert[0][Triangle.POS_DATA].x - camera.x)
						+ normal.y * (tri.vert[0][Triangle.POS_DATA].y - camera.y)
						+ normal.z * (tri.vert[0][Triangle.POS_DATA].z - camera.z);

				if (normalDot > 0) continue;
				if (true) {
					light = normal.dotProduct(directionalLight);
				}
				// Vertex Layer
			}
			Triangle dup = new Triangle(tri);
			List<Triangle> clipped = dup.triangleClipAgainstPlane(nearPlane, new Vec3d(0, 0, 1));

			for (Triangle ctri : clipped) {
				Triangle nt = new Triangle(ctri);

				for (int i = 0; i < tri.vert.length; i++) {
					Vec4d vec3d = tri.vert[i][Triangle.POS_DATA];
					Vec4d a = new Vec4d();
					proj.mul(vec3d, a);
					Vec4d uv = ctri.vert[i][Triangle.TEX_DATA];
					Vec4d nuv = new Vec4d();
					double w = a.w;
					if (w != 0) {
						nuv.x = uv.x / w;
						nuv.y = uv.y / w;
						nuv.z = 1d / w;
						a.x /= w;
						a.y /= w;
						a.z /= w;
					}
					a.x += 1;
					a.y += 1;
					a.x *= 0.5 * W;
					a.y *= 0.5 * H;
					nt.vert[i][Triangle.POS_DATA] = a;
					Vec4d c = ctri.vert[i][Triangle.COLOR_DATA].mulI(light);
					c.clip(0, 1);
					nt.vert[i][Triangle.COLOR_DATA] = c;
					nt.vert[i][Triangle.TEX_DATA] = nuv;
				}
				//nt.outline(this);

				tris.add(nt);
			}
		}
		tris.sort(Triangle.COMPARE_Z);
		for (Triangle tr : tris) {
			tr.clipAndBlit(this);
		}
	}

	@LuaMethod
	public void sync() throws LuaException {
		ctx.sync();
	}

	@LuaMethod
	public void clear() {
		for (int i = 0; i < ctx.getWidth(); i++) {
			for (int j = 0; j < ctx.getHeight(); j++) {
				ctx.set(i, j, 0);
			}
		}
		triangles.clear();
		mat.identity();
		if (depthBuffer != null) Arrays.fill(depthBuffer, 0f);
	}

	@LuaMethod
	public void glBegin(Object[] a) throws LuaException {
		if (buildingTri != null) throw new LuaException("Already building");
		int id = ParamCheck.optionalInt(a, 0, GLConstants.GL_TRIANGLES);
		buildingTri = TriBuilder.builder(id, triangles, () -> mat, tm::getTextureID);
	}

	@LuaMethod
	public void glEnd() throws LuaException {
		if (buildingTri == null) throw new LuaException("not building");
		buildingTri.finish();
		buildingTri = null;
	}

	@LuaMethod
	public void glVertex(Object[] a) throws LuaException {
		if (a.length < 3) throw new LuaException("Too few arguments, expected: number x, number y, number z");
		double x = ParamCheck.getDouble(a, 0);
		double y = ParamCheck.getDouble(a, 1);
		double z = ParamCheck.getDouble(a, 2);
		if (buildingTri == null) throw new LuaException("not building");
		buildingTri.append(new Vec3d(x, y, z));
		buildingTri.setColor(new Vec4d(color));
		buildingTri.setUV(new Vec2d());
	}

	@LuaMethod
	public void glTexCoord(Object[] a) throws LuaException {
		if (a.length < 2) throw new LuaException("Too few arguments, expected: number u, number v");
		double u = ParamCheck.getDouble(a, 0);
		double v = ParamCheck.getDouble(a, 1);
		if (buildingTri == null) throw new LuaException("not building");
		buildingTri.setUV(new Vec2d(u, v));
	}

	@LuaMethod
	public void glColor(Object[] a) throws LuaException {
		int c = ParamCheck.toColor(a, 0);
		color = new Vec4d(((c >> 16) & 0xFF) / 255f, ((c >> 8) & 0xFF) / 255f, (c & 0xFF) / 255f,
				((c >> 24) & 0xFF) / 255f);
		if (buildingTri != null) buildingTri.setColor(new Vec4d(color));
	}

	@LuaMethod
	public void glLoadIdentity() {
		mat.identity();
	}

	@LuaMethod
	public void glPushMatrix() {
		mstack.push(new Matrix4d(mat));
	}

	@LuaMethod
	public void glPopMatrix() throws LuaException {
		if (mstack.peek() == null) throw new LuaException("no element in stack");
		mat = mstack.pop();
	}

	@LuaMethod
	public void glTranslate(Object[] a) throws LuaException {
		if (a.length < 3) throw new LuaException("Too few arguments, expected: number x, number y, number z");
		double x = ParamCheck.getDouble(a, 0);
		double y = ParamCheck.getDouble(a, 1);
		double z = ParamCheck.getDouble(a, 2);
		mat.translate(x, y, z);
	}

	@LuaMethod
	public void glScale(Object[] a) throws LuaException {
		if (a.length < 3) throw new LuaException("Too few arguments, expected: number x, number y, number z");
		double x = ParamCheck.getDouble(a, 0);
		double y = ParamCheck.getDouble(a, 1);
		double z = ParamCheck.getDouble(a, 2);
		mat.scale(x, y, z);
	}

	@LuaMethod
	public void glRotate(Object[] a) throws LuaException {
		if (a.length < 4)
			throw new LuaException("Too few arguments, expected: number angle, number x, number y, number z");
		double d = ParamCheck.getDouble(a, 0);
		double x = ParamCheck.getDouble(a, 1);
		double y = ParamCheck.getDouble(a, 2);
		double z = ParamCheck.getDouble(a, 3);
		mat.rotate(Math.toRadians(d), x, y, z);
	}

	@LuaMethod
	public int glGenTextures(Object[] a) throws LuaException {
		return tm.genTextureID(a);
	}

	@LuaMethod
	public void glDeleteTextures(Object[] a) throws LuaException {
		tm.deleteTextures(a);
	}

	@LuaMethod
	public void glBindTexture(Object[] a) throws LuaException {
		tm.bindTexture(a);
	}

	@LuaMethod
	public void glTexImage(Object[] a) throws LuaException {
		tm.texImage(a);
	}

	@LuaMethod
	public Object getBounds() throws LuaException {
		return ctx.getBounds();
	}

	@Override
	public long getSize() {
		return (depthBuffer != null ? depthBuffer.length * 4 : 0) + 1024;
	}

	@LuaMethod
	public void glEnable(Object[] a) throws LuaException {
		int mode = ParamCheck.getInt(a, 0);
		switch (mode) {
		case GLConstants.GL_TEXTURE_2D -> tm.setTexEnabled(true);
		default ->
		throw new LuaException("Bad argument #1: unknown GL state" + mode);
		}
	}

	@LuaMethod
	public void glDisable(Object[] a) throws LuaException {
		int mode = ParamCheck.getInt(a, 0);
		switch (mode) {
		case GLConstants.GL_TEXTURE_2D -> tm.setTexEnabled(false);
		default ->
		throw new LuaException("Bad argument #1: unknown GL state" + mode);
		}
	}

	@LuaMethod
	public Object[] getConstants() {
		return GLConstants.ALL_CONST;
	}
}
