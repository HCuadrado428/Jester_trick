package com.jester.jestertricks.event;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.ability.JesterPassiveSettings;
import com.jester.jestertricks.ability.JesterPassiveSettings.Passive;
import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.network.PassivesSyncPacket;
import com.jester.jestertricks.registry.ModParticles;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Ventajas fijas del Jester (solo los jugadores de la configuración).
 * Cada una se puede encender o apagar en la pantalla de pasivas (tecla J o botón del inventario):
 * - Sin daño de caída: aterriza con confeti.
 * - Visión nocturna, como en su mundo de origen.
 * - Risa y confeti con estrellas cada vez que acaba con algo.
 */
@Mod.EventBusSubscriber(modid = JesterTricks.MOD_ID)
public final class JesterPassives {

    private static boolean active(Player p, Passive passive) {
        return JesterLock.isJester(p) && JesterPassiveSettings.isOn(p, passive);
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player p && active(p, Passive.NO_FALL)) {
            if (event.getDistance() > 3.5F && p.level() instanceof ServerLevel sl) {
                Fx.confetti(sl, p.position().add(0, 0.3, 0), 8, 0.6);
            }
            event.setDistance(0.0F);
            event.setDamageMultiplier(0.0F);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (event.player.tickCount % 40 == 0) refresh(event.player);
    }

    /** Pone o quita la visión nocturna según el ajuste actual. */
    public static void refresh(Player p) {
        MobEffectInstance nv = p.getEffect(MobEffects.NIGHT_VISION);
        if (active(p, Passive.NIGHT_VISION)) {
            // se renueva sola y sin el parpadeo de los últimos segundos
            if (nv == null || nv.getDuration() < 260) {
                p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false, false));
            }
        } else if (JesterLock.isJester(p) && nv != null && nv.isAmbient() && !nv.isVisible()) {
            // solo quitamos la nuestra, no una poción que se haya bebido
            p.removeEffect(MobEffects.NIGHT_VISION);
        }
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer jester) || !active(jester, Passive.KILL_LAUGH)) return;
        LivingEntity dead = event.getEntity();
        if (dead == jester || !(dead.level() instanceof ServerLevel sl)) return;
        Vec3 c = dead.position().add(0, dead.getBbHeight() * 0.6, 0);
        Fx.confetti(sl, c, 10, 0.5);
        sl.sendParticles(ModParticles.ORIGIN_STAR.get(), c.x, c.y + 0.4, c.z, 4, 0.4, 0.4, 0.4, 0.02);
        sl.playSound(null, jester.getX(), jester.getY(), jester.getZ(), ModSounds.JESTER_LAUGH.get(),
                SoundSource.PLAYERS, 0.7F, 1.0F + sl.random.nextFloat() * 0.3F);
    }

    // el cliente necesita saber si es el Jester (para el botón) y cómo tiene sus pasivas
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) PassivesSyncPacket.sendTo(sp);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) PassivesSyncPacket.sendTo(sp);
    }

    /** Quien empieza a ver al Jester a mitad de su forma real también la ve. */
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer jester && event.getEntity() instanceof ServerPlayer viewer) {
            com.jester.jestertricks.network.TrueFormPacket.sendTo(viewer, jester);
        }
    }

    private JesterPassives() {}
}
