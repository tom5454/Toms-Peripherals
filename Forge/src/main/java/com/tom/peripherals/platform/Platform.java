package com.tom.peripherals.platform;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.server.ServerLifecycleHooks;

import com.tom.peripherals.Content;
import com.tom.peripherals.PeripheralsMod;
import com.tom.peripherals.platform.GameObject.GameRegistry;
import com.tom.peripherals.platform.GameObject.GameRegistryBE;

public class Platform {
	public static final GameRegistry<Item> ITEMS = new GameRegistry<>(ForgeRegistries.ITEMS);
	public static final GameRegistry<Block> BLOCKS = new GameRegistry<>(ForgeRegistries.BLOCKS);
	public static final GameRegistryBE BLOCK_ENTITY = new GameRegistryBE();
	public static final DeferredRegister<CreativeModeTab> TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PeripheralsMod.ID);
	public static final GameRegistry<MenuType<?>> MENU_TYPE = new GameRegistry<>(ForgeRegistries.MENU_TYPES);

	public static void register() {
		ITEMS.register();
		BLOCKS.register();
		BLOCK_ENTITY.register();
		MENU_TYPE.register();
		TAB.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	public static MinecraftServer getServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}

	private static List<Item> tabItems = new ArrayList<>();
	public static final RegistryObject<CreativeModeTab> MOD_TAB = TAB.register("tab", () ->
	CreativeModeTab.builder()
	.title(Component.translatable("itemGroup.toms_peripherals.tab"))
	.icon(() -> new ItemStack(Content.gpu.get()))
	.displayItems((p, out) -> {
		tabItems.forEach(out::accept);
	})
	.build()
			);

	public static <I extends Item> I addItemToTab(I item) {
		tabItems.add(item);
		return item;
	}
}
