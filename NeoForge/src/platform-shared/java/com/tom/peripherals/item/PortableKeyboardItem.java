package com.tom.peripherals.item;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import com.tom.peripherals.Content;
import com.tom.peripherals.block.entity.KeyboardBlockEntity;
import com.tom.peripherals.client.ClientUtil;

public class PortableKeyboardItem extends Item {

	public PortableKeyboardItem() {
		super(new Item.Properties().stacksTo(1));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext p_339606_, List<Component> tooltip, TooltipFlag p_49819_) {
		ClientUtil.tooltip("portable_keyboard.info", tooltip);
		BlockPos pos = stack.get(Content.boundPosComponent.get());
		if(pos != null) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			tooltip.add(Component.translatable("tooltip.toms_peripherals.portable_keyboard.bound", x, y, z));
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		return new InteractionResultHolder<>(openKeyboard(worldIn, playerIn.getItemInHand(handIn), playerIn, handIn), playerIn.getItemInHand(handIn));
	}

	public static InteractionResult openKeyboard(Level worldIn, ItemStack stack, Player playerIn, InteractionHand handIn) {
		BlockPos pos = stack.get(Content.boundPosComponent.get());
		if(pos != null) {
			if(!worldIn.isClientSide) {
				if (playerIn.blockPosition().closerThan(pos, 64)) {
					BlockHitResult lookingAt = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, true);
					BlockState state = worldIn.getBlockState(lookingAt.getBlockPos());
					if (state.is(Content.keyboard_dongle.get())) {
						if (worldIn.getBlockEntity(pos) instanceof KeyboardBlockEntity term) {
							playerIn.openMenu(term);
							stack.set(Content.inUseComponent.get(), true);
						}
						return InteractionResult.CONSUME;
					} else {
						playerIn.displayClientMessage(Component.translatable("chat.toms_peripherals.dongle_not_found"), true);
					}
				} else {
					playerIn.displayClientMessage(Component.translatable("chat.toms_peripherals.dongle_out_of_range"), true);
				}
			} else {
				return InteractionResult.CONSUME;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult useOn(UseOnContext c) {
		if(c.isSecondaryUseActive()) {
			if(!c.getLevel().isClientSide) {
				BlockPos pos = c.getClickedPos();
				BlockState state = c.getLevel().getBlockState(pos);
				if(state.is(Content.keyboard_dongle.get())) {
					ItemStack stack = c.getItemInHand();
					stack.set(Content.boundPosComponent.get(), pos.immutable());
					if(c.getPlayer() != null)
						c.getPlayer().displayClientMessage(Component.translatable("chat.toms_peripherals.portable_keyboard.bound_success"), true);
					return InteractionResult.SUCCESS;
				}
			} else
				return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level p_41405_, Entity player, int p_41407_, boolean p_41408_) {
		Boolean in = stack.get(Content.inUseComponent.get());
		if (in != null && in) {
			if(!(player instanceof Player pl && pl.hasContainerOpen()))
				stack.set(Content.inUseComponent.get(), false);
		}
	}
}