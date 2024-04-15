package com.tom.peripherals.block;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.peripherals.Content;
import com.tom.peripherals.client.ClientUtil;

public class GPUBlock extends Block implements EntityBlock {

	public GPUBlock() {
		super(Block.Properties.of().mapColor(DyeColor.WHITE).sound(SoundType.METAL).strength(5));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		return Content.gpuBE.get().create(p_153215_, p_153216_);
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag p_49819_) {
		ClientUtil.tooltip("gpu", tooltip);
	}
}
