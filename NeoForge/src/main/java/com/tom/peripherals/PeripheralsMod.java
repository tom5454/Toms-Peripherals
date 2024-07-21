package com.tom.peripherals;

import org.slf4j.Logger;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import com.mojang.logging.LogUtils;

import com.tom.peripherals.gpu.font.FontManager;
import com.tom.peripherals.network.Network;
import com.tom.peripherals.platform.Platform;
import com.tom.peripherals.top.TheOneProbeHandler;
import com.tom.peripherals.util.ImageIO;
import com.tom.peripherals.util.NativeImageIO;

import dan200.computercraft.api.peripheral.PeripheralCapability;

@Mod(PeripheralsMod.ID)
public class PeripheralsMod {
	public static final String ID = "toms_peripherals";
	public static final Logger LOGGER = LogUtils.getLogger();

	public PeripheralsMod(ModContainer mc, IEventBus bus) {
		bus.addListener(this::setup);
		bus.addListener(this::doClientStuff);
		bus.addListener(this::enqueueIMC);
		if (FMLEnvironment.dist == Dist.CLIENT)Client.preInit(mc, bus);
		mc.registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
		mc.registerConfig(ModConfig.Type.SERVER, Config.serverSpec);
		bus.register(ForgeConfig.class);
		bus.register(Network.class);
		bus.addListener(this::registerCapabilities);
		Content.init();
		Platform.register(bus);
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		Client.setup();
		ImageIO.handler = new NativeImageIO();
	}

	private void setup(FMLCommonSetupEvent e) {
		FontManager.init();
	}

	public void enqueueIMC(InterModEnqueueEvent e) {
		if(ModList.get().isLoaded("theoneprobe"))
			InterModComms.sendTo("theoneprobe", "getTheOneProbe", () -> TheOneProbeHandler.create());
	}

	private void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(PeripheralCapability.get(), Content.gpuBE.get(), (be, side) -> be.getCCPeripheral());
		event.registerBlockEntity(PeripheralCapability.get(), Content.wdtBE.get(), (be, side) -> be.getCCPeripheral());
		event.registerBlockEntity(PeripheralCapability.get(), Content.redstonePortBE.get(), (be, side) -> be.getCCPeripheral());
		event.registerBlockEntity(PeripheralCapability.get(), Content.keyboardBE.get(), (be, side) -> be.getCCPeripheral());
	}
}
