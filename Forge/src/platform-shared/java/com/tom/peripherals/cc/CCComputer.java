package com.tom.peripherals.cc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tom.peripherals.api.IComputer;
import com.tom.peripherals.api.ITMLuaObject;
import com.tom.peripherals.api.LuaException;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.IDynamicLuaObject;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;

public class CCComputer implements IComputer {
	private IComputerAccess c;
	private ILuaContext context;
	public CCComputer(IComputerAccess c, ILuaContext context) {
		this.c = c;
		this.context = context;
	}
	@Override
	public void queueEvent(String event, Object[] arguments) {
		c.queueEvent(event, arguments);
	}

	@Override
	public String getAttachmentName() {
		return c.getAttachmentName();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CCComputer other = (CCComputer) obj;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		return true;
	}
	@Override
	public Object[] mapTo(Object[] in) {
		if(in == null){
			return new Object[0];
		}
		for (int i = 0;i < in.length;i++) {
			in[i] = map(in[i]);
		}
		return in;
	}
	@SuppressWarnings("unchecked")
	private Object map(Object object){
		if(object instanceof ITMLuaObject){
			return new Obj((ITMLuaObject) object);
		} else if(object instanceof List){
			Map<Object, Object> map = new HashMap<>();
			List<Object> list = (List<Object>) object;
			for (int i = 0;i < list.size();i++) {
				map.put(i+1, list.get(i));
			}
			return map;
		} else {
			return object;
		}
	}
	public class Obj implements IDynamicLuaObject {
		private ITMLuaObject obj;
		public Obj(ITMLuaObject obj) {
			this.obj = obj;
		}
		@Override
		public String[] getMethodNames() {
			return obj.getMethodNames();
		}

		@Override
		public MethodResult callMethod(ILuaContext context, int method, IArguments arguments) throws dan200.computercraft.api.lua.LuaException {
			try {
				return MethodResult.of(mapTo(obj.call(CCComputer.this, obj.getMethodNames()[method], mapFrom(arguments.getAll()))));
			} catch (LuaException e) {
				throw toLuaException(e);
			}
		}
	}
	@Override
	public Object[] pullEvent(String string) throws LuaException {
		/*try {
			return context != null ? context.pullEvent(string) : new Object[0];
		} catch (dan200.computercraft.api.lua.LuaException e) {
			throw new ITMPeripheral.LuaException(e.getMessage(), e.getLevel());
		} catch (InterruptedException e){
			throw new LuaInteruptedException();
		}*/
		return null;
	}
	public static dan200.computercraft.api.lua.LuaException toLuaException(LuaException ex){
		return new dan200.computercraft.api.lua.LuaException(ex.getMessage(), ex.getLevel());
	}
	public IComputerAccess getComputer() {
		return c;
	}
	public ILuaContext getContext() {
		return context;
	}
	@Override
	public Object[] mapFrom(Object[] in) {
		return in;
	}
}
