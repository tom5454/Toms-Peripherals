package com.tom.peripherals.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import dan200.computercraft.shared.peripheral.modem.wired.CableBlockEntity;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemPeripheral;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum ModemProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
	INSTANCE;

	@Override
	public ResourceLocation getUid() {
		return JadePlugin.MODEM_INFO;
	}

	@Override
	public void appendServerData(CompoundTag data, BlockAccessor accessor) {
		CableBlockEntity be = (CableBlockEntity) accessor.getBlockEntity();
		if (be.getPeripheral(null) instanceof WiredModemPeripheral modem) {
			Object[] nameArray = modem.getNameLocal();
			if (nameArray != null && nameArray.length == 1 && nameArray[0] instanceof String name) {
				data.putString("pn", name);
			}
		}
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		if (accessor.getServerData().contains("pn")) {
			String name = accessor.getServerData().getString("pn");
			tooltip.add(Component.translatable("label.toms_peripherals.modem.name").append(Component.literal(" " + name).withStyle(ChatFormatting.YELLOW)));
		}
	}
}
