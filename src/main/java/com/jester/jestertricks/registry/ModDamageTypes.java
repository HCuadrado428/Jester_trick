package com.jester.jestertricks.registry;

import com.jester.jestertricks.JesterTricks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class ModDamageTypes {
    /** Explosión de la banderita BANG!: atraviesa armadura y encantamientos (ver data/.../tags). */
    public static final ResourceKey<DamageType> BANG =
            ResourceKey.create(Registries.DAMAGE_TYPE, JesterTricks.id("bang"));

    public static DamageSource bang(Level level, @Nullable Entity direct, @Nullable Entity causing) {
        return new DamageSource(
                level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(BANG),
                direct, causing);
    }

    private ModDamageTypes() {}
}
