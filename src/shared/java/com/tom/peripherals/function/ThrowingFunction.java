package com.tom.peripherals.function;

@FunctionalInterface
public interface ThrowingFunction<T, R, X extends Throwable> {
	R apply(T t) throws X;
}
