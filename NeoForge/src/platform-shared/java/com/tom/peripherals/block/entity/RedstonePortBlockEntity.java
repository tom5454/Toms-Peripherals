package com.tom.peripherals.block.entity;

import java.util.Arrays;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.peripherals.Content;
import com.tom.peripherals.api.LuaException;
import com.tom.peripherals.api.LuaMethod;
import com.tom.peripherals.api.ObjectWrapper;
import com.tom.peripherals.api.TMLuaObject;
import com.tom.peripherals.platform.AbstractPeripheralBlockEntity;
import com.tom.peripherals.util.TickerUtil.TickableServer;

import dan200.computercraft.impl.BundledRedstone;
import dan200.computercraft.shared.util.DirectionUtil;
import dan200.computercraft.shared.util.RedstoneUtil;

public class RedstonePortBlockEntity extends AbstractPeripheralBlockEntity implements TickableServer {
	private static final Object[] SIDES = Arrays.stream(Direction.values()).map(e -> e.getSerializedName()).toArray();
	private ObjectWrapper peripheral;

	private boolean internalOutputChanged = false;
	private final int[] internalOutput = new int[6];
	private final int[] internalBundledOutput = new int[6];
	private final int[] externalOutput = new int[6];
	private final int[] externalBundledOutput = new int[6];
	private boolean inputChanged = false;
	private final int[] input = new int[6];
	private final int[] bundledInput = new int[6];

	public RedstonePortBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
		super(Content.redstonePortBE.get(), p_155229_, p_155230_);
	}

	@Override
	public ObjectWrapper getPeripheral() {
		if(peripheral == null)peripheral = new ObjectWrapper("tm_rsPort", new RSPort());
		return peripheral;
	}

	public class RSPort extends TMLuaObject {

		@LuaMethod
		public Object[] getSides() throws LuaException {
			return SIDES;
		}

		@LuaMethod
		public boolean getInput(Object[] args) throws LuaException {
			if (args.length < 1) {
				throw new LuaException("Too few arguments (expected direction)");
			}
			return RedstonePortBlockEntity.this.getInput(getDir(args[0])) > 0;
		}

		@LuaMethod
		public int getAnalogInput(Object[] args) throws LuaException {
			if (args.length < 1) {
				throw new LuaException("Too few arguments (expected direction)");
			}
			return RedstonePortBlockEntity.this.getInput(getDir(args[0]));
		}

		@LuaMethod
		public int getAnalogueInput(Object[] args) throws LuaException {
			return getAnalogInput(args);
		}

		@LuaMethod
		public int getBundledInput(Object[] args) throws LuaException {
			if (args.length < 1) {
				throw new LuaException("Too few arguments (expected direction)");
			}
			return RedstonePortBlockEntity.this.getBundledInput(getDir(args[0]));
		}



		@LuaMethod
		public boolean getOutput(Object[] args) throws LuaException {
			if (args.length < 1) {
				throw new LuaException("Too few arguments (expected direction)");
			}
			return RedstonePortBlockEntity.this.getOutput(getDir(args[0])) > 0;
		}

		@LuaMethod
		public int getAnalogOutput(Object[] args) throws LuaException {
			if (args.length < 1) {
				throw new LuaException("Too few arguments (expected direction)");
			}
			return RedstonePortBlockEntity.this.getOutput(getDir(args[0]));
		}

		@LuaMethod
		public int getAnalogueOutput(Object[] args) throws LuaException {
			return getAnalogOutput(args);
		}

		@LuaMethod
		public int getBundledOutput(Object[] args) throws LuaException {
			if (args.length < 1) {
				throw new LuaException("Too few arguments (expected direction)");
			}
			return RedstonePortBlockEntity.this.getBundledOutput(getDir(args[0]));
		}


		@LuaMethod
		public void setOutput(Object[] args) throws LuaException {
			if (args.length < 2) {
				throw new LuaException("Too few arguments (expected direction, value)");
			}
			if (!(args[1] instanceof Boolean v))
				throw new LuaException("Bad argument #2 (expected Boolean)");
			RedstonePortBlockEntity.this.setOutput(getDir(args[0]), v ? 15 : 0);
		}

		@LuaMethod
		public void setAnalogOutput(Object[] args) throws LuaException {
			if (args.length < 2) {
				throw new LuaException("Too few arguments (expected direction, value)");
			}
			if (!(args[1] instanceof Double v))
				throw new LuaException("Bad argument #2 (expected Number)");
			Direction dir = getDir(args[0]);
			int out = Mth.floor(v);
			if (out < 0 || out > 15)
				throw new LuaException("Bad argument #2: Expected number in range 0-15");

			RedstonePortBlockEntity.this.setOutput(dir, out);
		}

		@LuaMethod
		public void setAnalogueOutput(Object[] args) throws LuaException {
			setAnalogOutput(args);
		}

		@LuaMethod
		public void setBundledOutput(Object[] args) throws LuaException {
			if (args.length < 2) {
				throw new LuaException("Too few arguments (expected direction, value)");
			}
			if (!(args[1] instanceof Double v))
				throw new LuaException("Bad argument #2 (expected Number)");
			Direction dir = getDir(args[0]);
			int out = Mth.floor(v);

			RedstonePortBlockEntity.this.setBundledOutput(dir, out);
		}


		@LuaMethod
		public boolean testBundledInput(Object[] args) throws LuaException {
			if (args.length < 2) {
				throw new LuaException("Too few arguments (expected direction, mask)");
			}
			if (!(args[1] instanceof Double md))
				throw new LuaException("Bad argument #2 (expected Number)");
			int value = RedstonePortBlockEntity.this.getBundledInput(getDir(args[0]));
			int mask = Mth.floor(md);
			return (value & mask) == mask;
		}

		private Direction getDir(Object in) throws LuaException {
			String side = String.valueOf(in);
			Direction dir = null;
			for(Direction d : Direction.values()) {
				if (d.getName().equalsIgnoreCase(side)) {
					dir = d;
					break;
				}
			}
			if (dir == null)throw new LuaException("Bad argument #1: expected one of: up, down, north, south, east, west");
			return dir;
		}
	}

	public boolean updateOutput() {
		synchronized (this.internalOutput) {
			if (!this.internalOutputChanged) {
				return false;
			} else {
				boolean changed = false;

				for (int i = 0; i < 6; ++i) {
					if (this.externalOutput[i] != this.internalOutput[i]) {
						this.externalOutput[i] = this.internalOutput[i];
						changed = true;
					}

					if (this.externalBundledOutput[i] != this.internalBundledOutput[i]) {
						this.externalBundledOutput[i] = this.internalBundledOutput[i];
						changed = true;
					}
				}

				this.internalOutputChanged = false;
				return changed;
			}
		}
	}

	@Override
	public void updateServer() {
		if (inputChanged) {
			inputChanged = false;
			getPeripheral().queueEvent("tm_redstone", new Object[0]);
		}
		if (updateOutput()) {
			for (var dir : DirectionUtil.FACINGS) RedstoneUtil.propagateRedstoneOutput(getLevel(), getBlockPos(), dir);
			updateRedstoneInputs();
		}
	}

	public int getInput(Direction side) {
		return input[side.ordinal()];
	}

	public int getBundledInput(Direction side) {
		return bundledInput[side.ordinal()];
	}

	public void setOutput(Direction side, int output) {
		var index = side.ordinal();
		synchronized (internalOutput) {
			if (internalOutput[index] != output) {
				internalOutput[index] = output;
				internalOutputChanged = true;
			}
		}
	}

	public int getOutput(Direction side) {
		synchronized (internalOutput) {
			return internalOutput[side.ordinal()];
		}
	}

	public void setBundledOutput(Direction side, int output) {
		var index = side.ordinal();
		synchronized (internalOutput) {
			if (internalBundledOutput[index] != output) {
				internalBundledOutput[index] = output;
				internalOutputChanged = true;
			}
		}
	}

	public int getBundledOutput(Direction side) {
		synchronized (internalOutput) {
			return internalBundledOutput[side.ordinal()];
		}
	}

	public int getExternalRedstoneOutput(Direction side) {
		return externalOutput[side.ordinal()];
	}

	public int getExternalBundledRedstoneOutput(Direction side) {
		return externalBundledOutput[side.ordinal()];
	}

	public void setRedstoneInput(Direction side, int level) {
		var index = side.ordinal();
		if (input[index] != level) {
			input[index] = level;
			inputChanged = true;
		}
	}

	public void setBundledRedstoneInput(Direction side, int combination) {
		var index = side.ordinal();
		if (bundledInput[index] != combination) {
			bundledInput[index] = combination;
			inputChanged = true;
		}
	}

	private void updateRedstoneInput(Direction dir, BlockPos targetPos) {
		var offsetSide = dir.getOpposite();

		setRedstoneInput(dir, RedstoneUtil.getRedstoneInput(level, targetPos, dir));
		setBundledRedstoneInput(dir, BundledRedstone.getOutput(getLevel(), targetPos, offsetSide));
	}

	private void updateRedstoneInputs() {
		var pos = getBlockPos();
		for (var dir : DirectionUtil.FACINGS) updateRedstoneInput(dir, pos.relative(dir));
	}

	public void neighborChanged(BlockPos neighbour) {
		updateInputAt(neighbour);
	}

	private void updateInputAt(BlockPos neighbour) {
		for (var dir : DirectionUtil.FACINGS) {
			var offset = getBlockPos().relative(dir);
			if (offset.equals(neighbour)) {
				updateRedstoneInput(dir, offset);
				return;
			}
		}

		// If the position is not any adjacent one, update all inputs. This is pretty terrible, but some redstone mods
		// handle this incorrectly.
		updateRedstoneInputs();
	}
}
