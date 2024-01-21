package com.tom.peripherals.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.tom.peripherals.block.entity.MonitorBlockEntity;
import com.tom.peripherals.platform.Platform;

import dan200.computercraft.shared.peripheral.monitor.BlockMonitor;

public class LaserPointerItem extends Item {

	public LaserPointerItem() {
		super(new Item.Properties().stacksTo(1).tab(Platform.MOD_TAB));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		if (!world.isClientSide) {
			BlockHitResult lookingAt = (BlockHitResult) player.pick(32, 0f, true);
			BlockState state = world.getBlockState(lookingAt.getBlockPos());
			if (state.getBlock() instanceof BlockMonitor || world.getBlockEntity(lookingAt.getBlockPos()) instanceof MonitorBlockEntity) {
				state.use(world, player, InteractionHand.MAIN_HAND, lookingAt);
			}
		}
		return InteractionResultHolder.consume(player.getItemInHand(hand));
	}
}
