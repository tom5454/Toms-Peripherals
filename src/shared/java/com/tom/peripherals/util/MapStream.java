package com.tom.peripherals.util;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class MapStream<T> {
	private Stream<Pair<String, T>> stream;

	public MapStream(Stream<Pair<String, T>> stream) {
		this.stream = stream;
	}

	public static <T> MapStream<T> of(Map<String, T> map) {
		return new MapStream<>(map.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())));
	}

	public <R> MapStream<R> mapOValue(Function<T, R> f) {
		return new MapStream<>(stream.map(p -> Pair.of(p.getKey(), f.apply(p.getValue()))));
	}

	public void put(Map<String, T> map) {
		stream.forEach(e -> map.put(e.getKey(), e.getValue()));
	}
}