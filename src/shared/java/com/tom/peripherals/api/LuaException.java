package com.tom.peripherals.api;

public class LuaException extends RuntimeException {
	private static final long serialVersionUID = -6487582951282118489L;
	private final int level;

	public LuaException() {
		this("error", 1);
	}

	public LuaException(String message ) {
		this(message, 1);
	}

	public LuaException(String message, int level ) {
		super(message);
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public class LuaInteruptedException extends LuaException {
		private static final long serialVersionUID = -7396879728472447101L;

	}
}