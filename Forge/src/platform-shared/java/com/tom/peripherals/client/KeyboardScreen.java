package com.tom.peripherals.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import com.mojang.blaze3d.vertex.PoseStack;

import com.tom.peripherals.menu.KeyboardMenu;

public class KeyboardScreen extends Screen implements MenuAccess<KeyboardMenu> {
	private final KeyboardMenu menu;
	private KeyboardWidget keyHandler;

	public KeyboardScreen(KeyboardMenu menu, Inventory inv, Component p_96550_) {
		super(p_96550_);
		this.menu = menu;
	}

	@Override
	public KeyboardMenu getMenu() {
		return menu;
	}

	@Override
	protected void init() {
		this.passEvents = true;
		this.minecraft.mouseHandler.grabMouse();
		this.minecraft.screen = this;
		KeyMapping.releaseAll();
		super.init();
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		this.keyHandler = this.addRenderableWidget(new KeyboardWidget());
		this.setFocused(this.keyHandler);
	}

	@Override
	public final void removed() {
		super.removed();
		this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
	}

	@Override
	public final void tick() {
		super.tick();
		this.keyHandler.update();
	}

	@Override
	public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
		return super.mouseScrolled(pMouseX, pMouseY, pDelta);
	}

	@Override
	public void onClose() {
		this.keyHandler.release();
		this.minecraft.player.closeContainer();
		super.onClose();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public final boolean keyPressed(int key, int scancode, int modifiers) {
		return key == 258 && this.getFocused() != null && this.getFocused() == this.keyHandler
				? this.getFocused().keyPressed(key, scancode, modifiers)
						: super.keyPressed(key, scancode, modifiers);
	}

	@Override
	public void render(PoseStack transform, int mouseX, int mouseY, float partialTicks) {
		super.render(transform, mouseX, mouseY, partialTicks);
		Font font = this.minecraft.font;
		List<FormattedCharSequence> lines = new ArrayList<>();
		int w = (int) (this.width * 0.8D);
		lines.addAll(font.split(new TranslatableComponent("label.toms_peripherals.keyboard.open"), w));
		lines.addAll(font.split(keyHandler.getInfo(), w));
		float y = 10.0F;

		for (Iterator<FormattedCharSequence> var8 = lines.iterator(); var8.hasNext(); y += 9.0F) {
			FormattedCharSequence line = var8.next();
			font.drawShadow(transform, line, this.width / 2 - this.minecraft.font.width(line) / 2, y,
					16777215);
		}
	}
}
