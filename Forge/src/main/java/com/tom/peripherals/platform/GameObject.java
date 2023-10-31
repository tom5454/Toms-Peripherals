package com.tom.peripherals.platform;

import java.util.Arrays;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import com.tom.peripherals.PeripheralsMod;

public class GameObject<T> {
	private final RegistryObject<T> value;

	protected GameObject(RegistryObject<T> value) {
		this.value = value;
	}

	public T get() {
		return value.get();
	}

	public static class GameRegistry<T> {
		protected final DeferredRegister<T> handle;

		public GameRegistry(IForgeRegistry<T> reg) {
			handle = DeferredRegister.create(reg, PeripheralsMod.ID);
		}

		public <I extends T> GameObject<I> register(final String name, final Supplier<? extends I> sup) {
			return new GameObject<>(handle.register(name, sup));
		}

		public void register() {
			handle.register(FMLJavaModLoadingContext.get().getModEventBus());
		}
	}

	public ResourceLocation getId() {
		return value.getId();
	}

	public static class GameRegistryBE extends GameRegistry<BlockEntityType<?>> {

		public GameRegistryBE() {
			super(ForgeRegistries.BLOCK_ENTITY_TYPES);
		}

		@SuppressWarnings("unchecked")
		public <BE extends BlockEntity, I extends BlockEntityType<BE>> GameObjectBlockEntity<BE> registerBE(String name, BlockEntityFactory<BE> sup, GameObject<? extends Block>... blocks) {
			return new GameObjectBlockEntity<>(handle.register(name, () -> {
				BlockEntityType<BE>[] type = new BlockEntityType[1];
				Block[] bl = Arrays.stream(blocks).map(GameObject::get).toArray(Block[]::new);
				type[0] = BlockEntityType.Builder.<BE>of((a, b) -> sup.create(type[0], a, b), bl).build(null);
				return type[0];
			}));
		}

		public static interface BlockEntityFactory<T extends BlockEntity> {
			T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
		}
	}

	public static class GameObjectBlockEntity<T extends BlockEntity> extends GameObject<BlockEntityType<T>> {

		protected GameObjectBlockEntity(RegistryObject<BlockEntityType<T>> value) {
			super(value);
		}

	}
}
