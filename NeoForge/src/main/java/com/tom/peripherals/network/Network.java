package com.tom.peripherals.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import com.tom.peripherals.PeripheralsMod;
import com.tom.peripherals.util.IDataReceiver;

public class Network {

	@SubscribeEvent
	public static void register(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(PeripheralsMod.ID);

		registrar.playBidirectional(DataPacket.ID, DataPacket.STREAM_CODEC, new DirectionalPayloadHandler<>(Network::handleDataClient, Network::handleDataServer));
	}

	public static void handleDataServer(DataPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			ServerPlayer sender = (ServerPlayer) context.player();
			if(sender.containerMenu instanceof IDataReceiver) {
				((IDataReceiver)sender.containerMenu).receive(packet.tag());
			}
		});
	}

	public static void handleDataClient(DataPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if(Minecraft.getInstance().screen instanceof IDataReceiver) {
				((IDataReceiver)Minecraft.getInstance().screen).receive(packet.tag());
			}
		});
	}

	public static void sendToContainer(CompoundTag tag) {
		PacketDistributor.sendToServer(new DataPacket(tag));
	}

	public static void sendTo(ServerPlayer pl, CompoundTag tag) {
		PacketDistributor.sendToPlayer(pl, new DataPacket(tag));
	}
}
