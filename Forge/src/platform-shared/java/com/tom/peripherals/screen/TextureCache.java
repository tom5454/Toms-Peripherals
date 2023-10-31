package com.tom.peripherals.screen;

import net.minecraft.resources.ResourceLocation;

public interface TextureCache {
	void invalidate();
	void cleanup();
	ResourceLocation getTexture();
}
