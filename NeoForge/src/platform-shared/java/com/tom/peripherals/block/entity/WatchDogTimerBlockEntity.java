package com.tom.peripherals.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import com.tom.peripherals.Content;
import com.tom.peripherals.api.LuaException;
import com.tom.peripherals.api.LuaMethod;
import com.tom.peripherals.api.ObjectWrapper;
import com.tom.peripherals.api.TMLuaObject;
import com.tom.peripherals.cc.ComputerControl;
import com.tom.peripherals.platform.AbstractPeripheralBlockEntity;
import com.tom.peripherals.platform.Platform;
import com.tom.peripherals.util.ParamCheck;
import com.tom.peripherals.util.TickerUtil.TickableServer;

public class WatchDogTimerBlockEntity extends AbstractPeripheralBlockEntity implements TickableServer {
	private ObjectWrapper peripheral;

	private boolean enabled;
	private int timeLimit;
	private int timer;

	public WatchDogTimerBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
		super(Content.wdtBE.get(), p_155229_, p_155230_);
	}

	@Override
	public void updateServer() {
		if (!enabled)return;
		if (timer > timeLimit) {
			enabled = false;
			Direction facting = getBlockState().getValue(BlockStateProperties.FACING);
			BlockPos onPos = getBlockPos().relative(facting);
			ComputerControl.restartComputerAt(level, onPos);
		} else
			timer++;
	}

	@Override
	protected void loadAdditional(CompoundTag tag, Provider p_338445_) {
		super.loadAdditional(tag, p_338445_);
		enabled = tag.getBoolean("enabled");
		timeLimit = tag.getInt("timeLimit");
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
		super.saveAdditional(tag, provider);
		tag.putBoolean("enabled", enabled);
		tag.putInt("timeLimit", timeLimit);
	}

	@Override
	public ObjectWrapper getPeripheral() {
		if(peripheral == null)peripheral = new ObjectWrapper("tm_wdt", new WDT());
		return peripheral;
	}

	public class WDT extends TMLuaObject {

		@LuaMethod
		public boolean isEnabled() {
			return enabled;
		}

		@LuaMethod
		public int getTimeout() {
			return timeLimit;
		}

		@LuaMethod
		public void setEnabled(Object[] a) throws LuaException {
			if (a.length < 1) {
				throw new LuaException("Too few arguments (expected enable)");
			}
			boolean enable = ParamCheck.getBoolean(a, 0);
			Platform.getServer().execute(() -> {
				enabled = enable;
				timer = 0;
				setChanged();
			});
		}

		@LuaMethod
		public void setTimeout(Object[] a) throws LuaException {
			if (enabled)throw new LuaException("Can't edit timeout value while the timer is enabled");
			if (a.length < 1) {
				throw new LuaException("Too few arguments (expected enable)");
			}
			int time = ParamCheck.getInt(a, 0);
			if (time < 20) {
				throw new LuaException("Bad argument #1 (expected value must be larger than 20 ticks)");
			}
			Platform.getServer().execute(() -> {
				timeLimit = time;
				timer = 0;
				setChanged();
			});
		}

		@LuaMethod
		public void reset() throws LuaException {
			Platform.getServer().execute(() -> {
				timer = 0;
			});
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public int getTimer() {
		return timer;
	}
}
