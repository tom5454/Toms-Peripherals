package com.tom.peripherals.function;

@FunctionalInterface
public interface ThrowingBiConsumer<T, U, X extends Throwable> {
	void accept(T t, U u) throws X;
}
