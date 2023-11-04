package com.tom.peripherals.gpu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import com.tom.peripherals.api.LuaException;
import com.tom.peripherals.api.LuaMethod;
import com.tom.peripherals.api.ReferenceableLuaObject;
import com.tom.peripherals.gpu.VRAM.VRAMObject;
import com.tom.peripherals.util.ParamCheck;

public class LuaByteBuffer extends ReferenceableLuaObject implements VRAMObject {
	private final VRAM mngr;
	private byte[] buf;
	private int writeIndex, readIndex;

	public LuaByteBuffer(VRAM mngr) throws LuaException {
		this(mngr, 32);
	}

	public LuaByteBuffer(VRAM mngr, int initSize) throws LuaException {
		super(mngr.getRefMngr());
		this.mngr = mngr;
		mngr.allocEx(this, initSize);
		buf = new byte[initSize];
	}

	@Override
	public long getSize() {
		return buf.length;
	}

	@LuaMethod
	public int length() {
		if (buf == null)throw new LuaException("Error: Use after free");
		return writeIndex;
	}

	@LuaMethod
	public int available() {
		if (buf == null)throw new LuaException("Error: Use after free");
		return writeIndex - readIndex;
	}

	@LuaMethod
	public void write(Object[] d) throws LuaException {
		if (buf == null)throw new LuaException("Error: Use after free");
		int[] dt = ParamCheck.ints(d, 0).toArray();
		if (dt.length == 0)return;
		ensureCapacity(writeIndex + dt.length);
		for (int i = 0; i < dt.length; i++) {
			buf[writeIndex + i] = (byte) dt[i];
		}
		writeIndex += dt.length;
	}

	@LuaMethod
	public Object[] read(Object[] d) throws LuaException {
		if (buf == null)throw new LuaException("Error: Use after free");
		int amount = ParamCheck.optionalInt(d, 0, 1);
		if (amount < 0)amount = writeIndex - readIndex;
		else if(writeIndex - readIndex < amount)throw new LuaException("Index out of bounds");
		Object[] r = new Object[amount];
		for (int i = 0; i < amount; i++) {
			r[i] = Byte.toUnsignedInt(buf[readIndex + i]);
		}
		readIndex += amount;
		return r;
	}

	private void ensureCapacity(int minCapacity) throws LuaException {
		// overflow-conscious code
		if (minCapacity - buf.length > 0)
			grow(minCapacity);
	}

	private void grow(int minCapacity) throws LuaException {
		// overflow-conscious code
		int oldCapacity = buf.length;
		int newCapacity = oldCapacity << 1;
		if (newCapacity - minCapacity < 0)
			newCapacity = minCapacity;

		mngr.reallocEx(this, newCapacity);

		buf = Arrays.copyOf(buf, newCapacity);
	}

	@LuaMethod
	public void free() throws LuaException {
		if(buf == null)return;
		mngr.getRefMngr().remove(this);
		mngr.free(this);
		buf = null;
	}

	public void write(byte b[], int off, int len) {
		if ((off < 0) || (off > b.length) || (len < 0) ||
				((off + len) - b.length > 0)) {
			throw new IndexOutOfBoundsException();
		}
		ensureCapacity(writeIndex + len);
		System.arraycopy(b, off, buf, writeIndex, len);
		writeIndex += len;
	}

	public InputStream asInputStream() {
		return new InputStream() {
			int pos = readIndex;
			int count = writeIndex;

			@Override
			public int read() throws IOException {
				return (pos < count) ? (buf[pos++] & 0xff) : -1;
			}

			@Override
			public int read(byte b[], int off, int len) {
				if (b == null) {
					throw new NullPointerException();
				} else if (off < 0 || len < 0 || len > b.length - off) {
					throw new IndexOutOfBoundsException();
				}

				if (pos >= count) {
					return -1;
				}

				int avail = count - pos;
				if (len > avail) {
					len = avail;
				}
				if (len <= 0) {
					return 0;
				}
				System.arraycopy(buf, pos, b, off, len);
				pos += len;
				return len;
			}
		};
	}

	public OutputStream asOutputStream() {
		return new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				ensureCapacity(writeIndex + 1);
				buf[writeIndex] = (byte) b;
				writeIndex += 1;
			}

			@Override
			public void write(byte b[], int off, int len) {
				if ((off < 0) || (off > b.length) || (len < 0) ||
						((off + len) - b.length > 0)) {
					throw new IndexOutOfBoundsException();
				}
				ensureCapacity(writeIndex + len);
				System.arraycopy(b, off, buf, writeIndex, len);
				writeIndex += len;
			}
		};
	}
}
