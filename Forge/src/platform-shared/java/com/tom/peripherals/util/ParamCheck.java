package com.tom.peripherals.util;

import java.util.Arrays;
import java.util.stream.IntStream;

import net.minecraft.util.Mth;

import com.tom.peripherals.util.ITMPeripheral.LuaException;

public class ParamCheck {
	public static void isNumber(Object[] a, int arg) throws LuaException {
		if (!(arg < a.length && a[arg] instanceof Double)) {
			throw new LuaException("Bad argument #" + (arg + 1) + ": (expected Number)");
		}
	}

	public static int optionalInt(Object[] a, int arg, int def) throws LuaException {
		if (arg < a.length) {
			return getInt(a, arg);
		} else
			return def;
	}

	public static int getInt(Object[] a, int arg) throws LuaException {
		isNumber(a, arg);
		return Mth.floor((Double) a[arg]);
	}

	public static double getDouble(Object[] a, int arg) throws LuaException {
		isNumber(a, arg);
		return (Double) a[arg];
	}

	public static String getString(Object[] a, int arg) throws LuaException {
		if (!(arg < a.length && a[arg] != null)) {
			throw new LuaException("Bad argument #" + (arg + 1) + ": (expected Number)");
		}
		return a[arg].toString();
	}

	public static boolean getBoolean(Object[] a, int arg) throws LuaException {
		if (!(arg < a.length && a[arg] instanceof Boolean)) {
			throw new LuaException("Bad argument #" + (arg + 1) + ": (expected Boolean)");
		}
		return (Boolean) a[arg];
	}

	public static IntStream ints(Object[] a, int arg) throws LuaException {
		return Arrays.stream(a, arg, a.length).mapToInt(e -> {
			if (e instanceof Double d)
				return Mth.floor(d);
			else
				throw new LuaException("Bad argument #" + (arg + 1) + "-" + a.length + ": (expected Number)");
		});
	}

	public static IntStream uints(Object[] a, int arg) throws LuaException {
		return Arrays.stream(a, arg, a.length).mapToInt(e -> {
			if (e instanceof Double d)
				return (int) d.longValue();
			else
				throw new LuaException("Bad argument #" + (arg + 1) + "-" + a.length + ": (expected Number)");
		});
	}

	public static int toColor(Object[] args, int start) throws LuaException {
		if (args.length == start) {
			throw new LuaException(
					"Too few arguments expected number argb or [number r/a], [number g/r], [number b/g], [number b]");
		} else if (args.length == start + 3) {
			if (args[start] instanceof Double && args[start + 1] instanceof Double
					&& args[start + 2] instanceof Double) {
				int r = Mth.floor((double) args[start]) & 0xFF;
				int g = Mth.floor((double) args[start + 1]) & 0xFF;
				int b = Mth.floor((double) args[start + 2]) & 0xFF;

				return 0xFF000000 | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
			} else {
				throw new LuaException("bad arguments: numbers expected");
			}
		} else if (args.length == start + 4) {
			if (args[start] instanceof Double && args[start + 1] instanceof Double && args[start + 2] instanceof Double
					&& args[start + 3] instanceof Double) {
				int r = Mth.floor((double) args[start]) & 0xFF;
				int g = Mth.floor((double) args[start + 1]) & 0xFF;
				int b = Mth.floor((double) args[start + 2]) & 0xFF;
				int a = Mth.floor((double) args[start + 3]) & 0xFF;

				return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
			} else {
				throw new LuaException("bad arguments: numbers expected");
			}
		} else {
			isNumber(args, start);
			long color = ((Double) args[start]).longValue();
			return (int) color;
		}
	}
}
