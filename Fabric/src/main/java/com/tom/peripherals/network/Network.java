package com.tom.peripherals.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import com.tom.peripherals.PeripheralsMod;
import com.tom.peripherals.util.IDataReceiver;

import io.netty.buffer.Unpooled;

public class Network {
	public static final ResourceLocation DATA_S2C = new ResourceLocation(PeripheralsMod.ID, "data_s2c");
	public static final ResourceLocation DATA_C2S = new ResourceLocation(PeripheralsMod.ID, "data_c2s");

	public static void sendToContainer(CompoundTag tag) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeNbt(tag);
		ClientPlayNetworking.send(DATA_C2S, buf);
	}

	public static void sendTo(ServerPlayer player, CompoundTag tag) {
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeNbt(tag);
		ServerPlayNetworking.send(player, DATA_S2C, buf);
	}

	public static void initCommon() {
		ServerPlayNetworking.registerGlobalReceiver(DATA_C2S, (s, p, h, buf, rp) -> {
			CompoundTag tag = buf.readNbt();
			s.submit(() -> {
				if(p.containerMenu instanceof IDataReceiver) {
					((IDataReceiver)p.containerMenu).receive(tag);
				}
			});
		});
	}
}
