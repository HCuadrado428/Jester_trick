package com.jester.jestertricks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

/** El Jester en su forma real: su propia skin, pero alto y delgado como una sombra. */
public class TrueFormRenderer extends PlayerRenderer {

    public TrueFormRenderer(EntityRendererProvider.Context ctx, boolean slim) {
        super(ctx, slim);
    }

    @Override
    protected void scale(AbstractClientPlayer player, PoseStack pose, float partialTick) {
        float base = 0.9375F;
        pose.scale(base * 0.82F, base * 1.65F, base * 0.82F);
    }
}
