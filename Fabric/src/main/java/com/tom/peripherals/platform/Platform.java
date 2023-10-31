package com.tom.peripherals.platform;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import com.tom.peripherals.Content;
import com.tom.peripherals.PeripheralsMod;
import com.tom.peripherals.platform.GameObject.GameRegistry;
import com.tom.peripherals.platform.GameObject.GameRegistryBE;

public class Platform {
	public static final GameRegistry<Item> ITEMS = new GameRegistry<>(Registry.ITEM);
	public static final GameRegistry<Block> BLOCKS = new GameRegistry<>(Registry.BLOCK);
	public static final GameRegistryBE BLOCK_ENTITY = new GameRegistryBE(Registry.BLOCK_ENTITY_TYPE);
	private static MinecraftServer serverInst;

	public static final CreativeModeTab MOD_TAB = FabricItemGroupBuilder.build(new ResourceLocation(PeripheralsMod.ID, "tab"), () -> new ItemStack(Content.gpu.get()));

	public static void register() {
		Platform.BLOCK_ENTITY.register();
		ServerLifecycleEvents.SERVER_STARTED.register(s -> {
			serverInst = s;
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(s -> {
			serverInst = null;
		});
	}

	public static MinecraftServer getServer() {
		return serverInst;
	}

}
