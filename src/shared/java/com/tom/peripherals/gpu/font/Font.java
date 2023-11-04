package com.tom.peripherals.gpu.font;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.tom.peripherals.api.LuaException;

public class Font {
	public static final Font MISSING = new Font();
	public String name;
	public int[][] chars;
	public String chars2;
	public byte[] widths;
	public int fontHeight, UNKNOWN;

	public static Font load(String name){
		InputStream s = null;
		s = Font.class.getResourceAsStream(name + ".bin");
		if(s == null)return null;
		try{
			return new Font(s, name);
		}catch(IllegalStateException e){
			if("CUSTOMFONT".equals(e.getMessage()))
				return new CustomFont(name);
			else throw e;
		}
	}

	public Font(InputStream stream, String name) {
		this.name = name;
		DataInputStream s = new DataInputStream(stream);
		StringBuilder b = new StringBuilder();
		try {
			fontHeight = s.readByte();
			if(fontHeight == -1)throw new IllegalStateException("CUSTOMFONT");
			int size = s.readInt();
			for(int i = 0;i<size;i++){
				b.append(s.readChar());
			}
			widths = new byte[size];
			chars = new int[size][fontHeight];
			for(int i = 0;i<size;i++){
				widths[i] = s.readByte();
				for(int j = 0;j<fontHeight;j++){
					chars[i][j] = s.readInt();
				}
			}
			s.close();
		} catch (IOException e) {
			try {
				s.close();
			} catch (IOException e1) {
			}
			throw new RuntimeException("Couldn't read font", e);
		}
		chars2 = b.toString();
		UNKNOWN = chars2.indexOf('?');
		if(UNKNOWN == -1)UNKNOWN = 0;
	}
	private Font() {}
	/*public static void main(String[] args) {
		File o = new File(".", "font_out");
		for(int i = 0;i<5;i++){
			DataOutputStream str = null;
			File f = new File(o, "unicode_page_e" + i + ".bin");
			System.out.println(f.getName());
			try {
				ByteArrayOutputStream s = new ByteArrayOutputStream();
				str = new DataOutputStream(new DualOutputStream(new FileOutputStream(f), s));
				str.writeByte(-1);
				str.close();
				System.out.println("new byte[]" + Arrays.toString(s.toByteArray()).replace('[', '{').replace(']', '}'));
			}catch(IOException e){
				e.printStackTrace();
				IOUtils.closeQuietly(str);
			}
		}
	}*/
	public Font getFont(Map<String, CustomFont> internalFonts){
		return this;
	}
	public boolean editable(){
		return false;
	}
	public static class CustomFont extends Font {
		public CustomFont(String name) {
			this.name = name;
		}
		public CustomFont(CustomFont s) {
			this.name = s.name;
			this.fontHeight = 16;
			this.UNKNOWN = 0;
			this.widths = new byte[256];
			this.chars2 = String.join("", Collections.nCopies(widths.length, "\u0000"));
			Arrays.fill(widths, (byte) 1);
			chars = new int[widths.length][fontHeight];
		}
		@Override
		public Font getFont(Map<String, CustomFont> internalFonts) {
			CustomFont f = internalFonts.get(name);
			if(f == null)internalFonts.put(name, f = new CustomFont(this));
			return f;
		}
		@Override
		public boolean editable() {
			return true;
		}
		@Override
		public int freeChars() {
			return chars2.chars().filter(c -> c == 0).map(__ -> 1).sum();
		}
		@Override
		public int addChar(String c, int[] d) throws LuaException {
			char cIn = c.charAt(0);
			for(int i = 0;i<chars2.length();i++){
				if(chars2.charAt(i) == cIn)throw new LuaException("Character already exists in sprite");
			}
			for(int i = 0;i<chars2.length();i++){
				if(chars2.charAt(i) == 0){
					StringBuilder b = new StringBuilder(chars2);
					b.setCharAt(i, cIn);
					chars2 = b.toString();
					widths[i] = (byte) d[0];
					System.arraycopy(d, 1, chars[i], 0, 16);
					return i + 1;
				}
			}
			throw new LuaException("Sprite is full");
		}
		@Override
		public void remove(String c) throws LuaException {
			char cIn = c.charAt(0);
			for(int i = 0;i<chars2.length();i++){
				if(chars2.charAt(i) == cIn){
					StringBuilder b = new StringBuilder(chars2);
					b.setCharAt(i, '\u0000');
					chars2 = b.toString();
					return;
				}
			}
			throw new LuaException("Character not found");
		}
		@Override
		public void clear() throws LuaException {
			Arrays.fill(widths, (byte) 1);
			chars = new int[widths.length][fontHeight];
			this.chars2 = String.join("", Collections.nCopies(widths.length, "\u0000"));
		}
	}
	public int freeChars() {
		return 0;
	}

	public void clear() throws LuaException {
		throw new LuaException("Selected font is not modifiable");
	}

	public int addChar(String c, int[] d) throws LuaException {
		throw new LuaException("Selected font is not modifiable");
	}

	public void remove(String c) throws LuaException {
		throw new LuaException("Selected font is not modifiable");
	}
}
