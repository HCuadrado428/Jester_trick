package com.jester.jestertricks.client;

import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.entity.HarlequinDollEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Muñeco arlequín: un Jester en miniatura (media altura). */
public class HarlequinDollRenderer extends HumanoidMobRenderer<HarlequinDollEntity, HumanoidModel<HarlequinDollEntity>> {
    private static final ResourceLocation SKIN = JesterTricks.id("textures/entity/harlequin_doll.png");

    public HarlequinDollRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.25F);
    }

    @Override
    protected void scale(HarlequinDollEntity doll, PoseStack pose, float partialTick) {
        pose.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(HarlequinDollEntity doll) {
        return SKIN;
    }
}
