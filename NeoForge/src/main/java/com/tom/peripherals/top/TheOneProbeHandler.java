package com.tom.peripherals.top;

import java.util.Locale;
import java.util.function.Function;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.peripherals.PeripheralsMod;
import com.tom.peripherals.block.entity.WatchDogTimerBlockEntity;
import com.tom.peripherals.util.InfoUtil;

import dan200.computercraft.shared.peripheral.modem.wired.CableBlockEntity;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemFullBlockEntity;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemPeripheral;

import mcjty.theoneprobe.api.CompoundText;
import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IIconStyle;
import mcjty.theoneprobe.api.ILayoutStyle;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.config.Config;

public class TheOneProbeHandler implements Function<ITheOneProbe, Void>, IProbeInfoProvider {
	private static final ResourceLocation ICONS = ResourceLocation.tryBuild("theoneprobe", "textures/gui/icons.png");
	public static ITheOneProbe theOneProbeImp;

	public static TheOneProbeHandler create() {
		return new TheOneProbeHandler();
	}

	@Override
	public Void apply(ITheOneProbe input) {
		theOneProbeImp = input;
		theOneProbeImp.registerProvider(this);
		return null;
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data) {
		BlockEntity te = world.getBlockEntity(data.getPos());
		if(te instanceof WatchDogTimerBlockEntity be) {
			boolean v = Config.harvestStyleVanilla.get();
			int offs = v ? 16 : 0;
			int dim = v ? 13 : 16;
			ILayoutStyle alignment = probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER);
			IIconStyle iconStyle = probeInfo.defaultIconStyle().width(v ? 18 : 20).height(v ? 14 : 16).textureWidth(32)
					.textureHeight(32);
			IProbeInfo horizontal = probeInfo.horizontal(alignment);
			if (be.isEnabled()) {
				horizontal.icon(ICONS, 0, offs, dim, dim, iconStyle).text(CompoundText.create().style(TextStyleClass.OK).text(Component.translatable("label.toms_peripherals.wdt.enabled")));
			} else {
				horizontal.icon(ICONS, 16, offs, dim, dim, iconStyle).text(CompoundText.create().style(TextStyleClass.WARNING).text(Component.translatable("label.toms_peripherals.wdt.disabled")));
			}
			probeInfo.text(Component.translatable("label.toms_peripherals.wdt.timeLimit", ticksToElapsedTime(be.getTimeLimit())));
			probeInfo.text(Component.translatable("label.toms_peripherals.wdt.timer", ticksToElapsedTime(be.getTimer())));
		} else if(te instanceof CableBlockEntity be) {
			if (be.getPeripheral(null) instanceof WiredModemPeripheral modem) {
				Object[] nameArray = modem.getNameLocal();
				if (nameArray != null && nameArray.length == 1 && nameArray[0] instanceof String name) {
					probeInfo.horizontal()
					.text(Component.translatable("label.toms_peripherals.modem.name"))
					.text(CompoundText.create().style(TextStyleClass.HIGHLIGHTED).text(Component.literal(name)));
				}
			}
		} else if(te instanceof WiredModemFullBlockEntity be) {
			probeInfo.text(Component.translatable("label.toms_peripherals.full_modem.list"));
			boolean hasP = false;
			for (Direction d : Direction.values()) {
				var modem = be.getPeripheral(d);
				if (modem != null) {
					Object[] nameArray = modem.getNameLocal();
					if (nameArray != null && nameArray.length == 1 && nameArray[0] instanceof String name) {
						var h = probeInfo.horizontal()
								.text(Component.translatable("label.toms_peripherals.side." + d.name().toLowerCase(Locale.ROOT)).append(":"))
								.text(CompoundText.create().style(TextStyleClass.HIGHLIGHTED).text(Component.literal(name)));
						if(d.getAxis() != Axis.Y)
							h.text(Component.literal("(" + InfoUtil.getDirectionArrow(d.toYRot() - player.yRotO) + ")"));
						hasP = true;
					}
				}
			}
			if (!hasP)probeInfo.text(Component.translatable("label.toms_peripherals.full_modem.none"));
		}
	}

	public static String ticksToElapsedTime(int ticks) {
		int i = ticks / 20;
		int j = i / 60;
		i = i % 60;
		return i < 10 ? j + ":0" + i : j + ":" + i;
	}

	@Override
	public ResourceLocation getID() {
		return ResourceLocation.tryBuild(PeripheralsMod.ID, "top");
	}
}
