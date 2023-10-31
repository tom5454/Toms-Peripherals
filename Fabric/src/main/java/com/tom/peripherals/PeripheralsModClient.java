package com.tom.peripherals;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;

import com.tom.peripherals.client.MonitorBlockEntityRenderer;
import com.tom.peripherals.util.NativeImageIO;

public class PeripheralsModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistryImpl.register(Content.monitorBE.get(), MonitorBlockEntityRenderer::new);
		PeripheralsMod.imageIO = new NativeImageIO();
	}

}
