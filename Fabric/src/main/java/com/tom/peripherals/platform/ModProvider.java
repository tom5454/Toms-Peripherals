package com.tom.peripherals.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.tom.peripherals.cc.CCPeripheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;

public class ModProvider implements IPeripheralProvider {
	public static final ModProvider INSTANCE = new ModProvider();
	private ModProvider() {
	}

	@Override
	public IPeripheral getPeripheral(Level world, BlockPos pos, Direction side) {
		BlockEntity be = world.getBlockEntity(pos);
		return be instanceof IPeripheralBlockEntity p && p.getPeripheral() != null ? CCPeripheral.map(p.getPeripheral(), world, pos) : null;
	}
}
