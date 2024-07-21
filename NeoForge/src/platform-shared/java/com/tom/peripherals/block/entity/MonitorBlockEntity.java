package com.tom.peripherals.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.tom.peripherals.Content;
import com.tom.peripherals.math.Vec2i;
import com.tom.peripherals.screen.TextureCache;

public class MonitorBlockEntity extends BlockEntity {
	private BlockPos gpuPos;
	public TextureCache clientCache;
	public int[] screen = new int[0];
	public int width;

	public MonitorBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
		super(Content.monitorBE.get(), p_155229_, p_155230_);
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
			return new BlockPos(xCoord + x, yCoord, zCoord + y);
		case UP:
			return new BlockPos(xCoord + x, yCoord, zCoord - y);
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
				Vec2i c = getMonitorPixel(side, x, y, z);
				gpu.monitorClick(this, c.x, c.y, sneak);
			}
		}
	}

	public Vec2i getMonitorPixel(Direction side, double x, double y, double z) {
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
		return new Vec2i(xP, yP);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider p_323910_) {
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
	protected void loadAdditional(CompoundTag compoundTag, Provider p_338445_) {
		super.loadAdditional(compoundTag, p_338445_);
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

	public void event(String ev, int x, int y, Integer param) {
		if (gpuPos == null)return;
		BlockEntity tile = level.getBlockEntity(gpuPos);
		if (tile != null && tile instanceof GPUBlockEntity gpu) {
			gpu.monitorEvent(this, ev, x, y, param);
		}
	}
}
