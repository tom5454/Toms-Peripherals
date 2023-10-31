package com.tom.peripherals.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import com.tom.peripherals.util.ITMPeripheral;

public abstract class AbstractPeripheralBlockEntity extends BlockEntity {
	private LazyOptional<ITMPeripheral> cap = LazyOptional.of(this::getPeripheral);

	public AbstractPeripheralBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
		super(p_155228_, p_155229_, p_155230_);
	}

	public abstract ITMPeripheral getPeripheral();

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		cap.invalidate();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == PeripheralCapability.PERIPHERAL)
			return this.cap.cast();
		return super.getCapability(cap, side);
	}
}
