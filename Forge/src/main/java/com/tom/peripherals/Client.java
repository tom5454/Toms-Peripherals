package com.tom.peripherals;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import com.tom.peripherals.Content;
import com.tom.peripherals.client.MonitorBlockEntityRenderer;

public class Client {

	public static void setup() {
		BlockEntityRenderers.register(Content.monitorBE.get(), MonitorBlockEntityRenderer::new);
	}
}
