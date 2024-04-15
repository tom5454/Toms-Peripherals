package com.tom.peripherals.block.entity;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.peripherals.Content;
import com.tom.peripherals.api.IComputer;
import com.tom.peripherals.api.ITMPeripheral;
import com.tom.peripherals.api.LuaException;
import com.tom.peripherals.menu.KeyboardMenu;
import com.tom.peripherals.platform.AbstractPeripheralBlockEntity;
import com.tom.peripherals.util.ParamCheck;

public class KeyboardBlockEntity extends AbstractPeripheralBlockEntity implements MenuProvider {
	private Peripheral per;

	public KeyboardBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
		super(p_155228_, p_155229_, p_155230_);
	}

	@Override
	public Peripheral getPeripheral() {
		if (per == null)per = new Peripheral();
		return per;
	}

	@Override
	public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
		return new KeyboardMenu(p_39954_, p_39955_, this);
	}

	@Override
	public Component getDisplayName() {
		return Component.empty();
	}

	public void queueEvent(String event, Object[] args) {
		getPeripheral().queueEvent(event, args);
	}

	private static class CompSettings {
		private boolean fireNativeEvents;
	}

	private class Peripheral implements ITMPeripheral {
		private static final String[] METHODS = { "setFireNativeEvents" };
		private Map<IComputer, CompSettings> computers = new ConcurrentHashMap<>();

		@Override
		public String getType() {
			return "tm_keyboard";
		}

		@Override
		public String[] getMethodNames() {
			return METHODS;
		}

		@Override
		public Object[] call(IComputer computer, String method, Object[] args) throws LuaException {
			if (method.equals("setFireNativeEvents")) {
				boolean v = ParamCheck.getBoolean(args, 0);
				computers.computeIfAbsent(computer, __ -> new CompSettings()).fireNativeEvents = v;
			}
			return null;
		}

		@Override
		public void attach(IComputer computer) {
			computers.put(computer, new CompSettings());
		}

		@Override
		public void detach(IComputer computer) {
			computers.remove(computer);
		}

		public void queueEvent(String event, Object[] args) {
			Object[] a = new Object[args.length + 1];
			for (int i = 0;i < args.length;i++) {
				a[i + 1] = args[i];
			}
			for (Entry<IComputer, CompSettings> c : computers.entrySet()) {
				if (c.getValue().fireNativeEvents) {
					c.getKey().queueEvent(event, args);
				} else {
					a[0] = c.getKey().getAttachmentName();
					c.getKey().queueEvent("tm_keyboard_" + event, a);
				}
			}
		}
	}

	public void onKeyboardOpen(int id) {
		if (getBlockState().is(Content.keyboard_dongle.get()))
			queueEvent("portable_connect", new Object[] {id});
	}

	public void onKeyboardClosed(int id) {
		if (getBlockState().is(Content.keyboard_dongle.get()))
			queueEvent("portable_disconnect", new Object[] {id});
	}

	public boolean menuStillValid(Player player) {
		if (getBlockState().is(Content.keyboard_dongle.get()))
			return worldPosition.distSqr(player.blockPosition()) < 64 * 64;
		return worldPosition.distSqr(player.blockPosition()) < 16 * 16;
	}
}
