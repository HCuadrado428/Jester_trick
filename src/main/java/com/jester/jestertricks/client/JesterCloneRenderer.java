package com.jester.jestertricks.client;

import com.jester.jestertricks.entity.JesterCloneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

/** Dibuja las copias con la skin real del Jester que las creó (brazos finos o normales según su skin). */
public class JesterCloneRenderer extends MobRenderer<JesterCloneEntity, PlayerModel<JesterCloneEntity>> {
    private final PlayerModel<JesterCloneEntity> wide;
    private final PlayerModel<JesterCloneEntity> slim;

    public JesterCloneRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        this.wide = this.getModel();
        this.slim = new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER_SLIM), true);
    }

    @Override
    public ResourceLocation getTextureLocation(JesterCloneEntity clone) {
        UUID owner = clone.getOwnerId().orElse(clone.getUUID());
        Minecraft mc = Minecraft.getInstance();
        Player p = mc.level == null ? null : mc.level.getPlayerByUUID(owner);
        if (p instanceof AbstractClientPlayer acp) {
            this.model = "slim".equals(acp.getModelName()) ? slim : wide;
            return acp.getSkinTextureLocation();
        }
        this.model = "slim".equals(DefaultPlayerSkin.getSkinModelName(owner)) ? slim : wide;
        return DefaultPlayerSkin.getDefaultSkin(owner);
    }

    @Override
    protected boolean shouldShowName(JesterCloneEntity clone) {
        return false;
    }
}
