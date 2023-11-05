package com.tom.peripherals.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.peripherals.api.ITMPeripheral;
import com.tom.peripherals.cc.CCPeripheral;

import dan200.computercraft.api.peripheral.IPeripheral;

public abstract class AbstractPeripheralBlockEntity extends BlockEntity {
	private IPeripheral peripheral;

	public AbstractPeripheralBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	public abstract ITMPeripheral getPeripheral();

	public IPeripheral getCCPeripheral() {
		if (peripheral == null)peripheral = CCPeripheral.map(getPeripheral(), level, worldPosition);
		return peripheral;
	}
}
