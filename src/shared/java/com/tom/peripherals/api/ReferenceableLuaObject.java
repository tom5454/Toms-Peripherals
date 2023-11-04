package com.tom.peripherals.api;

import com.tom.peripherals.util.IReferenceable;
import com.tom.peripherals.util.ReferenceManager;

public class ReferenceableLuaObject extends TMLuaObject implements IReferenceable {
	private final String ref;

	public ReferenceableLuaObject(ReferenceManager mngr) {
		ref = mngr.createReference(this);
	}

	@LuaMethod
	@Override
	public Object ref() {
		return ref;
	}
}