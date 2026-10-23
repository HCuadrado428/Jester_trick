package com.jester.jestertricks.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Cielo del bolsillo: negro absoluto, sin sol, luna, estrellas ni nubes,
 * y niebla cercana para que el horizonte se pierda en la oscuridad.
 */
public class PocketSkyEffects extends DimensionSpecialEffects {
    public PocketSkyEffects() {
        super(Float.NaN, false, SkyType.NONE, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return Vec3.ZERO;
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return true;
    }

    @Nullable
    @Override
    public float[] getSunriseColor(float timeOfDay, float partialTicks) {
        return null;
    }
}
