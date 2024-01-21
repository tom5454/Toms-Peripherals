package com.tom.peripherals.client;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.BlockHitResult;

import com.tom.peripherals.block.entity.MonitorBlockEntity;
import com.tom.peripherals.math.Vec2i;
import com.tom.peripherals.network.Network;

public class MonHandler implements MonitorHandler {
	private BlockHitResult lookingAt;
	private Vec2i pos;
	private Vec2i lastPos;
	private BlockPos lastBPos;
	private int lastBtn;

	public MonHandler(BlockHitResult lookingAt, MonitorBlockEntity blockEntity) {
		setLookingAt(lookingAt, blockEntity);
		lastPos = pos;
		lastBPos = lookingAt.getBlockPos();
	}

	@Override
	public void mouseClick(int btn) {
		sendEvent("mouse_click", lookingAt.getBlockPos(), pos, btn);
		lastBtn = btn;

	}

	@Override
	public void mouseRelease(int btn) {
		if (this.lastBtn == btn) {
			sendEvent("mouse_up", lookingAt.getBlockPos(), pos, btn);
			this.lastBtn = -1;
		}
	}

	@Override
	public void mouseScroll(int dir) {
		sendEvent("mouse_scroll", lookingAt.getBlockPos(), pos, dir);
	}

	public void setLookingAt(BlockHitResult lookingAt, MonitorBlockEntity blockEntity) {
		Vec2i last = lastPos;
		BlockPos lastB = lastBPos;
		lastPos = pos;
		if (this.lookingAt != null)
			lastBPos = this.lookingAt.getBlockPos();

		this.lookingAt = lookingAt;
		BlockPos pos = lookingAt.getBlockPos();
		this.pos = blockEntity.getMonitorPixel(lookingAt.getDirection(), lookingAt.getLocation().x - pos.getX(), lookingAt.getLocation().y - pos.getY(), lookingAt.getLocation().z - pos.getZ());
		if (last != null) {
			if (this.pos.x != last.x || this.pos.y != last.y || !lastB.equals(pos)) {
				if (lastBtn > 0) {
					sendEvent("mouse_drag", pos, this.pos, lastBtn);
				} else {
					sendEvent("mouse_move", pos, this.pos);
				}
			}
		} else {
			sendEvent("mouse_enter", lookingAt.getBlockPos(), this.pos);
		}
	}

	private CompoundTag createTag(String event, BlockPos pos, Vec2i pixel) {
		CompoundTag tag = new CompoundTag();
		tag.putString("action", "monEvent");
		tag.putString("event", event);
		tag.putInt("wx", pos.getX());
		tag.putInt("wy", pos.getY());
		tag.putInt("wz", pos.getZ());
		tag.putInt("x", pixel.x);
		tag.putInt("y", pixel.y);
		return tag;
	}

	private void sendEvent(String event, BlockPos pos, Vec2i pixel) {
		Network.sendToContainer(createTag(event, pos, pixel));
	}

	private void sendEvent(String event, BlockPos pos, Vec2i pixel, int param) {
		CompoundTag tag = createTag(event, pos, pixel);
		tag.putInt("param", param);
		Network.sendToContainer(tag);
	}

	@Override
	public void onOffScreen() {
		if (this.lastBtn > 0) {
			mouseRelease(this.lastBtn);
			this.lastBtn = -1;
		}
		sendEvent("mouse_exit", lookingAt.getBlockPos(), this.pos);
	}

	@Override
	public Component infoComponent() {
		return new TranslatableComponent("label.toms_peripherals.keyboard.monitor");
	}
}
