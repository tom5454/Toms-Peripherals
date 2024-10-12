package com.tom.peripherals.jade;

import java.util.Locale;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import com.tom.peripherals.util.InfoUtil;

import dan200.computercraft.shared.peripheral.modem.wired.WiredModemFullBlockEntity;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum FullModemProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
	INSTANCE;

	@Override
	public ResourceLocation getUid() {
		return JadePlugin.FULL_MODEM_INFO;
	}

	@Override
	public void appendServerData(CompoundTag data, BlockAccessor accessor) {
		WiredModemFullBlockEntity be = (WiredModemFullBlockEntity) accessor.getBlockEntity();
		for (Direction d : Direction.values()) {
			var modem = be.getPeripheral(d);
			if (modem != null) {
				Object[] nameArray = modem.getNameLocal();
				if (nameArray != null && nameArray.length == 1 && nameArray[0] instanceof String name) {
					data.putString("p" + d.name(), name);
				}
			}
		}
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		tooltip.add(Component.translatable("label.toms_peripherals.full_modem.list"));
		boolean hasP = false;
		var player = Minecraft.getInstance().player;
		for (Direction d : Direction.values()) {
			if (accessor.getServerData().contains("p" + d.name())) {
				String name = accessor.getServerData().getString("p" + d.name());
				var text = Component.translatable("label.toms_peripherals.side." + d.name().toLowerCase(Locale.ROOT)).append(": ").append(Component.literal(name).withStyle(ChatFormatting.YELLOW));
				if(d.getAxis() != Axis.Y)
					text.append(" (" + InfoUtil.getDirectionArrow(d.toYRot() - player.yRotO) + ")");
				tooltip.add(text);
				hasP = true;
			}
		}
		if (!hasP)tooltip.add(Component.translatable("label.toms_peripherals.full_modem.none"));
	}
}
