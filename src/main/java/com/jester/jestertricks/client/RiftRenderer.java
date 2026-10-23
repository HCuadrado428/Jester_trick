package com.jester.jestertricks.client;

import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.entity.RiftEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * La grieta de la Estrella del Origen en 3D: un anillo dentado que gira y crece,
 * otro detrás girando al revés, y la estrella del origen latiendo en el centro.
 * Brilla en la oscuridad y se ve por las dos caras.
 */
public class RiftRenderer extends EntityRenderer<RiftEntity> {
    private static final ResourceLocation RING = JesterTricks.id("textures/entity/rift_ring.png");
    private static final ResourceLocation CORE = JesterTricks.id("textures/entity/rift_core.png");

    public RiftRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(RiftEntity rift, float yaw, float pt, PoseStack pose, MultiBufferSource buffers, int light) {
        float age = rift.tickCount + pt;
        float grow = Math.min(1.0F, 0.15F + age * 0.09F);
        float closing = rift.tickCount > 32 ? Math.max(0.0F, 1.0F - (age - 32.0F) * 0.15F) : 1.0F;
        float size = grow * closing;
        if (size <= 0.01F) return;

        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(pt, rift.yRotO, rift.getYRot())));
        pose.scale(size, size * 1.35F, size);

        // anillo trasero, más grande y girando al revés
        pose.pushPose();
        pose.translate(0, 0, -0.04);
        pose.mulPose(Axis.ZP.rotationDegrees(-age * 4.0F));
        quad(buffers.getBuffer(RenderType.entityCutoutNoCull(RING)), pose, 1.15F);
        pose.popPose();

        // anillo delantero
        pose.pushPose();
        pose.mulPose(Axis.ZP.rotationDegrees(age * 7.0F));
        quad(buffers.getBuffer(RenderType.entityCutoutNoCull(RING)), pose, 1.0F);
        pose.popPose();

        // estrella del origen, latiendo
        pose.pushPose();
        pose.translate(0, 0, 0.04);
        float beat = 0.55F + 0.06F * Mth.sin(age * 0.6F);
        pose.mulPose(Axis.ZP.rotationDegrees(Mth.sin(age * 0.15F) * 12.0F));
        quad(buffers.getBuffer(RenderType.entityCutoutNoCull(CORE)), pose, beat);
        pose.popPose();

        pose.popPose();
        super.render(rift, yaw, pt, pose, buffers, light);
    }

    private static void quad(VertexConsumer vc, PoseStack pose, float half) {
        Matrix4f m = pose.last().pose();
        Matrix3f n = pose.last().normal();
        int light = LightTexture.FULL_BRIGHT;
        vertex(vc, m, n, -half, -half, 0, 1, light);
        vertex(vc, m, n, half, -half, 1, 1, light);
        vertex(vc, m, n, half, half, 1, 0, light);
        vertex(vc, m, n, -half, half, 0, 0, light);
    }

    private static void vertex(VertexConsumer vc, Matrix4f m, Matrix3f n, float x, float y, float u, float v, int light) {
        vc.vertex(m, x, y, 0.0F).color(255, 255, 255, 255).uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0.0F, 0.0F, 1.0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(RiftEntity rift) {
        return RING;
    }
}
