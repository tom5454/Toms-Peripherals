package com.tom.peripherals.jade;

import net.minecraft.resources.ResourceLocation;

import com.tom.peripherals.PeripheralsMod;
import com.tom.peripherals.block.WatchDogTimerBlock;
import com.tom.peripherals.block.entity.WatchDogTimerBlockEntity;

import dan200.computercraft.shared.peripheral.modem.wired.CableBlock;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlockEntity;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemFullBlock;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemFullBlockEntity;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {
	public static final ResourceLocation WATCH_DOG_TIMER = ResourceLocation.tryBuild(PeripheralsMod.ID, "wdt_info");
	public static final ResourceLocation MODEM_INFO = ResourceLocation.tryBuild(PeripheralsMod.ID, "modem_info");
	public static final ResourceLocation FULL_MODEM_INFO = ResourceLocation.tryBuild(PeripheralsMod.ID, "full_modem_info");

	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerBlockDataProvider(WatchDogTimerProvider.INSTANCE, WatchDogTimerBlockEntity.class);
		registration.registerBlockDataProvider(ModemProvider.INSTANCE, CableBlockEntity.class);
		registration.registerBlockDataProvider(FullModemProvider.INSTANCE, WiredModemFullBlockEntity.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(WatchDogTimerProvider.INSTANCE, WatchDogTimerBlock.class);
		registration.registerBlockComponent(ModemProvider.INSTANCE, CableBlock.class);
		registration.registerBlockComponent(FullModemProvider.INSTANCE, WiredModemFullBlock.class);
	}
}
