package com.tom.peripherals.client;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import com.tom.peripherals.block.entity.MonitorBlockEntity;
import com.tom.peripherals.screen.TextureCacheImpl;

public class MonitorBlockEntityRenderer implements BlockEntityRenderer<MonitorBlockEntity> {

	public MonitorBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
	}

	@Override
	public void render(MonitorBlockEntity te, float partical, PoseStack stack, MultiBufferSource buffer,
			int pPackedLight, int pPackedOverlay) {
		if (te.screen.length == 0)return;
		if (te.clientCache == null)te.clientCache = new TextureCacheImpl(te);
		ResourceLocation tex = te.clientCache.getTexture();
		if (tex == null)return;
		stack.pushPose();
		Direction facing = te.getDirection();
		stack.translate(0.5d, 0.5d, 0.5d);
		if (facing.getAxis() != Direction.Axis.Y)
			stack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
		else {
			stack.mulPose(Axis.XP.rotationDegrees(-facing.getStepY() * 90));
		}
		stack.translate(-0.5d, -0.5d, -0.5d);
		Matrix4f mat = stack.last().pose();
		Matrix3f nor = stack.last().normal();
		Vector3f n = new Vector3f(facing.getStepX(), facing.getStepY(), facing.getStepZ());
		n.mul(nor);
		VertexConsumer buf = buffer.getBuffer(RenderType.entityTranslucent(tex));

		float z = 1.001F;
		buf.vertex(mat, 1, 1, z).color(1F, 1F, 1F, 1F).uv(1, 0).overlayCoords(pPackedOverlay).uv2(pPackedLight)
		.normal(nor, 0.0F, 0.0F, 1.0F).endVertex();
		buf.vertex(mat, 0, 1, z).color(1F, 1F, 1F, 1F).uv(0, 0).overlayCoords(pPackedOverlay).uv2(pPackedLight)
		.normal(nor, 0.0F, 0.0F, 1.0F).endVertex();
		buf.vertex(mat, 0, 0, z).color(1F, 1F, 1F, 1F).uv(0, 1).overlayCoords(pPackedOverlay).uv2(pPackedLight)
		.normal(nor, 0.0F, 0.0F, 1.0F).endVertex();
		buf.vertex(mat, 1, 0, z).color(1F, 1F, 1F, 1F).uv(1, 1).overlayCoords(pPackedOverlay).uv2(pPackedLight)
		.normal(nor, 0.0F, 0.0F, 1.0F).endVertex();
		stack.popPose();
	}

}
