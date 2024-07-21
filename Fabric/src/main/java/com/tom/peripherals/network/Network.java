package com.tom.peripherals.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import com.tom.peripherals.util.IDataReceiver;

public class Network {

	public static void sendToContainer(CompoundTag tag) {
		ClientPlayNetworking.send(new DataPacket(tag));
	}

	public static void sendTo(ServerPlayer player, CompoundTag tag) {
		ServerPlayNetworking.send(player, new DataPacket(tag));
	}

	public static void initCommon() {
		PayloadTypeRegistry.playS2C().register(DataPacket.ID, DataPacket.STREAM_CODEC);
		PayloadTypeRegistry.playC2S().register(DataPacket.ID, DataPacket.STREAM_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(DataPacket.ID, (p, c) -> {
			if(c.player().containerMenu instanceof IDataReceiver d) {
				d.receive(p.tag());
			}
		});
	}
}
