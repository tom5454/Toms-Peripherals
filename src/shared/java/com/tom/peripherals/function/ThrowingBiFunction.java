package com.tom.peripherals.function;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, X extends Throwable> {
	R apply(T t, U u) throws X;
}
