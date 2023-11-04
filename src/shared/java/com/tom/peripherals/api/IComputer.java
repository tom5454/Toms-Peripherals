package com.tom.peripherals.api;

public interface IComputer {
	void queueEvent(String event, Object[] arguments);
	String getAttachmentName();
	Object[] mapTo(Object[] in);
	Object[] mapFrom(Object[] in);
	Object[] pullEvent(String string) throws LuaException;
}