package com.jester.jestertricks.registry;

import com.jester.jestertricks.JesterTricks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Tipos de daño propios del Jester: cada arma tiene su mensaje de muerte
 * (lang: death.attack.jestertricks.&lt;tipo&gt;[.player|.item]).
 */
public final class ModDamageTypes {
    /** Explosión de la banderita BANG!: atraviesa armadura y encantamientos (ver data/.../tags). */
    public static final ResourceKey<DamageType> BANG = key("bang");
    public static final ResourceKey<DamageType> CARD = key("card");
    public static final ResourceKey<DamageType> KNIFE = key("knife");
    public static final ResourceKey<DamageType> JUGGLING_PIN = key("juggling_pin");
    public static final ResourceKey<DamageType> CREAM_PIE = key("cream_pie");
    public static final ResourceKey<DamageType> SCARF = key("scarf");
    public static final ResourceKey<DamageType> PETALS = key("petals");
    public static final ResourceKey<DamageType> SQUIRT = key("squirt");
    public static final ResourceKey<DamageType> MALLET = key("mallet");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, JesterTricks.id(name));
    }

    public static DamageSource of(Level level, ResourceKey<DamageType> type, @Nullable Entity direct, @Nullable Entity causing) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type),
                direct, causing);
    }

    public static DamageSource bang(Level level, @Nullable Entity direct, @Nullable Entity causing) {
        return of(level, BANG, direct, causing);
    }

    private ModDamageTypes() {}
}
