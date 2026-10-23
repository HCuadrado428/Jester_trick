package com.jester.jestertricks.client;

import com.jester.jestertricks.entity.PocketStatueEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/** Dibuja en gigante el objeto o la criatura de la estatua, brillando en la oscuridad. */
public class PocketStatueRenderer extends EntityRenderer<PocketStatueEntity> {
    private final ItemRenderer itemRenderer;

    public PocketStatueRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.shadowRadius = 0.0F;
    }

    @Override
    public boolean shouldRender(PocketStatueEntity e, Frustum frustum, double x, double y, double z) {
        return true;   // es enorme: no la ocultes aunque su punto de apoyo quede fuera de la vista
    }

    @Override
    public void render(PocketStatueEntity statue, float yaw, float pt, PoseStack pose, MultiBufferSource buffers, int light) {
        float scale = statue.getScale();
        int bright = LightTexture.FULL_BRIGHT;
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(-statue.getYRot()));

        ItemStack stack = statue.getItem();
        if (!stack.isEmpty()) {
            pose.translate(0.0, scale * 0.26, 0.0);
            pose.scale(scale, scale, scale);
            itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, bright, OverlayTexture.NO_OVERLAY,
                    pose, buffers, statue.level(), statue.getId());
        } else {
            Entity mob = mob(statue);
            if (mob != null) {
                pose.scale(scale, scale, scale);
                EntityRenderDispatcher d = Minecraft.getInstance().getEntityRenderDispatcher();
                try {
                    d.render(mob, 0.0, 0.0, 0.0, 0.0F, 0.0F, pose, buffers, bright);
                } catch (RuntimeException ignored) {
                    statue.cachedMob = null;
                }
            }
        }
        pose.popPose();
        super.render(statue, yaw, pt, pose, buffers, light);
    }

    private static Entity mob(PocketStatueEntity statue) {
        if (statue.cachedMob == null) {
            CompoundTag tag = statue.getMob();
            if (tag.isEmpty()) return null;
            statue.cachedMob = EntityType.create(tag, statue.level()).orElse(null);
            if (statue.cachedMob != null) {
                statue.cachedMob.setPos(statue.getX(), statue.getY(), statue.getZ());
                statue.cachedMob.setYRot(0.0F);
                statue.cachedMob.yRotO = 0.0F;
                if (statue.cachedMob instanceof LivingEntity le) {
                    le.yBodyRot = 0.0F;
                    le.yBodyRotO = 0.0F;
                    le.yHeadRot = 0.0F;
                    le.yHeadRotO = 0.0F;
                    le.hurtTime = 0;
                    le.deathTime = 0;
                }
            }
        }
        return statue.cachedMob;
    }

    @Override
    public ResourceLocation getTextureLocation(PocketStatueEntity statue) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
