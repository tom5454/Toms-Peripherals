package com.tom.peripherals.platform;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import com.tom.peripherals.Content;
import com.tom.peripherals.platform.GameObject.GameRegistry;
import com.tom.peripherals.platform.GameObject.GameRegistryBE;

public class Platform {
	public static final GameRegistry<Item> ITEMS = new GameRegistry<>(ForgeRegistries.ITEMS);
	public static final GameRegistry<Block> BLOCKS = new GameRegistry<>(ForgeRegistries.BLOCKS);
	public static final GameRegistryBE BLOCK_ENTITY = new GameRegistryBE();
	public static final GameRegistry<MenuType<?>> MENU_TYPE = new GameRegistry<>(ForgeRegistries.CONTAINERS);

	public static void register() {
		ITEMS.register();
		BLOCKS.register();
		BLOCK_ENTITY.register();
		MENU_TYPE.register();
	}

	public static MinecraftServer getServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}

	public static final CreativeModeTab MOD_TAB = new CreativeModeTab("toms_peripherals.tab") {

		@Override
		public ItemStack makeIcon() {
			return new ItemStack(Content.gpu.get());
		}
	};
}
