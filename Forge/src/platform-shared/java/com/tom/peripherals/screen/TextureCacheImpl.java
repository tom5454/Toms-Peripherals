package com.tom.peripherals.screen;

import java.lang.reflect.Field;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;

import com.tom.peripherals.PeripheralsMod;
import com.tom.peripherals.block.entity.MonitorBlockEntity;

public class TextureCacheImpl implements TextureCache {
	private final DynamicTexture dynTex;
	private final ResourceLocation loc;
	private NativeImage image;
	private IntBuffer buffer;

	private MonitorBlockEntity be;
	private boolean needsUpdate;

	public TextureCacheImpl(MonitorBlockEntity be) {
		this.be = be;
		this.needsUpdate = true;
		dynTex = new DynamicTexture(16, 16, true);
		loc = Minecraft.getInstance().getTextureManager().register(PeripheralsMod.ID, dynTex);
	}

	@Override
	public void invalidate() {
		needsUpdate = true;
	}

	@Override
	public void cleanup() {
		be = null;
		Minecraft.getInstance().getTextureManager().release(loc);
	}

	@Override
	public ResourceLocation getTexture() {
		if (be == null || be.screen == null)return null;

		Minecraft mc = Minecraft.getInstance();
		if(mc.getTextureManager().getTexture(loc) == null)
			mc.getTextureManager().register(loc, dynTex);
		if (needsUpdate) {
			load(be.width, be.screen);
			needsUpdate = false;
		}
		return loc;
	}

	private void load(int w, int[] img) {
		int h = img.length / w;
		if (img.length != w * h) {
			System.err.println("Attempting to load an invalid texture");
			return;
		}

		if (image == null) {
			image = new NativeImage(w, h, false);
			getBuffer();
		} else if(image.getWidth() != w || image.getHeight() != h) {
			image.close();
			image = new NativeImage(w, h, false);
			getBuffer();
		}
		buffer.rewind();
		buffer.put(img);
		dynTex.upload();
	}

	//TODO: use ATs
	private void getBuffer() {
		dynTex.setPixels(image);
		TextureUtil.prepareImage(dynTex.getId(), image.getWidth(), image.getHeight());
		try {
			Field p = NativeImage.class.getDeclaredField("pixels");
			p.setAccessible(true);
			long ptr = p.getLong(image);
			buffer = MemoryUtil.memIntBuffer(ptr, image.getWidth() * image.getHeight());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
