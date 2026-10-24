package com.jester.jestertricks.client;

import com.jester.jestertricks.JesterTricks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

/** Quién está en forma real y cómo se dibuja (cambia el aspecto del jugador por la sombra alta). */
@Mod.EventBusSubscriber(modid = JesterTricks.MOD_ID, value = Dist.CLIENT)
public final class ClientTrueForm {
    private static final Map<Integer, Long> UNTIL = new HashMap<>();
    private static TrueFormRenderer renderer;
    private static TrueFormRenderer slimRenderer;
    private static boolean rendering;

    /** Se llama al recargar los recursos (desde el registro de renderizadores). */
    public static void init(EntityRendererProvider.Context ctx) {
        renderer = new TrueFormRenderer(ctx, false);
        slimRenderer = new TrueFormRenderer(ctx, true);
    }

    public static void set(int entityId, int ticks) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        if (ticks <= 0) UNTIL.remove(entityId);
        else UNTIL.put(entityId, level.getGameTime() + ticks);
    }

    private static boolean active(int entityId) {
        Long until = UNTIL.get(entityId);
        var level = Minecraft.getInstance().level;
        if (until == null || level == null) return false;
        if (level.getGameTime() > until) {
            UNTIL.remove(entityId);
            return false;
        }
        return true;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (rendering || renderer == null || !active(event.getEntity().getId())) return;
        event.setCanceled(true);
        rendering = true;
        try {
            if (!(event.getEntity() instanceof net.minecraft.client.player.AbstractClientPlayer p)) return;
            float yaw = net.minecraft.util.Mth.lerp(event.getPartialTick(), p.yRotO, p.getYRot());
            ("slim".equals(p.getModelName()) ? slimRenderer : renderer).render(p, yaw, event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
        } finally {
            rendering = false;
        }
    }

    private ClientTrueForm() {}
}
