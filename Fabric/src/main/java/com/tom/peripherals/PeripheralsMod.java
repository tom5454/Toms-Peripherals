package com.tom.peripherals;

import org.slf4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;

import com.mojang.logging.LogUtils;

import com.tom.peripherals.network.Network;
import com.tom.peripherals.platform.AbstractPeripheralBlockEntity;
import com.tom.peripherals.platform.Platform;

import dan200.computercraft.api.peripheral.PeripheralLookup;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;

public class PeripheralsMod implements ModInitializer {
	public static final String ID = "toms_peripherals";
	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitialize() {
		Content.init();
		Platform.register();

		NeoForgeConfigRegistry.INSTANCE.register(ID, ModConfig.Type.COMMON, Config.commonSpec);
		NeoForgeConfigRegistry.INSTANCE.register(ID, ModConfig.Type.SERVER, Config.serverSpec);

		NeoForgeModConfigEvents.loading(ID).register(c -> {
			LOGGER.info("Loaded Tom's Peripherals config file {}", c.getFileName());
			Config.load(c);
		});
		NeoForgeModConfigEvents.reloading(ID).register(c -> {
			LOGGER.info("Tom's Peripherals config just got changed on the file system!");
			Config.load(c);
		});

		PeripheralLookup.get().registerForBlockEntities((b, side) -> {
			if (b instanceof AbstractPeripheralBlockEntity be)
				return be.getCCPeripheral();
			return null;
		}, Content.gpuBE.get(), Content.redstonePortBE.get(), Content.wdtBE.get(), Content.keyboardBE.get());
		Network.initCommon();
	}

}
