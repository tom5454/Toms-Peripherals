package com.tom.peripherals.math;

public class MathHelper {

	public static double clamp(double num, double min, double max) {
		if (num < min) {
			return min;
		} else {
			return num > max ? max : num;
		}
	}

	public static float clamp(float num, float min, float max) {
		if (num < min) {
			return min;
		} else {
			return num > max ? max : num;
		}
	}

	public static int floor(double val) {
		int i = (int)val;
		return val < i ? i - 1 : i;
	}
}
