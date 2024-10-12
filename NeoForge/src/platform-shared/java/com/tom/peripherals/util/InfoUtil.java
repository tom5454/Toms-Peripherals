package com.tom.peripherals.util;

public class InfoUtil {
	public static String getDirectionArrow(float yRotation) {
		// Normalize the yRotation to be between 0 and 360
		yRotation = yRotation % 360;
		if (yRotation < 0) {
			yRotation += 360;
		}

		// Calculate the direction based on the normalized yRotation
		if (yRotation >= 337.5f || yRotation < 22.5f) {
			return "\u2191"; // North
		} else if (yRotation >= 22.5f && yRotation < 67.5f) {
			return "\u2197"; // North-East
		} else if (yRotation >= 67.5f && yRotation < 112.5f) {
			return "\u2192"; // East
		} else if (yRotation >= 112.5f && yRotation < 157.5f) {
			return "\u2198"; // South-East
		} else if (yRotation >= 157.5f && yRotation < 202.5f) {
			return "\u2193"; // South
		} else if (yRotation >= 202.5f && yRotation < 247.5f) {
			return "\u2199"; // South-West
		} else if (yRotation >= 247.5f && yRotation < 292.5f) {
			return "\u2190"; // West
		} else if (yRotation >= 292.5f && yRotation < 337.5f) {
			return "\u2196"; // North-West
		}

		// Default to a North arrow if something goes wrong
		return "\u2191"; // North
	}

	public static String ticksToElapsedTime(int ticks) {
		int i = ticks / 20;
		int j = i / 60;
		i = i % 60;
		return i < 10 ? j + ":0" + i : j + ":" + i;
	}
}
