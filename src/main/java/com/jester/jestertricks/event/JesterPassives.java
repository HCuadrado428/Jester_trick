package com.jester.jestertricks.event;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.item.Fx;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Ventajas fijas del Jester (solo los jugadores de la configuración):
 * - No recibe daño de caída: aterriza con una voltereta y confeti.
 * - Ve en la oscuridad, como en su mundo de origen.
 * - Cada vez que acaba con algo, se ríe y suelta confeti con estrellas.
 */
@Mod.EventBusSubscriber(modid = JesterTricks.MOD_ID)
public final class JesterPassives {

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player p && JesterLock.isJester(p)) {
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
        Player p = event.player;
        if (p.tickCount % 40 != 0 || !JesterLock.isJester(p)) return;
        MobEffectInstance nv = p.getEffect(MobEffects.NIGHT_VISION);
        // se renueva sola y sin el parpadeo de los últimos segundos
        if (nv == null || nv.getDuration() < 260) {
            p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false, false));
        }
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer jester) || !JesterLock.isJester(jester)) return;
        LivingEntity dead = event.getEntity();
        if (dead == jester || !(dead.level() instanceof ServerLevel sl)) return;
        Vec3 c = dead.position().add(0, dead.getBbHeight() * 0.6, 0);
        Fx.confetti(sl, c, 10, 0.5);
        sl.sendParticles(ModParticles.ORIGIN_STAR.get(), c.x, c.y + 0.4, c.z, 4, 0.4, 0.4, 0.4, 0.02);
        sl.playSound(null, jester.getX(), jester.getY(), jester.getZ(), ModSounds.JESTER_LAUGH.get(),
                SoundSource.PLAYERS, 0.7F, 1.0F + sl.random.nextFloat() * 0.3F);
    }

    private JesterPassives() {}
}
