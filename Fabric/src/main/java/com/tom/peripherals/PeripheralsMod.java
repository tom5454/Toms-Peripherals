package com.tom.peripherals;

import org.slf4j.Logger;

import net.fabricmc.api.ModInitializer;

import net.minecraftforge.fml.config.ModConfig;

import com.mojang.logging.LogUtils;

import com.tom.peripherals.platform.AbstractPeripheralBlockEntity;
import com.tom.peripherals.platform.Platform;
import com.tom.peripherals.util.AWTImageIO;
import com.tom.peripherals.util.ImageIO;

import dan200.computercraft.api.peripheral.PeripheralLookup;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;

public class PeripheralsMod implements ModInitializer {
	public static final String ID = "toms_peripherals";
	public static ImageIO imageIO = new AWTImageIO();
	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitialize() {
		Content.init();
		Platform.register();

		ForgeConfigRegistry.INSTANCE.register(ID, ModConfig.Type.COMMON, Config.commonSpec);
		ForgeConfigRegistry.INSTANCE.register(ID, ModConfig.Type.SERVER, Config.serverSpec);

		ModConfigEvents.loading(ID).register(c -> {
			LOGGER.info("Loaded Tom's Peripherals config file {}", c.getFileName());
			Config.load(c);
		});
		ModConfigEvents.reloading(ID).register(c -> {
			LOGGER.info("Tom's Peripherals config just got changed on the file system!");
			Config.load(c);
		});

		PeripheralLookup.get().registerForBlockEntities((b, side) -> {
			if (b instanceof AbstractPeripheralBlockEntity be)
				return be.getCCPeripheral();
			return null;
		}, Content.gpuBE.get(), Content.redstonePortBE.get(), Content.wdtBE.get());
	}

}
