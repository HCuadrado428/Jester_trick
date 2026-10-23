package com.jester.jestertricks.pocket;

import com.jester.jestertricks.JesterTricks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Reglas del bolsillo: dentro nadie recibe daño, y los atrapados no pueden romper,
 * poner, usar objetos ni golpear (solo mirar y caminar). El Jester sí puede usar su Caja.
 */
@Mod.EventBusSubscriber(modid = JesterTricks.MOD_ID)
public final class PocketEvents {

    private static boolean trapped(Player p) {
        if (p == null || !PocketDimension.isPocket(p.level())) return false;
        if (p.level().isClientSide) return true;   // el cliente no sabe quién es Jester: lo decide el servidor
        return !(p instanceof ServerPlayer sp) || !PocketManager.isJesterInside(sp.server, sp.getUUID());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.getServer().getTickCount() % 20 == 0) {
            PocketManager.tick(event.getServer());
        }
    }

    @SubscribeEvent
    public static void onAttacked(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player p && PocketDimension.isPocket(p.level())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        if (trapped(event.getPlayer())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof Player p && trapped(p)) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (trappedServer(event.getEntity())) {
            event.setCanceled(true);
            event.setUseItem(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (trappedServer(event.getEntity())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (trappedServer(event.getEntity())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (trappedServer(event.getEntity())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        if (trappedServer(event.getEntity())) event.setCanceled(true);
    }

    /** En las interacciones solo decide el servidor, para que el Jester pueda usar su Caja dentro. */
    private static boolean trappedServer(Player p) {
        return !p.level().isClientSide && trapped(p);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp && PocketDimension.isPocket(sp.level())) {
            if (PocketData.get(sp.server).session(sp.getUUID()) == null) PocketManager.sendToSpawn(sp);
            else PocketManager.syncJesters(sp.server);
        }
    }

    /** Si sale del bolsillo por otro camino (comando, muerte...), se olvida su visita. */
    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getFrom() == PocketDimension.POCKET && event.getEntity() instanceof ServerPlayer sp) {
            PocketData.get(sp.server).removeSession(sp.getUUID());
            PocketManager.syncJesters(sp.server);
        } else if (event.getTo() == PocketDimension.POCKET && event.getEntity() instanceof ServerPlayer sp) {
            PocketManager.syncJesters(sp.server);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp && !PocketDimension.isPocket(sp.level())) {
            PocketData.get(sp.server).removeSession(sp.getUUID());
        }
    }

    private PocketEvents() {}
}
