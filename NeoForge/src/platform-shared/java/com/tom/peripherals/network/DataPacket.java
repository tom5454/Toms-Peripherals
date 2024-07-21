package com.tom.peripherals.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import com.tom.peripherals.PeripheralsMod;

public record DataPacket(CompoundTag tag) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<DataPacket> ID = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(PeripheralsMod.ID, "data"));
	public static final StreamCodec<FriendlyByteBuf, DataPacket> STREAM_CODEC = CustomPacketPayload.codec(DataPacket::write, DataPacket::new);

	private DataPacket(FriendlyByteBuf pb) {
		this((CompoundTag) pb.readNbt(NbtAccounter.unlimitedHeap()));
	}

	private void write(FriendlyByteBuf pb) {
		pb.writeNbt(tag);
	}

	@Override
	public CustomPacketPayload.Type<DataPacket> type() {
		return ID;
	}
}
