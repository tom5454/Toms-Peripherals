package com.tom.peripherals.util;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.MapMaker;

public class ReferenceManager {
	private Random rng = new Random();
	private Map<String, IReferenceable> refs = new MapMaker().weakValues().makeMap();

	public String createReference(IReferenceable ref) {
		String key = "ref:" + Integer.toHexString((ref.hashCode() ^ rng.nextInt()));
		refs.put(key, ref);
		return key;
	}

	public IReferenceable getByReference(String ref) {
		return refs.get(ref);
	}

	public void remove(IReferenceable obj) {
		refs.remove(obj.ref());
	}
}
