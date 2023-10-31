package com.tom.peripherals.block.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import com.tom.peripherals.Config;
import com.tom.peripherals.platform.AbstractPeripheralBlockEntity;
import com.tom.peripherals.platform.Platform;
import com.tom.peripherals.screen.gpu.BaseGPU.GPUContext;
import com.tom.peripherals.screen.gpu.GPUImpl;
import com.tom.peripherals.screen.gpu.VRAM;
import com.tom.peripherals.screen.gpu.VRAM.VRAMObject;
import com.tom.peripherals.util.ITMPeripheral;
import com.tom.peripherals.util.ITMPeripheral.LuaException;
import com.tom.peripherals.util.ITMPeripheral.LuaMethod;
import com.tom.peripherals.util.ParamCheck;

public class GPUBlockEntity extends AbstractPeripheralBlockEntity {
	private GPUPeripheral peripheral;

	public GPUBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
		super(p_155228_, p_155229_, p_155230_);
	}

	private class GPUPeripheral implements ITMPeripheral, GPUContext, VRAMObject {
		private List<List<MonitorBlockEntity>> monitors;
		private List<IComputer> computers = new ArrayList<>();

		private int maxX = 0;
		private int maxY = 0;
		private int size = 16;
		private int[][] screen = new int[16][16];
		private int error = 0;

		private GPUImpl impl = new GPUExt(this);
		private VRAM vram = new VRAM(16 * 1024 * 1024);

		public GPUPeripheral() {
			vram.alloc(this);
		}

		@Override
		public String getType() {
			return "tm_gpu";
		}

		@Override
		public String[] getMethodNames() {
			return impl.getMethodNames();
		}

		@Override
		public Object[] call(IComputer computer, String method, Object[] args) throws LuaException {
			switch (error) {
			case 1 -> throw new LuaException("Attached screen too big");
			case 2 -> throw new LuaException("Not enough VRAM for screen buffer");
			default -> {}
			}
			try {
				return impl.callInt(computer, method, args);
			} catch (NoSuchMethodException e) {
				throw new LuaException("No such method");
			}
		}

		@Override
		public void attach(IComputer computer) {
			computers.add(computer);
		}

		@Override
		public void detach(IComputer computer) {
			computers.remove(computer);
		}

		@Override
		public void set(int x, int y, int c) {
			screen[x][y] = Integer.reverseBytes(c) >> 8 | 0xFF000000;
		}

		@Override
		public void sync() {
			Platform.getServer().execute(() -> {
				int index1 = 0;
				for (List<MonitorBlockEntity> cMonList : getMonitors(false)) {
					int index2 = cMonList.size() - 1;
					for (MonitorBlockEntity mon : cMonList) {
						mon.screen = separateIntArray(screen, index1, index2, size);
						mon.width = size;
						mon.sync();
						index2--;
					}
					index1++;
				}
			});
		}

		public MonitorBlockEntity findMonitor() {
			for (Direction f : Direction.values()) {
				if (f.getAxis() == Axis.Y)continue;
				BlockPos p = worldPosition.relative(f);
				if (level.getBlockEntity(p) instanceof MonitorBlockEntity m) {
					return m;
				}
			}
			return null;
		}

		public List<List<MonitorBlockEntity>> connectMonitors(MonitorBlockEntity base) {
			error = 0;
			if (base != null) {
				List<MonitorBlockEntity> listX = this.getMonitorsRight(base);
				List<List<MonitorBlockEntity>> mons = new ArrayList<>();
				List<MonitorBlockEntity> listBY = this.getMonitorsUp(base);
				int maxSizeY = listBY.size() + 1;
				int maxSizeX = listX.size() + 1;
				for (MonitorBlockEntity mon : listX) {
					List<MonitorBlockEntity> cMons = this.getMonitorsUp(mon);
					maxSizeY = Math.min(maxSizeY, cMons.size());
				}
				for (MonitorBlockEntity mon : listBY) {
					List<MonitorBlockEntity> cMons = this.getMonitorsRight(mon);
					maxSizeX = Math.min(maxSizeX, cMons.size());
				}
				if (maxSizeX > Config.maxScreenSize || maxSizeY > Config.maxScreenSize) {
					error = 1;
					return Collections.emptyList();
				}
				for (int x = 0;x < maxSizeX;x++) {
					List<MonitorBlockEntity> cM = new ArrayList<>();
					for (int y = 0;y < maxSizeY;y++) {
						cM.add(((MonitorBlockEntity) level.getBlockEntity(base.getOffset(x, y, base.getDirection()))).connect(worldPosition));
					}
					mons.add(cM);
				}
				int oldX = this.maxX;
				int oldY = this.maxY;
				this.maxX = maxSizeX;
				this.maxY = maxSizeY;
				if (oldX != this.maxX || oldY != this.maxY) {
					int newSize = this.maxX * this.size * this.maxY * this.size * 4;
					if(vram.realloc(this, newSize))
						this.screen = new int[this.maxX * this.size][this.maxY * this.size];
					else {
						error = 2;
						return Collections.emptyList();
					}
				}
				return mons;
			}
			return Collections.emptyList();
		}

		public List<MonitorBlockEntity> getMonitorsUp(MonitorBlockEntity master) {
			List<MonitorBlockEntity> connectedMonitors = new ArrayList<>();
			if (master != null) {
				Stack<MonitorBlockEntity> traversingMonitors = new Stack<>();
				traversingMonitors.add(master);
				Direction direction = master.getDirection();
				int i = 1;
				while (!traversingMonitors.isEmpty()) {
					MonitorBlockEntity storage = traversingMonitors.pop();
					connectedMonitors.add(storage);
					BlockEntity te = level.getBlockEntity(master.getOffset(0, i, direction));
					if (te instanceof MonitorBlockEntity m && !connectedMonitors.contains(m) && m.getDirection() == direction) {
						traversingMonitors.add(m);
					} else {
						break;
					}
					i++;
				}
			}
			return connectedMonitors;
		}

		public List<MonitorBlockEntity> getMonitorsRight(MonitorBlockEntity master) {
			List<MonitorBlockEntity> connectedMonitors = new ArrayList<>();
			if (master != null) {
				Stack<MonitorBlockEntity> traversingMonitors = new Stack<>();
				traversingMonitors.add(master);
				Direction direction = master.getDirection();
				int i = 1;
				while (!traversingMonitors.isEmpty()) {
					MonitorBlockEntity storage = traversingMonitors.pop();
					connectedMonitors.add(storage);
					BlockEntity te = level.getBlockEntity(master.getOffset(i, 0, direction));
					if (te instanceof MonitorBlockEntity m && !connectedMonitors.contains(m) && m.getDirection() == direction) {
						traversingMonitors.add(m);
					} else {
						break;
					}
					i++;
				}
			}
			return connectedMonitors;
		}

		public static int[] separateIntArray(int[][] in, int index1, int index2, int size) {
			int[] ret = new int[size * size];
			try {
				int indexStart1 = index1 * size;
				int indexStart2 = index2 * size;
				int indexEnd1 = ((index1 + 1) * size);
				int indexEnd2 = ((index2 + 1) * size);
				int i2 = 0;
				for (int x = indexStart1;x < indexEnd1;x++) {
					int i1 = 0;
					for (int y = indexStart2;y < indexEnd2;y++) {
						ret[i1 * size + i2] = in[x][y];
						i1++;
					}
					i2++;
				}
			} catch (Exception e) {}
			return ret;
		}

		public void monitorClick(BlockPos pos, int x, int y, boolean soft) {
			int index1 = 0;
			for (List<MonitorBlockEntity> cMonList : getMonitors(false)) {
				int index2 = cMonList.size() - 1;
				for (MonitorBlockEntity mon : cMonList) {
					if (mon != null) {
						BlockPos monp = mon.getBlockPos();
						if (monp.equals(pos)) {
							int xP = x + (this.size * index1);
							int yP = y + (this.size * index2);
							this.queueEvent("tm_monitor_touch", new Object[]{xP + 1, yP + 1, soft});
							break;
						}
					}
					index2--;
				}
				index1++;
			}
		}

		public void queueEvent(String event, Object[] args) {
			Object[] a = new Object[args.length + 1];
			for (int i = 0;i < args.length;i++) {
				a[i + 1] = args[i];
			}
			for (IComputer c : computers) {
				a[0] = c.getAttachmentName();
				c.queueEvent(event, a);
			}
		}

		private List<List<MonitorBlockEntity>> getMonitors(boolean forceRefresh) {
			if (monitors == null || forceRefresh) {
				monitors = connectMonitors(findMonitor());
			}
			return monitors;
		}

		@Override
		public int getWidth() {
			return maxX * size;
		}

		@Override
		public int getHeight() {
			return maxY * size;
		}

		@Override
		public VRAM getVRam() {
			return vram;
		}

		@Override
		public long getSize() {
			return this.maxX * this.size * this.maxY * this.size * 4;
		}
	}

	public static class GPUExt extends GPUImpl {
		private GPUPeripheral gpu;

		public GPUExt(GPUPeripheral ctx) {
			super(ctx);
			this.gpu = ctx;
		}

		@LuaMethod
		public void setSize(Object[] a) throws LuaException {
			int s = ParamCheck.getInt(a, 0);
			if (s < 16)
				throw new LuaException("Bad Argument #1, (too small number (" + s + ") minimum value is 16 )");
			if (s > 64)
				throw new LuaException("Bad Argument #1, (too big number (" + s + ") maximum value is " + 64 + " )");

			int size = s * gpu.maxX * s * gpu.maxY * 4;
			gpu.vram.reallocEx(gpu, size);

			gpu.screen = new int[s * gpu.maxX][s * gpu.maxY];
			gpu.size = s;
		}

		@LuaMethod
		public void refreshSize() {
			Platform.getServer().execute(() -> {
				gpu.getMonitors(true);
			});
		}

		@Override
		@LuaMethod
		public Object[] getSize() {
			return new Object[]{this.ctx.getWidth(), this.ctx.getHeight(), gpu.maxX, gpu.maxY, gpu.size};
		}
	}

	@Override
	public GPUPeripheral getPeripheral() {
		if (peripheral == null)peripheral = new GPUPeripheral();
		return peripheral;
	}

	public void monitorClick(BlockPos pos, int x, int y, boolean soft) {
		getPeripheral().monitorClick(pos, x, y, soft);
	}
}
