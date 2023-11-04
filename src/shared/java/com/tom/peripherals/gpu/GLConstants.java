package com.tom.peripherals.gpu;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class GLConstants {
	//Begin modes
	@Const
	public static final int
	//GL_LINES          = 0x1,
	//GL_LINE_LOOP      = 0x2,
	//GL_LINE_STRIP     = 0x3,
	GL_TRIANGLES      = 0x4
	//GL_TRIANGLE_STRIP = 0x5,
	//GL_TRIANGLE_FAN   = 0x6,
	//GL_QUADS          = 0x7,
	//GL_QUAD_STRIP     = 0x8,
	//GL_POLYGON        = 0x9
	;

	@Const
	public static final int
	GL_TEXTURE_2D              = 0xDE1
	;

	public static @interface Const {}

	public static final Object[] ALL_CONST = create();

	private static Object[] create() {
		List<Object> consts = new ArrayList<>();
		try {
			for (Field f : GLConstants.class.getDeclaredFields()) {
				if (f.isAnnotationPresent(Const.class)) {
					consts.add(f.getName());
					consts.add(f.get(null));
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return consts.toArray();
	}
}
