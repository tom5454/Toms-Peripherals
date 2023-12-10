package com.tom.peripherals;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.tom.peripherals.block.GPUBlock;
import com.tom.peripherals.block.MonitorBlock;
import com.tom.peripherals.block.RedstonePortBlock;
import com.tom.peripherals.block.WatchDogTimerBlock;
import com.tom.peripherals.block.entity.GPUBlockEntity;
import com.tom.peripherals.block.entity.MonitorBlockEntity;
import com.tom.peripherals.block.entity.RedstonePortBlockEntity;
import com.tom.peripherals.block.entity.WatchDogTimerBlockEntity;
import com.tom.peripherals.platform.GameObject;
import com.tom.peripherals.platform.GameObject.GameObjectBlockEntity;
import com.tom.peripherals.platform.GameObject.GameRegistryBE.BlockEntityFactory;
import com.tom.peripherals.platform.Platform;

public class Content {
	public static final GameObject<GPUBlock> gpu = blockWithItem("gpu", GPUBlock::new);
	public static final GameObject<MonitorBlock> monitor = blockWithItem("monitor", MonitorBlock::new);
	public static final GameObject<WatchDogTimerBlock> wdt = blockWithItem("wdt", WatchDogTimerBlock::new);
	public static final GameObject<RedstonePortBlock> redstonePort = blockWithItem("redstone_port", RedstonePortBlock::new);

	public static final GameObject<Item> gpuChip = item("gpu_chip", () -> new Item(new Item.Properties()));
	public static final GameObject<Item> gpuChipRaw = item("gpu_chip_raw", () -> new Item(new Item.Properties()));

	public static final GameObjectBlockEntity<GPUBlockEntity> gpuBE = blockEntity("gpu", GPUBlockEntity::new, gpu);
	public static final GameObjectBlockEntity<MonitorBlockEntity> monitorBE = blockEntity("monitor", MonitorBlockEntity::new, monitor);
	public static final GameObjectBlockEntity<WatchDogTimerBlockEntity> wdtBE = blockEntity("wdt", WatchDogTimerBlockEntity::new, wdt);
	public static final GameObjectBlockEntity<RedstonePortBlockEntity> redstonePortBE = blockEntity("redstone_port", RedstonePortBlockEntity::new, redstonePort);

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
	private static <BE extends BlockEntity> GameObjectBlockEntity<BE> blockEntity(String name, BlockEntityFactory<? extends BE> create, GameObject<? extends Block>... blocks) {
		return (GameObjectBlockEntity<BE>) Platform.BLOCK_ENTITY.registerBE(name, create, blocks);
	}

	public static void init() {
	}
}
