package com.tom.peripherals;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

import com.tom.peripherals.client.KeyboardScreen;
import com.tom.peripherals.client.MonitorBlockEntityRenderer;

public class Client {

	public static void setup() {
		BlockEntityRenderers.register(Content.monitorBE.get(), MonitorBlockEntityRenderer::new);
		MenuScreens.register(Content.keyboardMenu.get(), KeyboardScreen::new);

		ItemProperties.register(Content.portableKeyboard.get(), new ResourceLocation("toms_peripherals:portable_keyboard"), (stack, level, player, p_174643_) -> {
			return stack.hasTag() && stack.getTag().getBoolean("inUse") ? 1F : 0F;
		});
	}
}
