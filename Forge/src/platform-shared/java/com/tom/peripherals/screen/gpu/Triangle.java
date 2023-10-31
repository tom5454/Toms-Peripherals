package com.tom.peripherals.screen.gpu;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.function.ToDoubleFunction;

import com.tom.peripherals.screen.gpu.BaseGPU.GPUContext;
import com.tom.peripherals.screen.gpu.GLConstants.Uniform;
import com.tom.peripherals.screen.gpu.GLConstants.Vec2d;
import com.tom.peripherals.screen.gpu.GLConstants.Vec2i;
import com.tom.peripherals.screen.gpu.GLConstants.Vec3d;
import com.tom.peripherals.screen.gpu.GLConstants.Vec4d;

public class Triangle {
	public static final Comparator<Triangle> COMPARE_Z = (a, b) -> {
		double z1 = (a.vert[0][Triangle.POS_DATA].z + a.vert[1][Triangle.POS_DATA].z
				+ a.vert[2][Triangle.POS_DATA].z) / 3.0f;
		double z2 = (b.vert[0][Triangle.POS_DATA].z + b.vert[1][Triangle.POS_DATA].z
				+ b.vert[2][Triangle.POS_DATA].z) / 3.0f;
		return -Double.compare(z1, z2);
	};

	public static final int POS_DATA = 0;
	public static final int TEX_DATA = 1;
	public static final int COLOR_DATA = 2;

	public Vec4d[][] vert;
	public Object[] uniforms;

	public Triangle() {
		vert = new Vec4d[3][3];
		uniforms = new Object[Uniform.all];
	}

	public Triangle(Triangle tri) {
		vert = new Vec4d[3][];
		vert[0] = Arrays.copyOf(tri.vert[0], tri.vert[0].length);
		vert[1] = Arrays.copyOf(tri.vert[1], tri.vert[1].length);
		vert[2] = Arrays.copyOf(tri.vert[2], tri.vert[2].length);
		uniforms = Arrays.copyOf(tri.uniforms, tri.uniforms.length);
	}

	public Triangle(Vec3d[] vecs, Vec4d[] color, Vec2d[] uv, int texID) {
		this();
		for (int i = 0; i < 3; i++) {
			vert[i][POS_DATA] = new Vec4d(vecs[i], 1);
			vert[i][COLOR_DATA] = color[i];
			vert[i][TEX_DATA] = new Vec4d(uv[i].x, uv[i].y, 0, 0);
		}
		uniforms[Uniform.TEXTURE.ordinal()] = texID;
	}

	public List<Triangle> triangleClipAgainstPlane(Vec3d plane_p, Vec3d plane_n) {
		plane_n.normalize();

		ToDoubleFunction<Vec4d> dist = p -> {
			Vec3d n = new Vec3d(p);
			n.normalize();
			return (plane_n.x * p.x + plane_n.y * p.y + plane_n.z * p.z - plane_n.dotProduct(plane_p));
		};

		Vec4d[][] inside_points = new Vec4d[3][];
		int nInsidePointCount = 0;
		Vec4d[][] outside_points = new Vec4d[3][];
		int nOutsidePointCount = 0;

		double d0 = dist.applyAsDouble(vert[0][POS_DATA]);
		double d1 = dist.applyAsDouble(vert[1][POS_DATA]);
		double d2 = dist.applyAsDouble(vert[2][POS_DATA]);

		if (d0 >= 0) {
			int p = nInsidePointCount++;
			inside_points[p] = vert[0];
		} else {
			int p = nOutsidePointCount++;
			outside_points[p] = vert[0];
		}
		if (d1 >= 0) {
			int p = nInsidePointCount++;
			inside_points[p] = vert[1];
		} else {
			int p = nOutsidePointCount++;
			outside_points[p] = vert[1];
		}
		if (d2 >= 0) {
			int p = nInsidePointCount++;
			inside_points[p] = vert[2];
		} else {
			int p = nOutsidePointCount++;
			outside_points[p] = vert[2];
		}

		if (nInsidePointCount == 0) {
			return Collections.emptyList();
		}

		if (nInsidePointCount == 3) {
			return Collections.singletonList(this);
		}

		if (nInsidePointCount == 1 && nOutsidePointCount == 2) {
			Triangle out_tri1 = new Triangle();
			out_tri1.uniforms = Arrays.copyOf(uniforms, uniforms.length);

			out_tri1.vert[0] = inside_points[0];

			double[] t = new double[1];
			calcValue(out_tri1.vert, 1, inside_points, outside_points, 0, 0, t[0]);
			out_tri1.vert[1][POS_DATA] = Vec3d.vectorIntersectPlane(plane_p, plane_n, inside_points[0][POS_DATA],
					outside_points[0][POS_DATA], t);

			calcValue(out_tri1.vert, 2, inside_points, outside_points, 0, 1, t[0]);
			out_tri1.vert[2][POS_DATA] = Vec3d.vectorIntersectPlane(plane_p, plane_n, inside_points[0][POS_DATA],
					outside_points[1][POS_DATA], t);

			return Collections.singletonList(out_tri1);
		}

		if (nInsidePointCount == 2 && nOutsidePointCount == 1) {
			Triangle out_tri1 = new Triangle();
			Triangle out_tri2 = new Triangle();

			out_tri1.uniforms = Arrays.copyOf(uniforms, uniforms.length);
			out_tri2.uniforms = Arrays.copyOf(uniforms, uniforms.length);
			double[] t = new double[1];

			out_tri1.vert[0] = inside_points[0];
			out_tri1.vert[1] = inside_points[1];
			calcValue(out_tri1.vert, 2, inside_points, outside_points, 0, 0, t[0]);
			out_tri1.vert[2][POS_DATA] = Vec3d.vectorIntersectPlane(plane_p, plane_n, inside_points[0][POS_DATA],
					outside_points[0][POS_DATA], t);

			out_tri2.vert[0] = inside_points[1];
			out_tri2.vert[1] = out_tri1.vert[2];
			calcValue(out_tri2.vert, 2, inside_points, outside_points, 1, 0, t[0]);
			out_tri2.vert[2][POS_DATA] = Vec3d.vectorIntersectPlane(plane_p, plane_n, inside_points[1][POS_DATA],
					outside_points[0][POS_DATA], t);

			return Arrays.asList(out_tri1, out_tri2);
		}

		return Collections.emptyList();
	}

	private static void calcValue(Vec4d[][] out, int i, Vec4d[][] inside_values, Vec4d[][] outside_values, int in,
			int o, double t) {
		int len = inside_values[in].length;
		out[i] = new Vec4d[len];
		for (int j = 1; j < len; j++) {
			Vec4d inside = inside_values[in][j];
			Vec4d outside = outside_values[o][j];
			out[i][j] = new Vec4d(t * (outside.x - inside.x) + inside.x, t * (outside.y - inside.y) + inside.y,
					t * (outside.z - inside.z) + inside.z, t * (outside.w - inside.w) + inside.w);
		}
	}

	public Vec4d[] array() {
		return new Vec4d[] { vert[0][POS_DATA], vert[1][POS_DATA], vert[2][POS_DATA] };
	}

	public void textureTriangle(GPU3D gpu) {
		int W = gpu.ctx.getWidth();
		Vec2i p1 = new Vec2i(this.vert[0][Triangle.POS_DATA]);
		Vec2i p2 = new Vec2i(this.vert[1][Triangle.POS_DATA]);
		Vec2i p3 = new Vec2i(this.vert[2][Triangle.POS_DATA]);

		Vec4d uv1 = this.vert[0][Triangle.TEX_DATA];
		Vec4d uv2 = this.vert[1][Triangle.TEX_DATA];
		Vec4d uv3 = this.vert[2][Triangle.TEX_DATA];

		Vec4d c1 = this.vert[0][Triangle.COLOR_DATA];
		Vec4d c2 = this.vert[1][Triangle.COLOR_DATA];
		Vec4d c3 = this.vert[2][Triangle.COLOR_DATA];

		// TODO perform this with all vert elements
		if (p2.y < p1.y) {
			swap(p1, p2);
			swap(uv1, uv2);
			swap(c1, c2);
		}

		if (p3.y < p1.y) {
			swap(p1, p3);
			swap(uv1, uv3);
			swap(c1, c3);
		}

		if (p3.y < p2.y) {
			swap(p3, p2);
			swap(uv3, uv2);
			swap(c3, c2);
		}

		int dy1 = p2.y - p1.y;
		int dx1 = p2.x - p1.x;
		double du1 = uv2.x - uv1.x;
		double dv1 = uv2.y - uv1.y;
		double dw1 = uv2.z - uv1.z;

		double dr1 = c2.x - c1.x;
		double dg1 = c2.y - c1.y;
		double db1 = c2.z - c1.z;
		double da1 = c2.w - c1.w;

		int dy2 = p3.y - p1.y;
		int dx2 = p3.x - p1.x;
		double du2 = uv3.x - uv1.x;
		double dv2 = uv3.y - uv1.y;
		double dw2 = uv3.z - uv1.z;

		double dr2 = c3.x - c1.x;
		double dg2 = c3.y - c1.y;
		double db2 = c3.z - c1.z;
		double da2 = c3.w - c1.w;

		double tex_u, tex_v, tex_w, c_r, c_g, c_b, c_a;

		double dax_step = 0, dbx_step = 0, du1_step = 0, dv1_step = 0, du2_step = 0, dv2_step = 0, dw1_step = 0,
				dw2_step = 0, dr1_step = 0, dg1_step = 0, db1_step = 0, da1_step = 0, dr2_step = 0, dg2_step = 0,
				db2_step = 0, da2_step = 0;

		if (dy1 != 0) dax_step = dx1 / (double) Math.abs(dy1);
		if (dy2 != 0) dbx_step = dx2 / (double) Math.abs(dy2);

		if (dy1 != 0) du1_step = du1 / Math.abs(dy1);
		if (dy1 != 0) dv1_step = dv1 / Math.abs(dy1);
		if (dy1 != 0) dw1_step = dw1 / Math.abs(dy1);

		if (dy1 != 0) dr1_step = dr1 / Math.abs(dy1);
		if (dy1 != 0) dg1_step = dg1 / Math.abs(dy1);
		if (dy1 != 0) db1_step = db1 / Math.abs(dy1);
		if (dy1 != 0) da1_step = da1 / Math.abs(dy1);

		if (dy2 != 0) du2_step = du2 / Math.abs(dy2);
		if (dy2 != 0) dv2_step = dv2 / Math.abs(dy2);
		if (dy2 != 0) dw2_step = dw2 / Math.abs(dy2);

		if (dy2 != 0) dr2_step = dr2 / Math.abs(dy2);
		if (dy2 != 0) dg2_step = dg2 / Math.abs(dy2);
		if (dy2 != 0) db2_step = db2 / Math.abs(dy2);
		if (dy2 != 0) da2_step = da2 / Math.abs(dy2);

		if (dy1 != 0) {
			for (int i = p1.y; i <= p2.y; i++) {
				int ax = (int) (p1.x + (i - p1.y) * dax_step);
				int bx = (int) (p1.x + (i - p1.y) * dbx_step);

				// TODO perform this with all vert elements
				Vec3d tex_s = new Vec3d(uv1.x + (i - p1.y) * du1_step, uv1.y + (i - p1.y) * dv1_step,
						uv1.z + (i - p1.y) * dw1_step);

				Vec3d tex_e = new Vec3d(uv1.x + (i - p1.y) * du2_step, uv1.y + (i - p1.y) * dv2_step,
						uv1.z + (i - p1.y) * dw2_step);

				Vec4d c_s = new Vec4d(c1.x + (i - p1.y) * dr1_step, c1.y + (i - p1.y) * dg1_step,
						c1.z + (i - p1.y) * db1_step, c1.w + (i - p1.y) * da1_step);

				Vec4d c_e = new Vec4d(c1.x + (i - p1.y) * dr2_step, c1.y + (i - p1.y) * dg2_step,
						c1.z + (i - p1.y) * db2_step, c1.w + (i - p1.y) * da2_step);

				if (ax > bx) {
					int v = ax;
					ax = bx;
					bx = v;

					Vec3d u = tex_s;
					tex_s = tex_e;
					tex_e = u;

					Vec4d t = c_s;
					c_s = c_e;
					c_e = t;
				}

				tex_u = tex_s.x;
				tex_v = tex_s.y;
				tex_w = tex_s.z;

				c_r = c_s.x;
				c_g = c_s.y;
				c_b = c_s.z;
				c_a = c_s.w;

				double tstep = 1.0f / (bx - ax);
				double t = 0.0f;

				for (int j = ax; j < bx; j++) {
					tex_u = (1.0f - t) * tex_s.x + t * tex_e.x;
					tex_v = (1.0f - t) * tex_s.y + t * tex_e.y;
					tex_w = (1.0f - t) * tex_s.z + t * tex_e.z;

					c_r = (1.0f - t) * c_s.x + t * c_e.x;
					c_g = (1.0f - t) * c_s.y + t * c_e.y;
					c_b = (1.0f - t) * c_s.z + t * c_e.z;
					c_a = (1.0f - t) * c_s.w + t * c_e.w;

					if (tex_w > gpu.depthBuffer[i * W + j]) {
						gpu.ctx.set(j, i, gpu.tm.sample(tex_u / tex_w, tex_v / tex_w, this, c_r, c_g, c_b, c_a));
						gpu.depthBuffer[i * W + j] = (float) tex_w;
					}

					t += tstep;
				}
			}
		}

		dy1 = p3.y - p2.y;
		dx1 = p3.x - p2.x;
		du1 = uv3.x - uv2.x;
		dv1 = uv3.y - uv2.y;
		dw1 = uv3.z - uv2.z;

		dr1 = c3.x - c2.x;
		dg1 = c3.y - c2.y;
		db1 = c3.z - c2.z;
		da1 = c3.w - c2.w;

		if (dy1 != 0) dax_step = dx1 / (double) Math.abs(dy1);
		if (dy2 != 0) dbx_step = dx2 / (double) Math.abs(dy2);

		du1_step = 0;
		dv1_step = 0;
		dw1_step = 0;
		dr1_step = 0;
		dg1_step = 0;
		db1_step = 0;
		da1_step = 0;

		if (dy1 != 0) du1_step = du1 / Math.abs(dy1);
		if (dy1 != 0) dv1_step = dv1 / Math.abs(dy1);
		if (dy1 != 0) dw1_step = dw1 / Math.abs(dy1);

		if (dy1 != 0) dr1_step = dr1 / Math.abs(dy1);
		if (dy1 != 0) dg1_step = dg1 / Math.abs(dy1);
		if (dy1 != 0) db1_step = db1 / Math.abs(dy1);
		if (dy1 != 0) da1_step = da1 / Math.abs(dy1);

		if (dy1 != 0) {
			for (int i = p2.y; i <= p3.y; i++) {
				int ax = (int) (p2.x + (i - p2.y) * dax_step);
				int bx = (int) (p1.x + (i - p1.y) * dbx_step);

				Vec3d tex_s = new Vec3d(uv2.x + (i - p2.y) * du1_step, uv2.y + (i - p2.y) * dv1_step,
						uv2.z + (i - p2.y) * dw1_step);

				Vec3d tex_e = new Vec3d(uv1.x + (i - p1.y) * du2_step, uv1.y + (i - p1.y) * dv2_step,
						uv1.z + (i - p1.y) * dw2_step);

				Vec4d c_s = new Vec4d(c2.x + (i - p2.y) * dr1_step, c2.y + (i - p2.y) * dg1_step,
						c2.z + (i - p2.y) * db1_step, c2.w + (i - p2.y) * da1_step);

				Vec4d c_e = new Vec4d(c1.x + (i - p1.y) * dr2_step, c1.y + (i - p1.y) * dg2_step,
						c1.z + (i - p1.y) * db2_step, c1.w + (i - p1.y) * da2_step);

				if (ax > bx) {
					int v = ax;
					ax = bx;
					bx = v;

					Vec3d u = tex_s;
					tex_s = tex_e;
					tex_e = u;

					Vec4d t = c_s;
					c_s = c_e;
					c_e = t;
				}

				tex_u = tex_s.x;
				tex_v = tex_s.y;
				tex_w = tex_s.z;

				c_r = c_s.x;
				c_g = c_s.y;
				c_b = c_s.z;
				c_a = c_s.w;

				float tstep = 1.0f / (bx - ax);
				float t = 0.0f;

				for (int j = ax; j < bx; j++) {
					tex_u = (1.0f - t) * tex_s.x + t * tex_e.x;
					tex_v = (1.0f - t) * tex_s.y + t * tex_e.y;
					tex_w = (1.0f - t) * tex_s.z + t * tex_e.z;

					c_r = (1.0f - t) * c_s.x + t * c_e.x;
					c_g = (1.0f - t) * c_s.y + t * c_e.y;
					c_b = (1.0f - t) * c_s.z + t * c_e.z;
					c_a = (1.0f - t) * c_s.w + t * c_e.w;

					if (tex_w > gpu.depthBuffer[i * W + j]) {
						gpu.ctx.set(j, i, gpu.tm.sample(tex_u / tex_w, tex_v / tex_w, this, c_r, c_g, c_b, c_a));
						gpu.depthBuffer[i * W + j] = (float) tex_w;
					}

					t += tstep;
				}
			}
		}
	}

	private static void swap(Vec4d a, Vec4d b) {
		double x = a.x;
		double y = a.y;
		double z = a.z;
		double w = a.w;
		a.x = b.x;
		a.y = b.y;
		a.z = b.z;
		a.w = b.w;
		b.x = x;
		b.y = y;
		b.z = z;
		b.w = w;
	}

	private static void swap(Vec2i a, Vec2i b) {
		int x = a.x;
		int y = a.y;
		a.x = b.x;
		a.y = b.y;
		b.x = x;
		b.y = y;
	}

	public void outline(GPU3D gpu) {
		triangle(gpu.ctx, (int)vert[0][POS_DATA].x, (int)vert[0][POS_DATA].y, (int)vert[1][POS_DATA].x, (int)vert[1][POS_DATA].y, (int)vert[2][POS_DATA].x, (int)vert[2][POS_DATA].y, 0xFFFFFFFF);
	}

	public void clipAndBlit(GPU3D gpu) {
		//outline(gpu);
		int w = gpu.ctx.getWidth() - 1;
		int h = gpu.ctx.getHeight() - 1;
		Deque<Triangle> trisToClip = new ArrayDeque<>();
		trisToClip.add(this);
		int nNewTriangles = 1;

		for (int p = 0; p < 4; p++) {
			while (nNewTriangles > 0) {
				Triangle test = trisToClip.pop();
				nNewTriangles--;

				switch (p) {
				case 0: trisToClip.addAll(test.triangleClipAgainstPlane(Vec3d.ZERO, new Vec3d(0, 1, 0))); break;
				case 1: trisToClip.addAll(test.triangleClipAgainstPlane(new Vec3d(0, h, 0), new Vec3d(0, -1, 0))); break;
				case 2: trisToClip.addAll(test.triangleClipAgainstPlane(Vec3d.ZERO, new Vec3d(1, 0, 0))); break;
				case 3: trisToClip.addAll(test.triangleClipAgainstPlane(new Vec3d(w, 0, 0), new Vec3d(-1, 0, 0))); break;
				}
			}
			nNewTriangles = trisToClip.size();
		}
		for (Triangle triangle : trisToClip) {
			triangle.textureTriangle(gpu);
			// Frag layer
		}
	}

	public Vec3d normal() {
		Vec3d line1 = new Vec3d();
		Vec3d line2 = new Vec3d();
		line1.x = this.vert[1][Triangle.POS_DATA].x - this.vert[0][Triangle.POS_DATA].x;
		line1.y = this.vert[1][Triangle.POS_DATA].y - this.vert[0][Triangle.POS_DATA].y;
		line1.z = this.vert[1][Triangle.POS_DATA].z - this.vert[0][Triangle.POS_DATA].z;

		line2.x = this.vert[2][Triangle.POS_DATA].x - this.vert[0][Triangle.POS_DATA].x;
		line2.y = this.vert[2][Triangle.POS_DATA].y - this.vert[0][Triangle.POS_DATA].y;
		line2.z = this.vert[2][Triangle.POS_DATA].z - this.vert[0][Triangle.POS_DATA].z;

		Vec3d normal = new Vec3d();
		normal.x = line1.y * line2.z - line1.z * line2.y;
		normal.y = line1.z * line2.x - line1.x * line2.z;
		normal.z = line1.x * line2.y - line1.y * line2.x;
		normal.normalize();

		return normal;
	}

	public void triangle(GPUContext ctx, int x0, int y0, int x1, int y1, int x2, int y2, int col) {
		System.out.println("Triangle: " + x0 + ":" + y0 + ", " + x1 + ":" + y1 + ", " + x2 + ":" + y2);
		line(ctx, x0, y0, x1, y1, col);
		line(ctx, x1, y1, x2, y2, col);
		line(ctx, x2, y2, x0, y0, col);
	}

	public void line(GPUContext ctx, int x0, int y0, int x1, int y1, int col) {
		int dx = Math.abs(x1 - x0);
		int sx = x0 < x1 ? 1 : -1;
		int dy = -Math.abs(y1 - y0);
		int sy = y0 < y1 ? 1 : -1;
		int err = dx + dy; /* error value e_xy */
		while (true) { /* loop */
			ctx.set(x0, y0, col);
			if (x0 == x1 && y0 == y1) break;
			int e2 = 2 * err;
			if (e2 >= dy) {
				err += dy; /* e_xy+e_x > 0 */
				x0 += sx;
			}
			if (e2 <= dx) { /* e_xy+e_y < 0 */
				err += dx;
				y0 += sy;
			}
		}
	}
}