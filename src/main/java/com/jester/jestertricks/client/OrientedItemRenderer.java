package com.jester.jestertricks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Dibuja el modelo 3D del proyectil apuntando hacia donde vuela (como una flecha),
 * en lugar de girarlo siempre hacia la cámara.
 * El modelo debe apuntar hacia +X; {@code modelToX} corrige modelos hechos en vertical.
 */
public class OrientedItemRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T> {
    public enum Spin { NONE, FLAT, TUMBLE }

    private final ItemRenderer itemRenderer;
    private final float scale;
    private final float modelToX;
    private final Spin spin;

    public OrientedItemRenderer(EntityRendererProvider.Context ctx, float scale, float modelToX, Spin spin) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.scale = scale;
        this.modelToX = modelToX;
        this.spin = spin;
    }

    @Override
    public void render(T entity, float yaw, float partialTicks, PoseStack pose, MultiBufferSource buffers, int light) {
        // igual que el renderizador vanilla: no tapar la vista justo al lanzarlo
        if (entity.tickCount < 2 && this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25) {
            return;
        }
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        pose.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        float age = entity.tickCount + partialTicks;
        if (spin == Spin.FLAT) {
            pose.mulPose(Axis.YP.rotationDegrees(age * 40.0F));
        } else if (spin == Spin.TUMBLE) {
            pose.mulPose(Axis.ZP.rotationDegrees(-age * 30.0F));
        }
        if (modelToX != 0.0F) {
            pose.mulPose(Axis.ZP.rotationDegrees(modelToX));
        }
        pose.scale(scale, scale, scale);
        itemRenderer.renderStatic(entity.getItem(), ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY,
                pose, buffers, entity.level(), entity.getId());
        pose.popPose();
        super.render(entity, yaw, partialTicks, pose, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
