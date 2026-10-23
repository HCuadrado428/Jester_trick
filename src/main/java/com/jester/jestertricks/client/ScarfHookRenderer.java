package com.jester.jestertricks.client;

import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.entity.ScarfHookEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Nudo de pañuelos + la cadena de pañuelos anudados que lo une a la mano del Jester.
 * La cadena se dibuja como dos tiras cruzadas con textura, para verse desde cualquier ángulo.
 */
public class ScarfHookRenderer extends OrientedItemRenderer<ScarfHookEntity> {
    private static final ResourceLocation CHAIN = JesterTricks.id("textures/entity/scarf_chain.png");
    private static final float WIDTH = 0.22F;
    /** Bloques de cadena que ocupa una repetición de la textura. */
    private static final float TILE = 1.0F;

    public ScarfHookRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, 0.8F, 0.0F, Spin.TUMBLE);
    }

    @Override
    public boolean shouldRender(ScarfHookEntity hook, Frustum frustum, double camX, double camY, double camZ) {
        return hook.getOwner() != null || super.shouldRender(hook, frustum, camX, camY, camZ);
    }

    @Override
    public void render(ScarfHookEntity hook, float yaw, float pt, PoseStack pose, MultiBufferSource buffers, int light) {
        super.render(hook, yaw, pt, pose, buffers, light);
        Entity owner = hook.getOwner();
        if (!(owner instanceof LivingEntity user)) return;

        Vec3 hookPos = new Vec3(Mth.lerp(pt, hook.xo, hook.getX()), Mth.lerp(pt, hook.yo, hook.getY()), Mth.lerp(pt, hook.zo, hook.getZ()));
        Vec3 delta = handPos(user, pt).subtract(hookPos);
        float length = (float) delta.length();
        if (length < 0.05F) return;

        Vec3 dir = delta.normalize();
        Vec3 side = dir.cross(new Vec3(0, 1, 0));
        if (side.lengthSqr() < 1.0E-4) side = new Vec3(1, 0, 0);
        side = side.normalize().scale(WIDTH / 2);
        Vec3 side2 = dir.cross(side).normalize().scale(WIDTH / 2);

        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(CHAIN));
        PoseStack.Pose last = pose.last();
        int segments = Math.max(1, Mth.ceil(length / TILE * 2));
        float sag = Math.min(0.6F, length * 0.04F);
        for (int i = 0; i < segments; i++) {
            float t0 = i / (float) segments, t1 = (i + 1) / (float) segments;
            Vec3 a = delta.scale(t0).add(0, -sag * Mth.sin(t0 * Mth.PI), 0);
            Vec3 b = delta.scale(t1).add(0, -sag * Mth.sin(t1 * Mth.PI), 0);
            float u0 = (t0 * length / TILE) % 1.0F;
            float u1 = u0 + (t1 - t0) * length / TILE;
            strip(vc, last, a, b, side, u0, u1, light);
            strip(vc, last, a, b, side2, u0, u1, light);
        }
    }

    /** Punto de la mano derecha del Jester (en primera persona, delante de la cámara). */
    private Vec3 handPos(LivingEntity user, float pt) {
        Minecraft mc = Minecraft.getInstance();
        float yawDeg = Mth.lerp(pt, user.yBodyRotO, user.yBodyRot);
        if (user == mc.getCameraEntity() && mc.options.getCameraType().isFirstPerson()) {
            yawDeg = Mth.lerp(pt, user.yRotO, user.getYRot());
            Vec3 eye = user.getEyePosition(pt);
            Vec3 look = user.getViewVector(pt);
            double r = Math.toRadians(yawDeg);
            Vec3 right = new Vec3(-Math.cos(r), 0, -Math.sin(r));
            return eye.add(look.scale(0.6)).add(right.scale(0.35)).add(0, -0.35, 0);
        }
        double r = Math.toRadians(yawDeg);
        Vec3 right = new Vec3(-Math.cos(r), 0, -Math.sin(r));
        Vec3 fwd = new Vec3(-Math.sin(r), 0, Math.cos(r));
        Vec3 base = user.getPosition(pt);
        return base.add(0, user.getBbHeight() * 0.55, 0).add(right.scale(0.4)).add(fwd.scale(0.25));
    }

    static void strip(VertexConsumer vc, PoseStack.Pose last, Vec3 a, Vec3 b, Vec3 side,
                              float u0, float u1, int light) {
        Matrix4f m = last.pose();
        Matrix3f n = last.normal();
        // u se repite a lo largo de la cadena; si el tramo cruza el borde de la textura, se parte en dos
        if (u1 > 1.0F) {
            float split = (1.0F - u0) / (u1 - u0);
            Vec3 mid = a.add(b.subtract(a).scale(split));
            strip(vc, last, a, mid, side, u0, 1.0F, light);
            strip(vc, last, mid, b, side, 0.0F, u1 - 1.0F, light);
            return;
        }
        vertex(vc, m, n, a.add(side), u0, 0.0F, light);
        vertex(vc, m, n, b.add(side), u1, 0.0F, light);
        vertex(vc, m, n, b.subtract(side), u1, 1.0F, light);
        vertex(vc, m, n, a.subtract(side), u0, 1.0F, light);
    }

    private static void vertex(VertexConsumer vc, Matrix4f m, Matrix3f n, Vec3 p, float u, float v, int light) {
        vc.vertex(m, (float) p.x, (float) p.y, (float) p.z).color(255, 255, 255, 255).uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(n, 0.0F, 1.0F, 0.0F).endVertex();
    }
}
