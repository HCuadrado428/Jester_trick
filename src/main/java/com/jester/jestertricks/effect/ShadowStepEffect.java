package com.jester.jestertricks.effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * Paso de sombra: los golpes cuerpo a cuerpo y los proyectiles atraviesan al Jester
 * (ver JesterEvents) y deja un rastro de sombra al moverse.
 */
public class ShadowStepEffect extends MobEffect {
    public ShadowStepEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x151515);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity e, int amplifier) {
        if (e.level() instanceof ServerLevel sl && e.tickCount % 2 == 0) {
            sl.sendParticles(ParticleTypes.SQUID_INK, e.getX(), e.getY() + 0.6, e.getZ(), 2, 0.2, 0.4, 0.2, 0.0);
            sl.sendParticles(ParticleTypes.SMOKE, e.getX(), e.getY() + 0.2, e.getZ(), 2, 0.2, 0.1, 0.2, 0.0);
        }
    }
}
