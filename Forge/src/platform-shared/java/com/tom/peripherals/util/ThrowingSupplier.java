package com.tom.peripherals.util;

@FunctionalInterface
public interface ThrowingSupplier<T, X extends Throwable> {
	T get() throws X;
}
