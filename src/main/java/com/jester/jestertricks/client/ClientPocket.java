package com.jester.jestertricks.client;

import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.pocket.PocketDimension;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Dentro del bolsillo, el Jester se ve gigante para los demás. */
@Mod.EventBusSubscriber(modid = JesterTricks.MOD_ID, value = Dist.CLIENT)
public final class ClientPocket {
    public static final float JESTER_SCALE = 4.0F;
    private static final Set<UUID> JESTERS = new HashSet<>();
    private static boolean pushed;

    public static void setJesters(List<UUID> jesters) {
        JESTERS.clear();
        JESTERS.addAll(jesters);
    }

    @SubscribeEvent
    public static void onRenderPre(RenderPlayerEvent.Pre event) {
        pushed = false;
        var p = event.getEntity();
        if (PocketDimension.isPocket(p.level()) && JESTERS.contains(p.getUUID())) {
            event.getPoseStack().pushPose();
            event.getPoseStack().scale(JESTER_SCALE, JESTER_SCALE, JESTER_SCALE);
            pushed = true;
        }
    }

    @SubscribeEvent
    public static void onRenderPost(RenderPlayerEvent.Post event) {
        if (pushed) {
            event.getPoseStack().popPose();
            pushed = false;
        }
    }

    private ClientPocket() {}
}
