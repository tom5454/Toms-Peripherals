package com.tom.peripherals.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.tom.peripherals.Content;
import com.tom.peripherals.client.ClientUtil;
import com.tom.peripherals.util.TickerUtil;

import dan200.computercraft.shared.peripheral.modem.ModemShapes;

public class WatchDogTimerBlock extends Block implements EntityBlock {

	public WatchDogTimerBlock() {
		super(Block.Properties.of().mapColor(DyeColor.GRAY).sound(SoundType.STONE).dynamicShape().strength(3).noOcclusion());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		return Content.wdtBE.get().create(p_153215_, p_153216_);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> b) {
		b.add(BlockStateProperties.FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(BlockStateProperties.FACING, ctx.getClickedFace().getOpposite());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_,
			CollisionContext p_60558_) {
		return ModemShapes.getBounds(state.getValue(BlockStateProperties.FACING));
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state,
			BlockEntityType<T> type) {
		return TickerUtil.createTicker(world, false, true);
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag p_49819_) {
		ClientUtil.tooltip("wdt", tooltip);
	}
}
