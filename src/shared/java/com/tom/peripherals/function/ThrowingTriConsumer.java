package com.tom.peripherals.function;

@FunctionalInterface
public interface ThrowingTriConsumer<T, U, V, X extends Throwable> {
	void accept(T t, U u, V v) throws X;
}
