package com.tom.peripherals;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;

public class ForgeConfig {
	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		PeripheralsMod.LOGGER.info("Loaded Tom's Peripherals config file {}", configEvent.getConfig().getFileName());
		Config.load(configEvent.getConfig());
	}

	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		PeripheralsMod.LOGGER.info("Tom's Peripherals config just got changed on the file system!");
		Config.load(configEvent.getConfig());
	}
}
