package com.tom.peripherals.cc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import com.tom.peripherals.api.IComputer;
import com.tom.peripherals.api.ITMPeripheral;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;

public class CCPeripheral {
	private CCPeripheral() {
	}

	public static IPeripheral map(ITMPeripheral p, Level world, BlockPos pos) {
		return new PeripheralWrapper(p, world, pos, world.getGameTime());
	}

	public static class PeripheralWrapper implements IPeripheral, IDynamicPeripheral {
		private ITMPeripheral p;
		public PeripheralWrapper(ITMPeripheral p, Level world, BlockPos pos, long update) {
			this.p = p;
		}
		@Override
		public String getType() {
			return p.getType();
		}

		@Override
		public String[] getMethodNames() {
			return p.getMethodNames();
		}

		@Override
		public MethodResult callMethod(IComputerAccess computer, ILuaContext context, int method, IArguments arguments)
				throws LuaException {
			try {
				IComputer c = new CCComputer(computer, context);
				return MethodResult.of(c.mapTo(p.call(c, p.getMethodNames()[method], arguments.getAll())));
			} catch (com.tom.peripherals.api.LuaException e) {
				throw CCComputer.toLuaException(e);
			}
		}

		@Override
		public boolean equals(IPeripheral other) {
			return this == other;
		}
		@Override
		public void attach(IComputerAccess computer) {
			p.attach(new CCComputer(computer, null));
		}
		@Override
		public void detach(IComputerAccess computer) {
			p.detach(new CCComputer(computer, null));
		}
	}
}
