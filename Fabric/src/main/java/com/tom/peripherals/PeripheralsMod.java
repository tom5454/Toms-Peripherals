package com.tom.peripherals;

import org.slf4j.Logger;

import net.fabricmc.api.ModInitializer;

import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.config.ModConfig;

import com.mojang.logging.LogUtils;

import com.tom.peripherals.platform.ModProvider;
import com.tom.peripherals.platform.Platform;

import dan200.computercraft.api.ComputerCraftAPI;

public class PeripheralsMod implements ModInitializer {
	public static final String ID = "toms_peripherals";
	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitialize() {
		Content.init();
		Platform.register();
		ComputerCraftAPI.registerPeripheralProvider(ModProvider.INSTANCE);

		ModLoadingContext.registerConfig(ID, ModConfig.Type.COMMON, Config.commonSpec);
		ModLoadingContext.registerConfig(ID, ModConfig.Type.SERVER, Config.serverSpec);

		ModConfigEvent.LOADING.register(c -> {
			if (c.getModId().equals(ID)) {
				LOGGER.info("Loaded Tom's Peripherals config file {}", c.getFileName());
				Config.load(c);
			}
		});
		ModConfigEvent.RELOADING.register(c -> {
			if (c.getModId().equals(ID)) {
				LOGGER.info("Tom's Peripherals config just got changed on the file system!");
				Config.load(c);
			}
		});
	}

}
