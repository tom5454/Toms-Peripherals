package com.tom.peripherals.util;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, X extends Throwable> {
	R apply(T t, U u) throws X;
}
