package com.jester.jestertricks.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Pranked!: mientras está activo, la armadura del afectado pierde sus encantamientos
 * (Protección, Protección contra explosiones/fuego/proyectiles, Caída de pluma y Espinas).
 * La lógica está en {@link com.jester.jestertricks.event.JesterEvents}.
 */
public class PrankedEffect extends MobEffect {
    public PrankedEffect() {
        super(MobEffectCategory.HARMFUL, 0xD9D9D9);
    }
}
