package com.jester.jestertricks.client;

import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.registry.ModEntities;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Los proyectiles se dibujan con sus modelos 3D (models/item/*_3d.json). */
@Mod.EventBusSubscriber(modid = JesterTricks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.THROWN_CARD.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.THROWN_KNIFE.get(), ctx -> new ThrownItemRenderer<>(ctx, 1.25F, false));
        event.registerEntityRenderer(ModEntities.BANG_FLAG.get(), ctx -> new ThrownItemRenderer<>(ctx, 1.2F, false));
        event.registerEntityRenderer(ModEntities.SCARF_HOOK.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.RIFT.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.JESTER_CLONE.get(), JesterCloneRenderer::new);
    }

    private ClientSetup() {}
}
