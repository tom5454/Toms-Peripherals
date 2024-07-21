package com.tom.peripherals.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

import com.tom.peripherals.Content;
import com.tom.peripherals.block.entity.MonitorBlockEntity;
import com.tom.peripherals.client.ClientUtil;

public class MonitorBlock extends Block implements EntityBlock {

	public MonitorBlock() {
		super(Block.Properties.of().mapColor(DyeColor.WHITE).sound(SoundType.GLASS).lightLevel(b -> 12).strength(5).noOcclusion());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		return Content.monitorBE.get().create(p_153215_, p_153216_);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> b) {
		b.add(BlockStateProperties.FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(BlockStateProperties.FACING, ctx.getNearestLookingDirection().getOpposite());
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos,
			Player player, BlockHitResult hit) {
		if (!world.isClientSide && world.getBlockEntity(pos) instanceof MonitorBlockEntity te) {
			te.onBlockActivated(hit.getDirection(), hit.getLocation().x - pos.getX(), hit.getLocation().y - pos.getY(), hit.getLocation().z - pos.getZ(), player.isSecondaryUseActive());
		}
		return state.getValue(BlockStateProperties.FACING) == hit.getDirection() ? InteractionResult.SUCCESS : InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack p_49816_, TooltipContext p_339606_, List<Component> tooltip, TooltipFlag p_49819_) {
		ClientUtil.tooltip("monitor", tooltip);
	}
}
