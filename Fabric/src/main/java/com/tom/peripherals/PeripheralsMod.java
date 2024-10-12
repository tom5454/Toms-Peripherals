package com.tom.peripherals;

import org.slf4j.Logger;

import net.fabricmc.api.ModInitializer;

import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvents;
import net.minecraftforge.fml.config.ModConfig;

import com.mojang.logging.LogUtils;

import com.tom.peripherals.gpu.font.FontManager;
import com.tom.peripherals.network.Network;
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

		ModConfigEvents.loading(ID).register(c -> {
			LOGGER.info("Loaded Tom's Peripherals config file {}", c.getFileName());
			Config.load(c);
		});
		ModConfigEvents.reloading(ID).register(c -> {
			LOGGER.info("Tom's Peripherals config just got changed on the file system!");
			Config.load(c);
		});
		Network.initCommon();
		FontManager.init();
	}

}
