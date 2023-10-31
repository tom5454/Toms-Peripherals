package com.tom.peripherals.util;

import com.tom.peripherals.util.ITMPeripheral.LuaMethod;

public interface IReferenceable {

	@LuaMethod
	public Object ref();
}
