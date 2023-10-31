package com.tom.peripherals.platform;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import com.tom.peripherals.util.ITMPeripheral;

public class PeripheralCapability {
	public static final Capability<ITMPeripheral> PERIPHERAL = get(new CapabilityToken<>(){});
}
