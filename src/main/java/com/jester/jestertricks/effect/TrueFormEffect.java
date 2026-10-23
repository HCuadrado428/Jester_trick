package com.jester.jestertricks.effect;

import com.jester.jestertricks.registry.ModParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Forma real: el Jester se muestra como lo que es, una sombra alta con la máscara blanca.
 * Mientras dura, los monstruos huyen de él y a los jugadores cercanos se les nubla la vista.
 * (El aspecto lo dibuja el cliente: ver client/ClientTrueForm.)
 */
public class TrueFormEffect extends MobEffect {
    public TrueFormEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x050505);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity jester, int amplifier) {
        if (!(jester.level() instanceof ServerLevel sl)) return;
        Vec3 c = jester.position();
        if (jester.tickCount % 2 == 0) {
            sl.sendParticles(ParticleTypes.SQUID_INK, c.x, c.y + 1.4, c.z, 3, 0.35, 1.2, 0.35, 0.0);
            sl.sendParticles(ParticleTypes.LARGE_SMOKE, c.x, c.y + 0.2, c.z, 2, 0.4, 0.1, 0.4, 0.0);
        }
        if (jester.tickCount % 10 != 0) return;
        sl.sendParticles(ModParticles.ORIGIN_STAR.get(), c.x, c.y + 3.2, c.z, 1, 0.3, 0.2, 0.3, 0.0);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, jester.getBoundingBox().inflate(16.0),
                e -> e != jester && e.isAlive())) {
            if (e instanceof Player p) {
                if (p.distanceToSqr(jester) < 12 * 12) {
                    p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0, false, false), jester);
                }
                continue;
            }
            if (e instanceof Mob mob && mob.getTarget() == jester) mob.setTarget(null);
            if (e instanceof PathfinderMob pm) {
                Vec3 away = e.position().subtract(c);
                away = new Vec3(away.x, 0, away.z);
                away = away.lengthSqr() < 1.0E-4 ? new Vec3(1, 0, 0) : away.normalize();
                Vec3 flee = e.position().add(away.scale(12));
                pm.getNavigation().moveTo(flee.x, flee.y, flee.z, 1.7);
            }
        }
    }
}
