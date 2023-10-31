package com.tom.peripherals;

import org.slf4j.Logger;

import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.mojang.logging.LogUtils;

import com.tom.peripherals.platform.ModProvider;
import com.tom.peripherals.platform.Platform;
import com.tom.peripherals.screen.font.FontManager;
import com.tom.peripherals.top.TheOneProbeHandler;
import com.tom.peripherals.util.AWTImageIO;
import com.tom.peripherals.util.ImageIO;
import com.tom.peripherals.util.NativeImageIO;

import dan200.computercraft.api.ComputerCraftAPI;

@Mod(PeripheralsMod.ID)
public class PeripheralsMod {
	public static final String ID = "toms_peripherals";
	public static ImageIO imageIO = new AWTImageIO();
	public static final Logger LOGGER = LogUtils.getLogger();

	public PeripheralsMod() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
		FMLJavaModLoadingContext.get().getModEventBus().register(ForgeConfig.class);
		Content.init();
		Platform.register();
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		Client.setup();
		imageIO = new NativeImageIO();
	}

	private void setup(FMLCommonSetupEvent e) {
		ComputerCraftAPI.registerPeripheralProvider(ModProvider.INSTANCE);
		FontManager.init();
	}

	public void enqueueIMC(InterModEnqueueEvent e) {
		if(ModList.get().isLoaded("theoneprobe"))
			InterModComms.sendTo("theoneprobe", "getTheOneProbe", () -> TheOneProbeHandler.create());
	}
}
