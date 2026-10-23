package com.jester.jestertricks.client;

import com.jester.jestertricks.JesterTricks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;

/** El Jester en su forma real: una figura negra, alta y delgada con la máscara blanca. */
public class TrueFormRenderer extends PlayerRenderer {
    private static final ResourceLocation SKIN = JesterTricks.id("textures/entity/jester_true_form.png");

    public TrueFormRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, false);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractClientPlayer player) {
        return SKIN;
    }

    @Override
    protected void scale(AbstractClientPlayer player, PoseStack pose, float partialTick) {
        float base = 0.9375F;
        pose.scale(base * 0.82F, base * 1.65F, base * 0.82F);
    }
}
