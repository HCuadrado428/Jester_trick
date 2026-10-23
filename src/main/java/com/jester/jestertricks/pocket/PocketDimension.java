package com.jester.jestertricks.pocket;

import com.jester.jestertricks.JesterTricks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/** La dimensión de bolsillo del Jester (data/jestertricks/dimension/pocket.json). */
public final class PocketDimension {
    public static final ResourceKey<Level> POCKET = ResourceKey.create(Registries.DIMENSION, JesterTricks.id("pocket"));

    /** Altura a la que se pisa el suelo de rombos (barrera en y=0, rombos en y=1). */
    public static final double FLOOR_Y = 2.0;

    public static boolean isPocket(Level level) {
        return level.dimension() == POCKET;
    }

    /** Cada Jester tiene su propio rincón del bolsillo, muy lejos de los demás. */
    public static Vec3 center(UUID jester) {
        int idx = Math.floorMod(jester.hashCode(), 64);
        return new Vec3(idx * 4096 + 0.5, FLOOR_Y, 0.5);
    }

    private PocketDimension() {}
}
