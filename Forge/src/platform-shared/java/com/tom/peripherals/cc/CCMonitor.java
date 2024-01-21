package com.tom.peripherals.cc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.BlockHitResult;

import com.tom.peripherals.client.MonitorHandler;
import com.tom.peripherals.network.Network;

public class CCMonitor implements MonitorHandler {
	private BlockHitResult lookingAt;

	public CCMonitor(BlockHitResult lookingAt) {
		this.lookingAt = lookingAt;
	}

	@Override
	public void mouseClick(int btn) {
		if (btn == 1) {
			CompoundTag tag = new CompoundTag();
			tag.putString("action", "clickCC");
			tag.putInt("wx", lookingAt.getBlockPos().getX());
			tag.putInt("wy", lookingAt.getBlockPos().getY());
			tag.putInt("wz", lookingAt.getBlockPos().getZ());
			tag.putDouble("x", lookingAt.getLocation().x);
			tag.putDouble("y", lookingAt.getLocation().y);
			tag.putDouble("z", lookingAt.getLocation().z);
			tag.putByte("d", (byte) lookingAt.getDirection().ordinal());
			Network.sendToContainer(tag);
		}
	}

	@Override
	public void mouseRelease(int btn) {
	}

	@Override
	public void mouseScroll(int dir) {
	}

	@Override
	public void onOffScreen() {
	}

	public void setLookingAt(BlockHitResult lookingAt) {
		this.lookingAt = lookingAt;
	}

	@Override
	public Component infoComponent() {
		return new TranslatableComponent("label.toms_peripherals.keyboard.ccmon");
	}
}