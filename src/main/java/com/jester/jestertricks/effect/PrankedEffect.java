package com.jester.jestertricks.effect;

import com.jester.jestertricks.item.Fx;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * Pranked!: mientras está activo, la armadura del afectado pierde sus encantamientos
 * (Protección, Protección contra explosiones/fuego/proyectiles, Caída de pluma y Espinas)
 * y le rodea una nubecita de confeti blanco y negro.
 * La lógica de la armadura está en {@link com.jester.jestertricks.event.JesterEvents}.
 */
public class PrankedEffect extends MobEffect {
    public PrankedEffect() {
        super(MobEffectCategory.HARMFUL, 0xD9D9D9);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 4 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity e, int amplifier) {
        if (e.level() instanceof ServerLevel sl) {
            double r = e.getBbWidth() * 0.9;
            double y = e.getY() + e.getBbHeight() + 0.25;
            sl.sendParticles(Fx.WHITE, e.getX(), y, e.getZ(), 2, r, 0.15, r, 0.0);
            sl.sendParticles(Fx.BLACK, e.getX(), y, e.getZ(), 2, r, 0.15, r, 0.0);
            sl.sendParticles(Fx.GREY, e.getX(), e.getY() + e.getBbHeight() * 0.5, e.getZ(), 1, r, e.getBbHeight() * 0.4, r, 0.0);
        }
    }
}
