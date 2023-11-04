package com.tom.peripherals.gpu;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import com.tom.peripherals.api.LuaException;
import com.tom.peripherals.gpu.font.Font.CustomFont;
import com.tom.peripherals.util.ReferenceManager;

public class VRAM {
	private final long maxMemory;
	private Map<String, CustomFont> internalFonts = new HashMap<>();
	private Set<VRAMObject> objects = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
	private ReferenceManager refMngr = new ReferenceManager();

	public VRAM(long maxMemory) {
		this.maxMemory = maxMemory;
	}

	public Map<String, CustomFont> getIntFonts() {
		return internalFonts;
	}

	public static interface VRAMObject {
		long getSize();
	}

	public void alloc(VRAMObject o) {
		objects.add(o);
	}

	public long getUsedMemory() {
		return objects.stream().mapToLong(VRAMObject::getSize).sum();
	}

	public long getMaxMemory() {
		return maxMemory;
	}

	public boolean checkSize(long size) {
		long all = getUsedMemory();
		return all + size <= maxMemory;
	}

	public void checkSizeEx(long size) throws LuaException {
		if (!checkSize(size))throw new LuaException("Alloc failed: Out of VRAM");
	}

	public boolean tryAlloc(VRAMObject o, long size) {
		if (checkSize(size)) {
			alloc(o);
			return true;
		} else
			return false;
	}

	public void allocEx(VRAMObject o, long size) throws LuaException {
		if (!tryAlloc(o, size))throw new LuaException("Alloc failed: Out of VRAM");
	}

	public boolean realloc(VRAMObject o, long size) {
		long all = objects.stream().filter(e -> e != o).mapToLong(VRAMObject::getSize).sum();
		return all + size <= maxMemory;
	}

	public void reallocEx(VRAMObject o, long size) throws LuaException {
		if (!realloc(o, size))throw new LuaException("Realloc failed: Out of VRAM");
	}

	public void free(VRAMObject o) {
		objects.remove(o);
	}

	public ReferenceManager getRefMngr() {
		return refMngr;
	}
}
