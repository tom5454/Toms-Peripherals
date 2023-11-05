package com.tom.peripherals.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import com.tom.peripherals.api.ITMPeripheral;
import com.tom.peripherals.cc.CCPeripheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;

public abstract class AbstractPeripheralBlockEntity extends BlockEntity {
	private LazyOptional<IPeripheral> cap = LazyOptional.of(() -> CCPeripheral.map(getPeripheral(), level, worldPosition));

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
		if (cap == Capabilities.CAPABILITY_PERIPHERAL)
			return this.cap.cast();
		return super.getCapability(cap, side);
	}
}
