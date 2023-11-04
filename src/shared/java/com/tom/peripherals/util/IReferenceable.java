package com.tom.peripherals.util;

import com.tom.peripherals.api.LuaMethod;

public interface IReferenceable {

	@LuaMethod
	public Object ref();
}
