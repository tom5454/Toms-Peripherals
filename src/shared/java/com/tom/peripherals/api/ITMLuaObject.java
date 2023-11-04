package com.tom.peripherals.api;

public interface ITMLuaObject {
	String[] getMethodNames();
	Object[] call(IComputer computer, String method, Object[] args) throws LuaException;
}