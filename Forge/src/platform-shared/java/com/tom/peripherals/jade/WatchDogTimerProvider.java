package com.tom.peripherals.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import com.tom.peripherals.block.entity.WatchDogTimerBlockEntity;
import com.tom.peripherals.util.InfoUtil;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum WatchDogTimerProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
	INSTANCE;

	@Override
	public ResourceLocation getUid() {
		return JadePlugin.WATCH_DOG_TIMER;
	}

	@Override
	public void appendServerData(CompoundTag data, BlockAccessor accessor) {
		WatchDogTimerBlockEntity be = (WatchDogTimerBlockEntity) accessor.getBlockEntity();
		data.putBoolean("en", be.isEnabled());
		data.putInt("limit", be.getTimeLimit());
		data.putInt("timer", be.getTimer());
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
		boolean en = accessor.getServerData().getBoolean("en");
		int limit = accessor.getServerData().getInt("limit");
		int timer = accessor.getServerData().getInt("timer");
		tooltip.add(en ? Component.translatable("label.toms_peripherals.wdt.enabled") : Component.translatable("label.toms_peripherals.wdt.disabled"));

		tooltip.add(Component.translatable("label.toms_peripherals.wdt.timeLimit", InfoUtil.ticksToElapsedTime(limit)));
		tooltip.add(Component.translatable("label.toms_peripherals.wdt.timer", InfoUtil.ticksToElapsedTime(timer)));
	}
}
