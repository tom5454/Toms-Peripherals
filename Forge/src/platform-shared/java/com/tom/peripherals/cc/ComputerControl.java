package com.tom.peripherals.cc;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import dan200.computercraft.shared.computer.blocks.TileComputerBase;
import dan200.computercraft.shared.computer.core.ServerComputer;

public class ComputerControl {

	public static void restartComputerAt(Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof TileComputerBase c) {
			ServerComputer computer = c.getServerComputer();
			if (computer != null) {
				computer.reboot();
			}
		}
	}
}
