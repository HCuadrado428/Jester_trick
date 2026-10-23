package com.jester.jestertricks.client;

import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.entity.ClownBalloonEntity;
import com.jester.jestertricks.registry.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

/** Globo 3D que se balancea y la cuerdecita que lo une a la cabeza del atado. */
public class ClownBalloonRenderer extends EntityRenderer<ClownBalloonEntity> {
    private static final ResourceLocation STRING = JesterTricks.id("textures/entity/balloon_string.png");
    private final ItemRenderer itemRenderer;
    private ItemStack balloon;

    public ClownBalloonRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public boolean shouldRender(ClownBalloonEntity e, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public void render(ClownBalloonEntity e, float yaw, float pt, PoseStack pose, MultiBufferSource buffers, int light) {
        if (balloon == null) balloon = new ItemStack(ModItems.CLOWN_BALLOON.get());
        float t = e.tickCount + pt;
        pose.pushPose();
        pose.translate(0.0, 0.2, 0.0);
        pose.mulPose(Axis.YP.rotationDegrees(t * 3.0F));
        pose.mulPose(Axis.ZP.rotationDegrees(Mth.sin(t * 0.15F) * 8.0F));
        pose.scale(1.6F, 1.6F, 1.6F);
        itemRenderer.renderStatic(balloon, ItemDisplayContext.NONE, light, OverlayTexture.NO_OVERLAY, pose, buffers, e.level(), e.getId());
        pose.popPose();

        Entity target = e.getTarget();
        if (target != null) {
            Vec3 me = new Vec3(Mth.lerp(pt, e.xo, e.getX()), Mth.lerp(pt, e.yo, e.getY()), Mth.lerp(pt, e.zo, e.getZ()));
            Vec3 head = target.getPosition(pt).add(0, target.getBbHeight() + 0.05, 0);
            Vec3 a = new Vec3(0, -0.45, 0);
            Vec3 b = head.subtract(me);
            Vec3 dir = b.subtract(a).normalize();
            Vec3 side = dir.cross(new Vec3(0, 1, 0));
            if (side.lengthSqr() < 1.0E-4) side = new Vec3(1, 0, 0);
            side = side.normalize().scale(0.02);
            Vec3 side2 = dir.cross(side).normalize().scale(0.02);
            VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(STRING));
            ScarfHookRenderer.strip(vc, pose.last(), a, b, side, 0.0F, 1.0F, light);
            ScarfHookRenderer.strip(vc, pose.last(), a, b, side2, 0.0F, 1.0F, light);
        }
        super.render(e, yaw, pt, pose, buffers, light);
    }

    @Override
    public ResourceLocation getTextureLocation(ClownBalloonEntity e) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
