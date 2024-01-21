package com.tom.peripherals.client;

import java.util.BitSet;

import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.mojang.blaze3d.vertex.PoseStack;

import com.tom.peripherals.block.entity.MonitorBlockEntity;
import com.tom.peripherals.cc.CCMonitor;
import com.tom.peripherals.network.Network;

import dan200.computercraft.shared.peripheral.monitor.BlockMonitor;

public class KeyboardWidget extends AbstractWidget {
	private final Minecraft mc = Minecraft.getInstance();
	private float terminateTimer = -1.0F;
	private float rebootTimer = -1.0F;
	private float shutdownTimer = -1.0F;
	private final BitSet keysDown = new BitSet(256);
	private MonitorHandler monHandler;

	public KeyboardWidget() {
		super(0, 0, 0, 0, Component.empty());
	}

	@Override
	public void updateNarration(NarrationElementOutput p_169152_) {
	}

	@Override
	public boolean charTyped(char ch, int modifiers) {
		if (ch >= ' ' && ch <= '~' || ch >= 160 && ch <= 255) {
			charType(ch);
		}

		return true;
	}

	@Override
	public boolean keyPressed(int key, int scancode, int modifiers) {
		if (key == 256) {
			return false;
		} else {
			if ((modifiers & 2) != 0) {
				switch (key) {
				case 82 :
					if (this.rebootTimer < 0.0F) {
						this.rebootTimer = 0.0F;
					}

					return true;
				case 83 :
					if (this.shutdownTimer < 0.0F) {
						this.shutdownTimer = 0.0F;
					}

					return true;
				case 84 :
					if (this.terminateTimer < 0.0F) {
						this.terminateTimer = 0.0F;
					}

					return true;
				case 85 :
				default :
					break;
				case 86 :
					String clipboard = mc.keyboardHandler.getClipboard();
					if (clipboard != null) {
						int newLineIndex1 = clipboard.indexOf("\r");
						int newLineIndex2 = clipboard.indexOf("\n");
						if (newLineIndex1 >= 0 && newLineIndex2 >= 0) {
							clipboard = clipboard.substring(0, Math.min(newLineIndex1, newLineIndex2));
						} else if (newLineIndex1 >= 0) {
							clipboard = clipboard.substring(0, newLineIndex1);
						} else if (newLineIndex2 >= 0) {
							clipboard = clipboard.substring(0, newLineIndex2);
						}

						clipboard = SharedConstants.filterText(clipboard);
						if (!clipboard.isEmpty()) {
							if (clipboard.length() > 512) {
								clipboard = clipboard.substring(0, 512);
							}

							pasteContent(clipboard);
						}

						return true;
					}
				}
			}

			if (key >= 0 && this.terminateTimer < 0.0F && this.rebootTimer < 0.0F && this.shutdownTimer < 0.0F) {
				boolean repeat = this.keysDown.get(key);
				this.keysDown.set(key);
				keyDown(key, repeat);
			}

			return true;
		}
	}

	private void pasteContent(String clipboard) {
		CompoundTag tag = new CompoundTag();
		tag.putString("action", "paste");
		tag.putString("v", clipboard);
		Network.sendToContainer(tag);
	}

	private void keyDown(int key, boolean repeat) {
		CompoundTag tag = new CompoundTag();
		tag.putString("action", "keyDown");
		tag.putInt("key", key);
		tag.putBoolean("r", repeat);
		Network.sendToContainer(tag);
	}

	private void keyUp(int key) {
		CompoundTag tag = new CompoundTag();
		tag.putString("action", "keyUp");
		tag.putInt("key", key);
		Network.sendToContainer(tag);
	}

	private void charType(char ch) {
		CompoundTag tag = new CompoundTag();
		tag.putString("action", "char");
		tag.putShort("char", (short) ch);
		Network.sendToContainer(tag);
	}

	private void queueEvent(String string) {
		CompoundTag tag = new CompoundTag();
		tag.putString("action", "event");
		tag.putString("name", string);
		Network.sendToContainer(tag);
	}

	@Override
	public boolean keyReleased(int key, int scancode, int modifiers) {
		if (key >= 0 && this.keysDown.get(key)) {
			this.keysDown.set(key, false);
			keyUp(key);
		}

		switch (key) {
		case 82 :
			this.rebootTimer = -1.0F;
			break;
		case 83 :
			this.shutdownTimer = -1.0F;
			break;
		case 84 :
			this.terminateTimer = -1.0F;
			break;
		case 341 :
		case 345 :
			this.terminateTimer = this.rebootTimer = this.shutdownTimer = -1.0F;
		}

		return true;
	}

	public void update() {
		if (this.terminateTimer >= 0.0F && this.terminateTimer < 0.5F && (this.terminateTimer += 0.05F) > 0.5F) {
			queueEvent("terminate");
		}

		if (this.shutdownTimer >= 0.0F && this.shutdownTimer < 0.5F && (this.shutdownTimer += 0.05F) > 0.5F) {
			queueEvent("shutdown");
		}

		if (this.rebootTimer >= 0.0F && this.rebootTimer < 0.5F && (this.rebootTimer += 0.05F) > 0.5F) {
			queueEvent("reboot");
		}

	}

	@Override
	public void render(PoseStack p_93657_, int p_93658_, int p_93659_, float p_93660_) {
		BlockHitResult lookingAt = (BlockHitResult) mc.player.pick(32, 0f, true);
		BlockState state = mc.level.getBlockState(lookingAt.getBlockPos());
		MonitorHandler mh = monHandler;
		if (mc.level.getBlockEntity(lookingAt.getBlockPos()) instanceof MonitorBlockEntity be) {
			if (monHandler instanceof MonHandler h)h.setLookingAt(lookingAt, be);
			else mh = new MonHandler(lookingAt, be);
		} else if(state.getBlock() instanceof BlockMonitor) {
			if (monHandler instanceof CCMonitor h)h.setLookingAt(lookingAt);
			else mh = new CCMonitor(lookingAt);
		} else {
			mh = null;
		}
		if (mh != monHandler) {
			if(monHandler != null)monHandler.onOffScreen();
			monHandler = mh;
		}
	}

	@Override
	public boolean mouseClicked(double p_93641_, double p_93642_, int button) {
		if (monHandler != null)monHandler.mouseClick(button + 1);
		return true;
	}

	@Override
	public boolean mouseReleased(double p_93684_, double p_93685_, int button) {
		if (monHandler != null)monHandler.mouseRelease(button + 1);
		return true;
	}

	@Override
	public boolean mouseDragged(double p_93645_, double p_93646_, int button, double p_93648_, double p_93649_) {
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (delta != 0.0D) {
			if (monHandler != null)
				monHandler.mouseScroll(delta < 0.0D ? 1 : -1);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onFocusedChanged(boolean focused) {
		if (!focused) {
			release();
		}
	}

	public void release() {
		for (int key = 0; key < this.keysDown.size(); ++key) {
			if (this.keysDown.get(key)) {
				keyUp(key);
			}
		}

		this.keysDown.clear();
		if (this.monHandler != null) {
			this.monHandler.onOffScreen();
			this.monHandler = null;
		}

		this.shutdownTimer = this.terminateTimer = this.rebootTimer = -1.0F;
	}

	@Override
	public boolean isMouseOver(double p_93672_, double p_93673_) {
		return true;
	}

	public Component getInfo() {
		return monHandler != null ? monHandler.infoComponent() : Component.empty();
	}
}
