package com.tom.peripherals.gpu;

import com.tom.peripherals.api.LuaException;
import com.tom.peripherals.api.LuaMethod;
import com.tom.peripherals.api.TMLuaObject;
import com.tom.peripherals.gpu.font.Font;
import com.tom.peripherals.gpu.font.FontManager;
import com.tom.peripherals.util.Image;
import com.tom.peripherals.util.ParamCheck;

public class BaseGPU extends TMLuaObject {
	public Font selectedFont = FontManager.DEF;

	public static interface GPUContext {
		void set(int x, int y, int c);

		void sync() throws LuaException;

		VRAM getVRam();

		int getWidth();

		int getHeight();

		default Rect getBounds() {
			return new Rect(getWidth(), getHeight());
		}
	}

	public BaseGPU(GPUContext ctx) {
		this.ctx = ctx;
	}

	public BaseGPU() {
		this.ctx = (GPUContext) this;
	}

	protected GPUContext ctx;

	@LuaMethod
	public boolean fill(Object[] a) throws LuaException {
		int color;
		if (a.length == 0) {
			color = 0x000000;
		} else {
			color = ParamCheck.toColor(a, 0);
		}
		for (int i = 0; i < ctx.getWidth(); i++) {
			for (int j = 0; j < ctx.getHeight(); j++) {
				ctx.set(i, j, color);
			}
		}
		return true;
	}

	@LuaMethod
	public boolean filledRectangle(Object[] a) throws LuaException {
		int color;
		if (a.length < 4) {
			throw new LuaException("Too few arguments (expected x,y,width,height,[color])");
		}
		if (a.length < 5) {
			color = 0x000000;
		} else {
			color = ParamCheck.toColor(a, 4);
		}
		int xStart = ParamCheck.getInt(a, 0) - 1;
		int yStart = ParamCheck.getInt(a, 1) - 1;
		int xStop = xStart + ParamCheck.getInt(a, 2);
		int yStop = yStart + ParamCheck.getInt(a, 3);
		if (xStart < ctx.getWidth() + 1 && yStart < ctx.getHeight() + 1 && xStart >= 0 && yStart >= 0) {
			xStop = xStop > ctx.getWidth() ? ctx.getWidth() : xStop;
			yStop = yStop > ctx.getHeight() ? ctx.getHeight() : yStop;
			for (int i = xStart; i < xStop; i++) {
				for (int y = yStart; y < yStop; y++) {
					ctx.set(i, y, color);
				}
			}
			return true;
		} else {
			throw new LuaException("Out of boundary");
		}
	}

	@LuaMethod
	public boolean rectangle(Object[] a) throws LuaException {
		int color;
		if (a.length < 4) {
			throw new LuaException("Too few arguments (expected x,y,width,height,[color])");
		}
		if (a.length < 5) {
			color = 0x000000;
		} else {
			color = ParamCheck.toColor(a, 4);
		}
		int xStart = ParamCheck.getInt(a, 0) - 1;
		int yStart = ParamCheck.getInt(a, 1) - 1;
		int xStop = xStart + ParamCheck.getInt(a, 2);
		int yStop = yStart + ParamCheck.getInt(a, 3);
		if (xStart < ctx.getWidth() + 1 && yStart < ctx.getHeight() + 1 && xStart >= 0 && yStart >= 0) {
			xStop = xStop > ctx.getWidth() ? ctx.getWidth() : xStop;
			yStop = yStop > ctx.getHeight() ? ctx.getHeight() : yStop;
			for (int i = xStart; i < xStop; i++) {
				for (int y = yStart; y < yStop; y++) {
					if (i == xStart || i == xStop - 1) ctx.set(i, y, color);
					else if (y == yStart || y == yStop - 1) ctx.set(i, y, color);
				}
			}
			return true;
		} else {
			throw new LuaException("Out of boundary");
		}
	}

	@LuaMethod
	public boolean line(Object[] a) throws LuaException {
		int color;
		if (a.length < 4) {
			throw new LuaException("Too few arguments (expected x1,y1,x2,y2,[color])");
		}
		if (a.length < 5) {
			color = 0x000000;
		} else {
			color = ParamCheck.toColor(a, 4);
		}
		int xStart = ParamCheck.getInt(a, 0) - 1;
		int yStart = ParamCheck.getInt(a, 1) - 1;
		int xStop = ParamCheck.getInt(a, 2) - 1;
		int yStop = ParamCheck.getInt(a, 3) - 1;
		try {
			line(xStart, yStart, xStop, yStop, color);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new LuaException(e.getMessage());
		}
		return true;
	}

	@LuaMethod
	public boolean lineS(Object[] a) throws LuaException {
		int color;
		if (a.length < 4) {
			throw new LuaException("Too few arguments (expected x1,y1,x2,y2,[color])");
		}
		if (a.length < 5) {
			color = 0x000000;
		} else {
			color = ParamCheck.toColor(a, 4);
		}
		int xStart = ParamCheck.getInt(a, 0) - 1;
		int yStart = ParamCheck.getInt(a, 1) - 1;
		int xStop = ParamCheck.getInt(a, 2) - 1;
		int yStop = ParamCheck.getInt(a, 3) - 1;
		try {
			lineF(xStart, yStart, xStop, yStop, color);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new LuaException(e.getMessage());
		}
		return true;
	}

	public void lineF(int x0, int y0, int x1, int y1, int col) {
		if (x0 < 0 || x1 < 0 || x0 > ctx.getWidth() || x1 > ctx.getWidth())
			throw new ArrayIndexOutOfBoundsException("Out of bounds: x");
		if (y0 < 0 || y1 < 0 || y0 > ctx.getHeight() || y1 > ctx.getHeight())
			throw new ArrayIndexOutOfBoundsException("Out of bounds: y");
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		int sgnX = x0 < x1 ? 1 : -1;
		int sgnY = y0 < y1 ? 1 : -1;
		int e = 0;
		for (int i = 0; i < dx + dy; i++) {
			ctx.set(x0, y0, col);
			int e1 = e + dy;
			int e2 = e - dx;
			if (Math.abs(e1) < Math.abs(e2)) {
				x0 += sgnX;
				e = e1;
			} else {
				y0 += sgnY;
				e = e2;
			}
		}
	}

	public void line(int x0, int y0, int x1, int y1, int col) {
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

	@LuaMethod
	public Object[] getSize() {
		return new Object[] { this.ctx.getWidth(), this.ctx.getHeight() };
	}

	@LuaMethod
	public boolean drawText(Object[] a) throws LuaException {
		if (a.length < 3) {
			throw new LuaException(
					"Too few arguments, excepted (number x,number y,String text, [number text_color], [number bg_color], [number size], [number padding])");
		}
		if (a.length > 2 && a[0] instanceof Double && a[1] instanceof Double && a[2] != null) {
			int x = ParamCheck.getInt(a, 0) - 1;
			int y = ParamCheck.getInt(a, 1) - 1;
			int c = ParamCheck.optionalInt(a, 3, 0xFFFFFFFF);
			int bg = ParamCheck.optionalInt(a, 4, -1);
			int size = ParamCheck.optionalInt(a, 5, 1);
			int padding = ParamCheck.optionalInt(a, 6, 1);
			String s = ParamCheck.getString(a, 2);
			char[] chars = s.toCharArray();
			int l = getTextLength(chars, size, padding);
			if (x < 0 || (l + x) > ctx.getWidth()) {
				throw new LuaException("Out of boundary x " + x + ":" + (l + x));
			}
			if (y < 0 || (y + selectedFont.fontHeight) > ctx.getHeight()) {
				throw new LuaException("Out of boundary y");
			}
			int wx = x;
			try {
				for (int i = 0; i < chars.length; i++) {
					char d = chars[i];
					int index = selectedFont.chars2.indexOf(d);
					if (index == -1) index = selectedFont.UNKNOWN;
					int[] charData = selectedFont.chars[index];
					int w = selectedFont.widths[index];
					if (d == ' ') {
						w = 5;
						if (bg > -1) {
							for (int j = 0; j < charData.length; j++) {
								for (int k = 0; k < w; k++) {
									fill(wx, k, y, j, size, bg);
								}
							}
						}
					} else {
						for (int j = 0; j < charData.length; j++) {
							int b = charData[j];
							for (int k = 0; k < w; k++) {
								if ((b & (1 << k)) != 0) {
									fill(wx, k, y, j, size, c);
								} else if (bg > -1) {
									fill(wx, k, y, j, size, bg);
								}
							}
						}
					}
					if (bg > -1) {
						for (int j = 0; j < charData.length; j++) {
							for (int k = 0; k < padding; k++) {
								fill(wx, w + k, y, j, size, bg);
							}
						}
					}
					wx += ((w + padding) * size);
				}
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			throw new LuaException(
					"Invalid Arguments, excepted (number x,number y,String text, [number text_color], [number bg_color], [number size], [number padding])");
		}
	}

	@LuaMethod
	public Object[] getFont() {
		return new Object[] { selectedFont.name, selectedFont.editable() };
	}

	@LuaMethod
	public boolean setFont(Object[] a) throws LuaException {
		if (a.length > 0) {
			Font f = FontManager.getOrLoadFont(ctx.getVRam().getIntFonts(), ParamCheck.getString(a, 0));
			if (f != null) selectedFont = f;
			return true;
		} else throw new LuaException("Invalid Arguments, excepted (string font_name)");
	}

	@LuaMethod
	public int getTextLength(Object[] a) throws LuaException {
		if (a.length > 0 && a[0] != null) {
			int size = ParamCheck.optionalInt(a, 1, 1);
			int padding = ParamCheck.optionalInt(a, 2, 1);
			return getTextLength(a[0].toString().toCharArray(), size, padding);
		} else throw new LuaException("Invalid Arguments, excepted (string text, [number size], [number padding])");
	}

	@LuaMethod
	public boolean drawTextSmart(Object[] a) throws LuaException {
		if (a.length < 3) {
			throw new LuaException(
					"Too few arguments, excepted (number x,number y,String text, [number text_color], [number bg_color], [boolean force_unicode], [number size], [number padding])");
		}
		int x = ParamCheck.getInt(a, 0) - 1;
		int y = ParamCheck.getInt(a, 1) - 1;
		int c = ParamCheck.optionalInt(a, 3, 0xFFFFFFFF);
		int bg = ParamCheck.optionalInt(a, 4, -1);
		boolean force_unicode = a.length > 5 && a[5] instanceof Boolean ? (Boolean) a[5] : false;
		int sizeIn = ParamCheck.optionalInt(a, 6, 1);
		int padding = ParamCheck.optionalInt(a, 7, 1);
		String s = ParamCheck.getString(a, 2);
		char[] chars = s.toCharArray();
		int l = getTextLength(chars, sizeIn, padding);
		if (x < 0 || (l + x) > ctx.getWidth()) {
			throw new LuaException("Out of boundary x");
		}
		if (y < 0 || (y + selectedFont.fontHeight) > ctx.getHeight()) {
			throw new LuaException("Out of boundary y");
		}
		int wx = x;
		String font = selectedFont.name;
		try {
			for (int i = 0; i < chars.length; i++) {
				int size = sizeIn;
				char d = chars[i];
				if (!force_unicode && d < 256) {
					size *= 2;
					selectedFont = FontManager.getOrLoadFont(ctx.getVRam().getIntFonts(), "ascii");
				} else {
					selectedFont = FontManager.getOrLoadFont(ctx.getVRam().getIntFonts(),
							String.format("unicode_page_%02x", d / 256));
				}
				int index = selectedFont.chars2.indexOf(d);
				if (index == -1) index = selectedFont.UNKNOWN;
				int[] charData = selectedFont.chars[index];
				int w = selectedFont.widths[index];
				if (d == ' ') {
					w = 5;
					if (bg > -1) {
						for (int j = 0; j < charData.length; j++) {
							for (int k = 0; k < w; k++) {
								fill(wx, k, y, j, size, bg);
							}
						}
					}
				} else {
					for (int j = 0; j < charData.length; j++) {
						int b = charData[j];
						for (int k = 0; k < w; k++) {
							if ((b & (1 << k)) != 0) {
								fill(wx, k, y, j, size, c);
							} else if (bg > -1) {
								fill(wx, k, y, j, size, bg);
							}
						}
					}
				}
				if (bg > -1) {
					for (int j = 0; j < charData.length; j++) {
						for (int k = 0; k < padding; k++) {
							fill(wx, w + k, y, j, size, bg);
						}
					}
				}
				wx += ((w + padding) * size);
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		selectedFont = FontManager.getOrLoadFont(ctx.getVRam().getIntFonts(), font);
		return true;
	}

	@LuaMethod
	public boolean setFontDefaultCharID(Object[] a) throws LuaException {
		if (!selectedFont.editable()) throw new LuaException("Selected font is not modifiable");
		if (a.length > 0) {
			int id = ParamCheck.getInt(a, 0) - 1;
			if (id < 0) throw new LuaException("Bad Argument #1, (too small number (" + id + ") minimum value is 1 )");
			if (id > 255)
				throw new LuaException("Bad Argument #1, (too big number (" + id + ") maximum value is 256 )");
			selectedFont.UNKNOWN = id;
			return true;
		} else {
			throw new LuaException("Invalid Arguments, excepted (number id)");
		}
	}

	@LuaMethod
	public int getFontDefaultCharID() {
		return selectedFont.UNKNOWN;
	}

	@LuaMethod
	public int addNewChar(Object[] a) throws LuaException {
		if (!selectedFont.editable()) throw new LuaException("Selected font is not modifiable");
		if (a.length > 17) {
			String c = ParamCheck.getString(a, 0);
			if (c.length() != 1) throw new LuaException("Bad Argument #1 a sigle character expected");
			int[] d = ParamCheck.ints(a, 1).toArray();
			if (d.length != 16)
				throw new LuaException("Invalid arguments, expected (string char, number width, 16 x number char_data)");
			return selectedFont.addChar(c, d);
		} else throw new LuaException("Invalid arguments, expected (string char, number width, 16 x number char_data)");
	}

	@LuaMethod
	public boolean delChar(Object[] a) throws LuaException {
		if (!selectedFont.editable()) throw new LuaException("Selected font is not modifiable");
		if (a.length > 0 && a[0] != null) {
			String c = ParamCheck.getString(a, 0);
			if (c.length() != 1) throw new LuaException("Bad Argument #1 a sigle character expected");
			selectedFont.remove(c);
			return true;
		} else throw new LuaException("Invalid arguments, expected (string char)");
	}

	@LuaMethod
	public int freeChars() {
		return selectedFont.freeChars();
	}

	@LuaMethod
	public boolean clearChars() throws LuaException {
		selectedFont.clear();
		return true;
	}

	@LuaMethod
	public boolean drawChar(Object[] a) throws LuaException {
		if (a.length < 3) {
			throw new LuaException(
					"Too few arguments, excepted (number x,number y,number char, [number text_color], [number bg_color], [number size])");
		}
		int x = ParamCheck.getInt(a, 0) - 1;
		int y = ParamCheck.getInt(a, 1) - 1;
		int index = ParamCheck.getInt(a, 2) - 1;
		int c = ParamCheck.optionalInt(a, 3, 0xFFFFFFFF);
		int bg = ParamCheck.optionalInt(a, 4, -1);
		int size = ParamCheck.optionalInt(a, 5, 1);
		if (index == -1) index = selectedFont.UNKNOWN;
		int[] charData = selectedFont.chars[index];
		int w = selectedFont.widths[index];
		if (x < 0 || (w * size + x) > ctx.getWidth()) {
			throw new LuaException("Out of boundary x");
		}
		if (y < 0 || (y + charData.length / w * size) > ctx.getHeight()) {
			throw new LuaException("Out of boundary y");
		}
		for (int j = 0; j < charData.length; j++) {
			int b = charData[j];
			for (int k = 0; k < w; k++) {
				if ((b & (1 << k)) != 0) {
					fill(x, k, y, j, size, c);
				} else if (bg > -1) {
					fill(x, k, y, j, size, bg);
				}
			}
		}
		return true;
	}

	@LuaMethod
	public boolean drawBuffer(Object[] a) throws LuaException {
		if (a.length < 4) {
			throw new LuaException(
					"Too few arguments, excepted (number x,number y,number w, number scale, number... data)");
		}
		int x = ParamCheck.getInt(a, 0) - 1;
		int y = ParamCheck.getInt(a, 1) - 1;
		int w = ParamCheck.getInt(a, 2);
		int s = ParamCheck.getInt(a, 3);
		int[] d = ParamCheck.uints(a, 4).toArray();
		if (x < 0 || (w * s + x) > ctx.getWidth()) {
			throw new LuaException("Out of boundary x");
		}
		if (y < 0 || (y + d.length / w * s) > ctx.getHeight()) {
			throw new LuaException("Out of boundary y");
		}
		for (int i = 0; i < d.length; i++)
			fill(x, i % w, y, i / w, s, d[i]);
		return true;
	}

	@LuaMethod
	public boolean drawImage(Object[] a) throws LuaException {
		if (a.length < 3) {
			throw new LuaException(
					"Too few arguments, excepted (number x, number y, image ref)");
		}
		int xS = ParamCheck.getInt(a, 0) - 1;
		int yS = ParamCheck.getInt(a, 1) - 1;
		if (!(ctx.getVRam().getRefMngr().getByReference(ParamCheck.getString(a, 2)) instanceof LuaImage li))
			throw new LuaException("Invalid reference");
		Image img = li.getImage();
		int cW = ctx.getWidth();
		int cH = ctx.getHeight();
		int iW = img.getWidth();
		int iH = img.getHeight();
		for (int x = Math.min(0, -xS);x<iW && x+xS < cW;x++) {
			for (int y = Math.min(0, -yS);y<iH && y+yS < cH;y++) {
				ctx.set(x, y, img.getRGB(x, y));
			}
		}
		return true;
	}

	@LuaMethod
	public void sync() throws LuaException {
		ctx.sync();
	}

	@LuaMethod
	public Object getBounds() throws LuaException {
		return ctx.getBounds();
	}

	private void fill(int x, int ox, int y, int oy, int size, int col) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				ctx.set(x + (ox * size) + i, y + (oy * size) + j, col);
			}
		}
	}

	private int getTextLength(char[] chars, int size, int padding) {
		int l = 0;
		for (int i = 0; i < chars.length; i++) {
			char d = chars[i];
			int index = selectedFont.chars2.indexOf(d);
			if (index == -1) index = selectedFont.UNKNOWN;
			int w = selectedFont.widths[index];
			if (d == ' ') {
				w = 5;
			}
			l += ((w + padding) * size);
		}
		return l;
	}

	@LuaMethod
	public Object createWindow(Object[] a) throws LuaException {
		Rect r = Rect.parseRect(a);
		return new BaseGPU(new WindowGPUContext(ctx, r));
	}

	@LuaMethod
	public Object createWindow3D(Object[] a) throws LuaException {
		Rect r = Rect.parseRect(a);
		return new GPU3D(new WindowGPUContext(ctx, r));
	}
}
