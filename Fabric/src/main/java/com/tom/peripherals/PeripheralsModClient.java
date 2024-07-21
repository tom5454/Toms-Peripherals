package com.tom.peripherals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

import com.tom.peripherals.client.KeyboardScreen;
import com.tom.peripherals.client.MonitorBlockEntityRenderer;
import com.tom.peripherals.network.DataPacket;
import com.tom.peripherals.util.IDataReceiver;
import com.tom.peripherals.util.ImageIO;
import com.tom.peripherals.util.NativeImageIO;

public class PeripheralsModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistryImpl.register(Content.monitorBE.get(), MonitorBlockEntityRenderer::new);
		MenuScreens.register(Content.keyboardMenu.get(), KeyboardScreen::new);
		ImageIO.handler = new NativeImageIO();

		ClientPlayNetworking.registerGlobalReceiver(DataPacket.ID, (p, c) -> {
			if(Minecraft.getInstance().screen instanceof IDataReceiver d) {
				d.receive(p.tag());
			}
		});

		ItemProperties.register(Content.portableKeyboard.get(), ResourceLocation.tryBuild(PeripheralsMod.ID, "portable_keyboard"), (stack, level, player, p_174643_) -> {
			Boolean in = stack.get(Content.inUseComponent.get());
			return in != null && in ? 1F : 0F;
		});

		BlockRenderLayerMap.INSTANCE.putBlock(Content.keyboard.get(), RenderType.cutout());
	}

}
