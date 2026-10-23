package com.jester.jestertricks.client;

import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.registry.ModEntities;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Los proyectiles se dibujan con sus modelos 3D (models/item/*_3d.json). */
@Mod.EventBusSubscriber(modid = JesterTricks.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // los proyectiles apuntan hacia donde vuelan (como las flechas)
        event.registerEntityRenderer(ModEntities.THROWN_CARD.get(),
                ctx -> new OrientedItemRenderer<>(ctx, 0.9F, 0.0F, OrientedItemRenderer.Spin.FLAT));
        event.registerEntityRenderer(ModEntities.THROWN_KNIFE.get(),
                ctx -> new OrientedItemRenderer<>(ctx, 1.0F, -90.0F, OrientedItemRenderer.Spin.NONE));
        event.registerEntityRenderer(ModEntities.BANG_FLAG.get(),
                ctx -> new OrientedItemRenderer<>(ctx, 1.0F, 0.0F, OrientedItemRenderer.Spin.NONE));
        event.registerEntityRenderer(ModEntities.SCARF_HOOK.get(), ScarfHookRenderer::new);
        event.registerEntityRenderer(ModEntities.RIFT.get(), NoopRenderer::new);
        event.registerEntityRenderer(ModEntities.JESTER_CLONE.get(), JesterCloneRenderer::new);
    }

    private ClientSetup() {}
}
