package com.tom.peripherals.util;

@FunctionalInterface
public interface ThrowingTriConsumer<T, U, V, X extends Throwable> {
	void accept(T t, U u, V v) throws X;
}
