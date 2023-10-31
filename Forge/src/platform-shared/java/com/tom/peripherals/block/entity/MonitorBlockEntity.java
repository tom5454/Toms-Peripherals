package com.tom.peripherals.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.tom.peripherals.screen.TextureCache;

public class MonitorBlockEntity extends BlockEntity {
	private BlockPos gpuPos;
	public TextureCache clientCache;
	public int[] screen;
	public int width;

	public MonitorBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
		super(p_155228_, p_155229_, p_155230_);
	}

	public Direction getDirection() {
		return getBlockState().getValue(BlockStateProperties.FACING);
	}

	public BlockPos getOffset(int x, int y, Direction d) {
		int xCoord = worldPosition.getX();
		int yCoord = worldPosition.getY();
		int zCoord = worldPosition.getZ();
		switch(d) {
		case DOWN:
			return new BlockPos(xCoord - x, yCoord, zCoord - y);
		case UP:
			return new BlockPos(xCoord + x, yCoord, zCoord + y);
		case NORTH:
			return new BlockPos(xCoord - x, yCoord + y, zCoord);
		case SOUTH:
			return new BlockPos(xCoord + x, yCoord + y, zCoord);
		case WEST:
			return new BlockPos(xCoord, yCoord + y, zCoord + x);
		case EAST:
			return new BlockPos(xCoord, yCoord + y, zCoord - x);
		default:
			return new BlockPos(xCoord, yCoord, zCoord);
		}
	}

	public MonitorBlockEntity connect(BlockPos gpuPos) {
		this.gpuPos = gpuPos;
		return this;
	}

	public void sync() {
		if (getLevel() != null) {
			BlockState state = getLevel().getBlockState(getBlockPos());
			getLevel().sendBlockUpdated(getBlockPos(), state, state, 3);
		}
	}

	public void onBlockActivated(Direction side, double x, double y, double z, boolean sneak) {
		if (getDirection() == side && gpuPos != null) {
			BlockEntity tile = level.getBlockEntity(gpuPos);
			if (tile != null && tile instanceof GPUBlockEntity gpu) {
				int xP;
				int yP;
				if (side.getAxis() != Axis.Y) {
					double yPos = 1F - y;
					double xPos = (Math.abs(side.getStepZ()) * x + Math.abs(side.getStepX()) * z);
					if ((side.getAxisDirection() == AxisDirection.NEGATIVE) != (side.getAxis() == Axis.X))
						xPos = 1F - xPos;
					xP = Mth.floor(xPos * width);
					yP = Mth.floor(yPos * width);
				} else {
					double yPos = side == Direction.DOWN ? (1F - z) : z;
					xP = Mth.floor(x * width);
					yP = Mth.floor(yPos * width);
				}
				gpu.monitorClick(worldPosition, xP, yP, sneak);
			}
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		tag.putIntArray("s", screen);
		tag.putShort("w", (short) width);
		return tag;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void load(CompoundTag compoundTag) {
		super.load(compoundTag);
		if (compoundTag.contains("s")) {
			screen = compoundTag.getIntArray("s");
			width = compoundTag.getShort("w");
			if (clientCache != null)clientCache.invalidate();
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		if (clientCache != null)clientCache.cleanup();
		clientCache = null;
	}
}
