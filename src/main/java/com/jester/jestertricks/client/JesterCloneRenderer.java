package com.jester.jestertricks.client;

import com.jester.jestertricks.entity.JesterCloneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/**
 * Dibuja las copias exactamente como al Jester de verdad: se usa el propio dibujado del jugador
 * (con su skin, su modelo personalizado de CPM/Figura si lo tiene y lo que lleve en la mano),
 * colocado en la posición y postura de la copia. Si el Jester no está cerca, se usa su skin normal.
 */
public class JesterCloneRenderer extends EntityRenderer<JesterCloneEntity> {
    private final Fallback wide;
    private final Fallback slim;

    public JesterCloneRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.5F;
        this.wide = new Fallback(ctx, false);
        this.slim = new Fallback(ctx, true);
    }

    @Override
    public void render(JesterCloneEntity clone, float yaw, float partial, PoseStack pose, MultiBufferSource buffers, int light) {
        UUID owner = clone.getOwnerId().orElse(clone.getUUID());
        Minecraft mc = Minecraft.getInstance();
        Player p = mc.level == null ? null : mc.level.getPlayerByUUID(owner);
        if (p instanceof AbstractClientPlayer jester && renderAsJester(clone, jester, partial, pose, buffers, light)) return;
        boolean isSlim = p instanceof AbstractClientPlayer acp ? "slim".equals(acp.getModelName())
                : "slim".equals(DefaultPlayerSkin.getSkinModelName(owner));
        (isSlim ? slim : wide).render(clone, yaw, partial, pose, buffers, light);
    }

    /** Pone al jugador un instante en la postura de la copia, lo dibuja ahí y lo deja como estaba. */
    private boolean renderAsJester(JesterCloneEntity clone, AbstractClientPlayer jester, float partial, PoseStack pose,
                                   MultiBufferSource buffers, int light) {
        EntityRenderer<? super AbstractClientPlayer> r = entityRenderDispatcher.getRenderer(jester);
        if (r == null) return false;

        float bodyRot = jester.yBodyRot, bodyRotO = jester.yBodyRotO;
        float headRot = jester.yHeadRot, headRotO = jester.yHeadRotO;
        float yRot = jester.getYRot(), yRotO = jester.yRotO;
        float xRot = jester.getXRot(), xRotO = jester.xRotO;
        boolean invisible = jester.isInvisible();
        float[] walk = saveWalk(jester.walkAnimation);
        try {
            jester.yBodyRot = clone.yBodyRot;
            jester.yBodyRotO = clone.yBodyRotO;
            jester.yHeadRot = clone.yHeadRot;
            jester.yHeadRotO = clone.yHeadRotO;
            jester.setYRot(clone.getYRot());
            jester.yRotO = clone.yRotO;
            jester.setXRot(clone.getXRot());
            jester.xRotO = clone.xRotO;
            if (invisible) jester.setInvisible(false);
            loadWalk(jester.walkAnimation, saveWalk(clone.walkAnimation));
            float yaw = net.minecraft.util.Mth.lerp(partial, clone.yRotO, clone.getYRot());
            r.render(jester, yaw, partial, pose, buffers, light);
        } finally {
            jester.yBodyRot = bodyRot;
            jester.yBodyRotO = bodyRotO;
            jester.yHeadRot = headRot;
            jester.yHeadRotO = headRotO;
            jester.setYRot(yRot);
            jester.yRotO = yRotO;
            jester.setXRot(xRot);
            jester.xRotO = xRotO;
            if (invisible) jester.setInvisible(true);
            loadWalk(jester.walkAnimation, walk);
        }
        return true;
    }

    /** {speedOld, speed, position} */
    private static float[] saveWalk(WalkAnimationState w) {
        return new float[]{w.speed(0.0F), w.speed(), w.position()};
    }

    /** WalkAnimationState no tiene "set": se llega al estado deseado con setSpeed + update(…, 0). */
    private static void loadWalk(WalkAnimationState w, float[] s) {
        float speedOld = s[0], speed = s[1], position = s[2];
        w.setSpeed(position - speedOld - w.position());
        w.update(0.0F, 0.0F);            // speedOld = speed; position += speed  -> position = target - speedOld
        w.setSpeed(speedOld);
        w.update(0.0F, 0.0F);            // speedOld = speed = target speedOld; position = target
        w.setSpeed(speed);
    }

    @Override
    public ResourceLocation getTextureLocation(JesterCloneEntity clone) {
        return DefaultPlayerSkin.getDefaultSkin(clone.getOwnerId().orElse(clone.getUUID()));
    }

    /** Dibujado de reserva con la skin del Jester sobre el cuerpo normal de jugador. */
    private static final class Fallback extends MobRenderer<JesterCloneEntity, PlayerModel<JesterCloneEntity>> {
        Fallback(EntityRendererProvider.Context ctx, boolean slim) {
            super(ctx, new PlayerModel<>(ctx.bakeLayer(slim ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), slim), 0.0F);
        }

        @Override
        public ResourceLocation getTextureLocation(JesterCloneEntity clone) {
            UUID owner = clone.getOwnerId().orElse(clone.getUUID());
            Minecraft mc = Minecraft.getInstance();
            Player p = mc.level == null ? null : mc.level.getPlayerByUUID(owner);
            if (p instanceof AbstractClientPlayer acp) return acp.getSkinTextureLocation();
            return DefaultPlayerSkin.getDefaultSkin(owner);
        }

        @Override
        protected boolean shouldShowName(JesterCloneEntity clone) {
            return false;
        }
    }
}
