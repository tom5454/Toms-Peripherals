package com.tom.peripherals.client;

import net.minecraft.network.chat.Component;

public interface MonitorHandler {
	void mouseClick(int btn);
	void mouseRelease(int btn);
	void mouseScroll(int dir);
	void onOffScreen();
	Component infoComponent();
}
