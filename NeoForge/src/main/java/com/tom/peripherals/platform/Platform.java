package com.tom.peripherals.platform;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import com.tom.peripherals.Content;
import com.tom.peripherals.PeripheralsMod;
import com.tom.peripherals.platform.GameObject.GameRegistry;
import com.tom.peripherals.platform.GameObject.GameRegistryBE;

public class Platform {
	public static final GameRegistry<Item> ITEMS = new GameRegistry<>(Registries.ITEM);
	public static final GameRegistry<Block> BLOCKS = new GameRegistry<>(Registries.BLOCK);
	public static final GameRegistryBE BLOCK_ENTITY = new GameRegistryBE();
	public static final DeferredRegister<CreativeModeTab> TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PeripheralsMod.ID);
	public static final GameRegistry<MenuType<?>> MENU_TYPE = new GameRegistry<>(Registries.MENU);
	public static final GameRegistry<DataComponentType<?>> DATA_COMPONENT_TYPES = new GameRegistry<>(Registries.DATA_COMPONENT_TYPE);

	public static void register(IEventBus bus) {
		ITEMS.register(bus);
		BLOCKS.register(bus);
		BLOCK_ENTITY.register(bus);
		MENU_TYPE.register(bus);
		TAB.register(bus);
		DATA_COMPONENT_TYPES.register(bus);
	}

	public static MinecraftServer getServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}

	private static List<Item> tabItems = new ArrayList<>();
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_TAB = TAB.register("tab", () ->
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
