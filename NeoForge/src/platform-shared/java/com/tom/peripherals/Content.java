package com.tom.peripherals;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MenuType.MenuSupplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;

import com.mojang.serialization.Codec;

import com.tom.peripherals.block.GPUBlock;
import com.tom.peripherals.block.KeyboardBlock;
import com.tom.peripherals.block.KeyboardDongleBlock;
import com.tom.peripherals.block.MonitorBlock;
import com.tom.peripherals.block.RedstonePortBlock;
import com.tom.peripherals.block.WatchDogTimerBlock;
import com.tom.peripherals.block.entity.GPUBlockEntity;
import com.tom.peripherals.block.entity.KeyboardBlockEntity;
import com.tom.peripherals.block.entity.MonitorBlockEntity;
import com.tom.peripherals.block.entity.RedstonePortBlockEntity;
import com.tom.peripherals.block.entity.WatchDogTimerBlockEntity;
import com.tom.peripherals.item.LaserPointerItem;
import com.tom.peripherals.item.PortableKeyboardItem;
import com.tom.peripherals.menu.KeyboardMenu;
import com.tom.peripherals.platform.GameObject;
import com.tom.peripherals.platform.GameObject.GameObjectBlockEntity;
import com.tom.peripherals.platform.Platform;

public class Content {
	public static final GameObject<GPUBlock> gpu = blockWithItem("gpu", GPUBlock::new);
	public static final GameObject<MonitorBlock> monitor = blockWithItem("monitor", MonitorBlock::new);
	public static final GameObject<WatchDogTimerBlock> wdt = blockWithItem("wdt", WatchDogTimerBlock::new);
	public static final GameObject<RedstonePortBlock> redstonePort = blockWithItem("redstone_port", RedstonePortBlock::new);
	public static final GameObject<KeyboardBlock> keyboard = blockWithItem("keyboard", KeyboardBlock::new);
	public static final GameObject<KeyboardDongleBlock> keyboard_dongle = blockWithItem("keyboard_dongle", KeyboardDongleBlock::new);

	public static final GameObject<Item> gpuChip = item("gpu_chip", () -> new Item(new Item.Properties()));
	public static final GameObject<Item> gpuChipRaw = item("gpu_chip_raw", () -> new Item(new Item.Properties()));
	public static final GameObject<LaserPointerItem> laserPointer = item("laser_pointer", () -> new LaserPointerItem());
	public static final GameObject<PortableKeyboardItem> portableKeyboard = item("portable_keyboard", () -> new PortableKeyboardItem());

	public static final GameObjectBlockEntity<GPUBlockEntity> gpuBE = blockEntity("gpu", GPUBlockEntity::new, gpu);
	public static final GameObjectBlockEntity<MonitorBlockEntity> monitorBE = blockEntity("monitor", MonitorBlockEntity::new, monitor);
	public static final GameObjectBlockEntity<WatchDogTimerBlockEntity> wdtBE = blockEntity("wdt", WatchDogTimerBlockEntity::new, wdt);
	public static final GameObjectBlockEntity<RedstonePortBlockEntity> redstonePortBE = blockEntity("redstone_port", RedstonePortBlockEntity::new, redstonePort);
	public static final GameObjectBlockEntity<KeyboardBlockEntity> keyboardBE = blockEntity("keyboard", KeyboardBlockEntity::new, keyboard, keyboard_dongle);

	public static final GameObject<MenuType<KeyboardMenu>> keyboardMenu = menu("keyboard", KeyboardMenu::new);

	public static final GameObject<DataComponentType<BlockPos>> boundPosComponent = Platform.DATA_COMPONENT_TYPES.register("bound_pos", () -> DataComponentType.<BlockPos>builder().persistent(BlockPos.CODEC).build());
	public static final GameObject<DataComponentType<Boolean>> inUseComponent = Platform.DATA_COMPONENT_TYPES.register("in_use", () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build());

	private static <B extends Block> GameObject<B> blockWithItem(String name, Supplier<B> create) {
		return blockWithItem(name, create, b -> new BlockItem(b, new Item.Properties()));
	}

	private static <B extends Block, I extends Item> GameObject<B> blockWithItem(String name, Supplier<B> create, Function<Block, I> createItem) {
		GameObject<B> re = Platform.BLOCKS.register(name, create);
		item(name, () -> createItem.apply(re.get()));
		return re;
	}

	private static <I extends Item> GameObject<I> item(String name, Supplier<I> fact) {
		return Platform.ITEMS.register(name, () -> Platform.addItemToTab(fact.get()));
	}

	@SuppressWarnings("unchecked")
	@SafeVarargs
	private static <BE extends BlockEntity> GameObjectBlockEntity<BE> blockEntity(String name, BlockEntitySupplier<? extends BE> create, GameObject<? extends Block>... blocks) {
		return (GameObjectBlockEntity<BE>) Platform.BLOCK_ENTITY.registerBE(name, create, blocks);
	}

	private static <M extends AbstractContainerMenu> GameObject<MenuType<M>> menu(String name, MenuSupplier<M> create) {
		return Platform.MENU_TYPE.register(name, () -> new MenuType<>(create, FeatureFlags.VANILLA_SET));
	}

	public static void init() {
	}
}
