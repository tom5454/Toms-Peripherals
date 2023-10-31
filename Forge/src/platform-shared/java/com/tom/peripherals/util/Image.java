package com.tom.peripherals.util;

public class Image {
	private final int[] data;
	private final int w, h;
	public Image(int w, int h) {
		this.w = w;
		this.h = h;
		data = new int[w * h];
	}

	public Image(Image cpyFrom) {
		this(cpyFrom.w, cpyFrom.h);
		System.arraycopy(cpyFrom.data, 0, data, 0, data.length);
	}

	public Image(int[] data, int w) {
		this.data = data;
		this.w = w;
		this.h = data.length / w;
	}

	public void setRGB(int x, int y, int rgb) {
		data[y * w + x] = rgb;
	}

	public int getRGB(int x, int y) {
		return data[y * w + x];
	}

	public int[] getData() {
		return data;
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}

	public void draw(Image i) {
		for(int x = 0;x<w && x<i.w;x++) {
			for(int y = 0;y<h && y<i.h;y++) {
				data[y * w + x] = i.data[y * i.w + x];
			}
		}
	}

	public void draw(Image i, int xs, int ys) {
		for(int x = 0;x + xs < w && x < i.w;x++) {
			for(int y = 0;y + ys < h && y < i.h;y++) {
				int p = (y + ys) * w + x + xs;
				if(p < 0)continue;
				data[p] = i.data[y * i.w + x];
			}
		}
	}

	public void draw(Image i, int xs, int ys, int w, int h) {
		for(int y = ys;y<h;y++) {
			for(int x = xs;x<w;x++) {
				float fx = x / (float) w;
				float fy = y / (float) h;
				data[y * w + x] = i.getRGB(Math.min((int) (fx * i.getWidth()), i.getWidth()-1), Math.min((int) (fy * i.getHeight()), i.getHeight()-1));
			}
		}
	}

	public void fill(int color) {
		for(int y = 0;y<h;y++) {
			for(int x = 0;x<w;x++) {
				data[y * w + x] = color;
			}
		}
	}

	public void fill(int xs, int ys, int w, int h, int color) {
		for(int x = 0;x + xs < this.w && x < w;x++) {
			for(int y = 0;y + ys < this.h && y < h;y++) {
				data[(y + ys) * this.w + x + xs] = color;
			}
		}
	}
}
