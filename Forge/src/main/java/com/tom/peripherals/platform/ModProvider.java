package com.tom.peripherals.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import net.minecraftforge.common.util.LazyOptional;

import com.tom.peripherals.cc.CCComputer;
import com.tom.peripherals.util.ITMPeripheral;
import com.tom.peripherals.util.ITMPeripheral.IComputer;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class ModProvider implements IPeripheralProvider {
	public static final ModProvider INSTANCE = new ModProvider();
	private ModProvider() {
	}

	@Override
	public LazyOptional<IPeripheral> getPeripheral(Level world, BlockPos pos, Direction side) {
		BlockEntity be = world.getBlockEntity(pos);
		return be != null ? be.getCapability(PeripheralCapability.PERIPHERAL).lazyMap(p -> new PeripheralWrapper(p, world, pos, world.getGameTime())) : LazyOptional.empty();
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
			} catch (ITMPeripheral.LuaException e) {
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
