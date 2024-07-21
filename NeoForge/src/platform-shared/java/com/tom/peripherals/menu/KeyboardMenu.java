package com.tom.peripherals.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import com.tom.peripherals.Content;
import com.tom.peripherals.block.entity.KeyboardBlockEntity;
import com.tom.peripherals.block.entity.MonitorBlockEntity;
import com.tom.peripherals.util.IDataReceiver;

import dan200.computercraft.shared.container.InvisibleSlot;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlock;

public class KeyboardMenu extends AbstractContainerMenu implements IDataReceiver {
	private KeyboardBlockEntity te;
	private Inventory inv;
	private int randomId;

	public KeyboardMenu(int id, Inventory inv) {
		super(Content.keyboardMenu.get(), id);
		this.inv = inv;
		addSlots(inv);
	}

	public KeyboardMenu(int id, Inventory inv, KeyboardBlockEntity te) {
		this(id, inv);
		this.te = te;
		te.onKeyboardOpen(randomId);
		randomId = (int) (Math.random() * Integer.MAX_VALUE);
	}

	private void addSlots(Inventory player) {
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new InvisibleSlot(player, i));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		return te == null ? true : te.menuStillValid(player);
	}

	@Override
	public void removed(Player p_38940_) {
		super.removed(p_38940_);
		if (te != null)
			te.onKeyboardClosed(randomId);
	}

	@Override
	public void receive(CompoundTag tag) {
		switch (tag.getString("action")) {
		case "paste":
		{
			String text = tag.getString("v");
			queueEvent("paste", text);
		}
		break;

		case "keyDown":
		{
			int key = tag.getInt("key");
			boolean rep = tag.getBoolean("r");
			queueEvent("key", key, rep);
		}
		break;

		case "keyUp":
		{
			int key = tag.getInt("key");
			queueEvent("key_up", key);
		}
		break;

		case "char":
		{
			short sh = tag.getShort("char");
			queueEvent("char", Character.toString(sh));
		}
		break;

		case "event":
			queueEvent(tag.getString("name"));
			break;

		case "clickCC":
		{
			int wx = tag.getInt("wx");
			int wy = tag.getInt("wy");
			int wz = tag.getInt("wz");
			double x = tag.getDouble("x");
			double y = tag.getDouble("y");
			double z = tag.getDouble("z");
			byte d = tag.getByte("d");
			Direction dir = Direction.values()[Math.abs(d) % 6];
			BlockPos pos = new BlockPos(wx, wy, wz);
			BlockHitResult hit = new BlockHitResult(new Vec3(x, y, z), dir, pos, false);
			if (te.getLevel().isLoaded(pos) && te.getBlockPos().distSqr(pos) < 64 * 64) {
				BlockState b = te.getLevel().getBlockState(pos);
				if (b.getBlock() instanceof MonitorBlock) {
					b.useWithoutItem(te.getLevel(), inv.player, hit);
				}
			}
		}
		break;

		case "monEvent":
		{
			String ev = tag.getString("event");
			int wx = tag.getInt("wx");
			int wy = tag.getInt("wy");
			int wz = tag.getInt("wz");
			int x = tag.getInt("x");
			int y = tag.getInt("y");
			BlockPos pos = new BlockPos(wx, wy, wz);
			Integer param = tag.contains("param") ? tag.getInt("param") : null;
			if (te.getLevel().isLoaded(pos) && te.getBlockPos().distSqr(pos) < 64 * 64 && te.getLevel().getBlockEntity(pos) instanceof MonitorBlockEntity be) {
				be.event(ev, x, y, param);
			}
		}
		break;

		default:
			break;
		}
	}

	private void queueEvent(String event, Object... args) {
		te.queueEvent(event, args);
	}
}
