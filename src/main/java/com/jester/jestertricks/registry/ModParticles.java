package com.jester.jestertricks.registry;

import com.jester.jestertricks.JesterTricks;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, JesterTricks.MOD_ID);

    /** La estrella del origen: blanca con el centro negro, parpadea y gira. */
    public static final RegistryObject<SimpleParticleType> ORIGIN_STAR =
            PARTICLES.register("origin_star", () -> new SimpleParticleType(false));

    private ModParticles() {}
}
