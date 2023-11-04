package com.tom.peripherals.function;

@FunctionalInterface
public interface ThrowingTriFunction<T, U, V, R, X extends Throwable> {
	R apply(T t, U u, V v) throws X;
}
