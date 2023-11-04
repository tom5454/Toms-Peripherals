package com.tom.peripherals.gpu.font;

import java.util.HashMap;
import java.util.Map;

import com.tom.peripherals.api.LuaException;
import com.tom.peripherals.gpu.font.Font.CustomFont;

public class FontManager {
	public static void init(){
		Font f = null;
		try{f = Font.load("ascii");}catch(Throwable e){
			e.printStackTrace();
			f = Font.MISSING;
		}
		if(f == null)throw new RuntimeException("Missing ascii.bin file from the mod JAR!! Please redownload the mod from Curseforge or Modrinth! DO NOT REPORT THIS!!");
		fonts.put("ascii", f);
		DEF = f;
	}

	public static Map<String, Font> fonts = new HashMap<>();
	public static Font DEF;

	public static Font getOrLoadFont(Map<String, CustomFont> internalFonts, String s) throws LuaException {
		Font f = fonts.get(s);
		if(f != null)return f.getFont(internalFonts);
		try{
			f = Font.load(s);
		}catch(Throwable e){
			fonts.put(s, Font.MISSING);
			throw new LuaException(e.getMessage());
		}
		if(f == null){
			fonts.put(s, Font.MISSING);
			throw new LuaException("Font file not found");
		}
		fonts.put(s, f);
		return f.getFont(internalFonts);
	}
}
