package com.tom.peripherals.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.peripherals.Content;
import com.tom.peripherals.block.entity.RedstonePortBlockEntity;
import com.tom.peripherals.util.TickerUtil;

import dan200.computercraft.shared.common.IBundledRedstoneBlock;

public class RedstonePortBlock extends Block implements EntityBlock, IForgeBlock, IBundledRedstoneBlock {

	public RedstonePortBlock() {
		super(Block.Properties.of().mapColor(DyeColor.RED).sound(SoundType.METAL).strength(5).isRedstoneConductor((a, b, c) -> false));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		return Content.redstonePortBE.get().create(p_153215_, p_153216_);
	}

	@Override
	public final void neighborChanged(BlockState state, Level world, BlockPos pos, Block neighbourBlock, BlockPos neighbourPos, boolean isMoving) {
		var be = world.getBlockEntity(pos);
		if (be instanceof RedstonePortBlockEntity te) te.neighborChanged(neighbourPos);
	}

	@Override
	public final void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbour) {
		var be = world.getBlockEntity(pos);
		if (be instanceof RedstonePortBlockEntity te) te.neighborChanged(neighbour);
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return true;
	}

	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction incomingSide) {
		var entity = world.getBlockEntity(pos);
		if (!(entity instanceof RedstonePortBlockEntity te)) return 0;
		return te.getExternalRedstoneOutput(incomingSide.getOpposite());
	}

	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction incomingSide) {
		return getDirectSignal(state, world, pos, incomingSide);
	}

	@Override
	public int getBundledRedstoneOutput(Level world, BlockPos pos, Direction side) {
		var entity = world.getBlockEntity(pos);
		if (!(entity instanceof RedstonePortBlockEntity te)) return 0;
		return te.getExternalBundledRedstoneOutput(side);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state,
			BlockEntityType<T> type) {
		return TickerUtil.createTicker(world, false, true);
	}
}
