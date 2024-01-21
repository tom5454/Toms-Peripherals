package com.tom.peripherals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import com.tom.peripherals.client.KeyboardScreen;
import com.tom.peripherals.client.MonitorBlockEntityRenderer;
import com.tom.peripherals.network.Network;
import com.tom.peripherals.util.IDataReceiver;
import com.tom.peripherals.util.ImageIO;
import com.tom.peripherals.util.NativeImageIO;

public class PeripheralsModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistryImpl.register(Content.monitorBE.get(), MonitorBlockEntityRenderer::new);
		MenuScreens.register(Content.keyboardMenu.get(), KeyboardScreen::new);
		ImageIO.handler = new NativeImageIO();

		ClientPlayNetworking.registerGlobalReceiver(Network.DATA_S2C, (mc, h, buf, rp) -> {
			CompoundTag tag = buf.readAnySizeNbt();
			mc.submit(() -> {
				if(mc.screen instanceof IDataReceiver) {
					((IDataReceiver)mc.screen).receive(tag);
				}
			});
		});

		ItemProperties.register(Content.portableKeyboard.get(), new ResourceLocation("toms_peripherals:portable_keyboard"), (stack, level, player, p_174643_) -> {
			return stack.hasTag() && stack.getTag().getBoolean("inUse") ? 1F : 0F;
		});

		BlockRenderLayerMap.INSTANCE.putBlock(Content.keyboard.get(), RenderType.cutout());
	}

}
