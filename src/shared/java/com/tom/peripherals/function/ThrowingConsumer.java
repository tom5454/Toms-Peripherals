package com.tom.peripherals.function;

@FunctionalInterface
public interface ThrowingConsumer<T, X extends Throwable> {
	void accept(T t) throws X;
}
