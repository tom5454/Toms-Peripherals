package com.tom.peripherals.function;

@FunctionalInterface
public interface ThrowingSupplier<T, X extends Throwable> {
	T get() throws X;
}
