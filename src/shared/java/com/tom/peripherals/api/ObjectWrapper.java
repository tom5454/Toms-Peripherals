package com.tom.peripherals.api;

import java.util.ArrayList;
import java.util.List;

public class ObjectWrapper implements ITMPeripheral {
	private List<IComputer> computers = new ArrayList<>();
	private final String type;
	private final TMLuaObject object;

	public ObjectWrapper(String type, TMLuaObject object) {
		this.type = type;
		this.object = object;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String[] getMethodNames() {
		return object.getMethodNames();
	}

	@Override
	public Object[] call(IComputer computer, String method, Object[] args) throws LuaException {
		try {
			return object.callInt(computer, method, args);
		} catch (NoSuchMethodException e) {
			throw new LuaException("No such method");
		}
	}

	@Override
	public void attach(IComputer computer) {
		computers.add(computer);
	}

	@Override
	public void detach(IComputer computer) {
		computers.remove(computer);
	}

	public void queueEvent(String event, Object[] args) {
		Object[] a = new Object[args.length + 1];
		for (int i = 0;i < args.length;i++) {
			a[i + 1] = args[i];
		}
		for (IComputer c : computers) {
			a[0] = c.getAttachmentName();
			c.queueEvent(event, a);
		}
	}
}