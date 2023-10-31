package com.tom.peripherals.util;

@FunctionalInterface
public interface ThrowingRunnable<X extends Throwable> {
	public void run() throws X;
}
