package com.tom.peripherals.platform;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import com.tom.peripherals.Content;
import com.tom.peripherals.PeripheralsMod;
import com.tom.peripherals.platform.GameObject.GameRegistry;
import com.tom.peripherals.platform.GameObject.GameRegistryBE;

public class Platform {
	public static final GameRegistry<Item> ITEMS = new GameRegistry<>(BuiltInRegistries.ITEM);
	public static final GameRegistry<Block> BLOCKS = new GameRegistry<>(BuiltInRegistries.BLOCK);
	public static final GameRegistryBE BLOCK_ENTITY = new GameRegistryBE(BuiltInRegistries.BLOCK_ENTITY_TYPE);
	public static final GameRegistry<MenuType<?>> MENU_TYPE = new GameRegistry<>(BuiltInRegistries.MENU);
	private static MinecraftServer serverInst;

	private static List<Item> tabItems = new ArrayList<>();

	public static <I extends Item> I addItemToTab(I item) {
		tabItems.add(item);
		return item;
	}

	private static final ResourceKey<CreativeModeTab> ITEM_GROUP = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(PeripheralsMod.ID, "tab"));

	public static final CreativeModeTab MOD_TAB = FabricItemGroup.builder().title(Component.translatable("itemGroup.toms_peripherals.tab")).icon(() -> new ItemStack(Content.gpu.get())).displayItems((p, out) -> {
		tabItems.forEach(out::accept);
	}).build();

	static {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ITEM_GROUP, MOD_TAB);
	}

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
