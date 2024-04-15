package com.tom.peripherals.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import com.tom.peripherals.PeripheralsMod;

public record DataPacket(CompoundTag tag) implements CustomPacketPayload {
	public static final ResourceLocation ID = new ResourceLocation(PeripheralsMod.ID, "data");

	public DataPacket(FriendlyByteBuf pb) {
		this(pb.readNbt());
	}

	@Override
	public void write(FriendlyByteBuf pb) {
		pb.writeNbt(tag);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}
}
