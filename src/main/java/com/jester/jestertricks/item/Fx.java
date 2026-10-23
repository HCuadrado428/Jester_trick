package com.jester.jestertricks.item;

import com.jester.jestertricks.registry.ModParticles;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/** Efectos visuales y de sonido compartidos, en la paleta blanco/gris/negro del Jester. */
public final class Fx {
    public static final DustParticleOptions WHITE = new DustParticleOptions(new Vector3f(1.0F, 1.0F, 1.0F), 1.4F);
    public static final DustParticleOptions GREY = new DustParticleOptions(new Vector3f(0.55F, 0.55F, 0.55F), 1.4F);
    public static final DustParticleOptions BLACK = new DustParticleOptions(new Vector3f(0.05F, 0.05F, 0.05F), 1.6F);
    public static final DustParticleOptions BLACK_BIG = new DustParticleOptions(new Vector3f(0.02F, 0.02F, 0.02F), 2.5F);

    /** Confeti arlequín: mezcla de motas blancas, grises y negras. */
    public static void confetti(ServerLevel level, Vec3 pos, int count, double spread) {
        level.sendParticles(WHITE, pos.x, pos.y, pos.z, count, spread, spread, spread, 0.1);
        level.sendParticles(GREY, pos.x, pos.y, pos.z, count, spread, spread, spread, 0.1);
        level.sendParticles(BLACK, pos.x, pos.y, pos.z, count, spread, spread, spread, 0.1);
        level.sendParticles(ModParticles.ORIGIN_STAR.get(), pos.x, pos.y, pos.z, Math.max(1, count / 4), spread, spread, spread, 0.02);
    }

    /** Anillo de pétalos negros alrededor de un punto. */
    public static void petalRing(ServerLevel level, Vec3 c, double radius, int points) {
        for (int i = 0; i < points; i++) {
            double a = (Math.PI * 2 * i) / points;
            double x = c.x + Math.cos(a) * radius;
            double z = c.z + Math.sin(a) * radius;
            level.sendParticles(BLACK_BIG, x, c.y + 0.2, z, 2, 0.1, 0.3, 0.1, 0.0);
            level.sendParticles(ParticleTypes.SQUID_INK, x, c.y + 0.5, z, 1, 0.05, 0.1, 0.05, 0.02);
        }
    }

    /**
     * Grieta al mundo del Jester: un anillo vertical negro con borde blanco
     * y la estrella de cuatro puntas de su origen en el centro.
     * @param yaw hacia dónde mira la grieta (grados, como el yaw de una entidad)
     */
    public static void rift(ServerLevel level, Vec3 c, float yaw, double radius) {
        double a = Math.toRadians(yaw);
        // eje horizontal de la grieta (perpendicular a hacia donde mira)
        double rx = Math.cos(a), rz = Math.sin(a);
        int points = (int) (radius * 28);
        for (int i = 0; i < points; i++) {
            double t = Math.PI * 2 * i / points;
            double h = Math.cos(t) * radius, v = Math.sin(t) * radius * 1.35;
            level.sendParticles(BLACK_BIG, c.x + rx * h, c.y + v, c.z + rz * h, 1, 0, 0, 0, 0);
            double h2 = Math.cos(t) * (radius + 0.15), v2 = Math.sin(t) * (radius + 0.15) * 1.35;
            level.sendParticles(WHITE, c.x + rx * h2, c.y + v2, c.z + rz * h2, 1, 0, 0, 0, 0);
        }
        // estrella del origen en el centro y alguna suelta alrededor
        level.sendParticles(ModParticles.ORIGIN_STAR.get(), c.x, c.y, c.z, 1, 0, 0, 0, 0);
        level.sendParticles(ModParticles.ORIGIN_STAR.get(), c.x, c.y, c.z, 3, radius * 0.6, radius * 0.7, radius * 0.6, 0.01);
        level.sendParticles(ParticleTypes.SQUID_INK, c.x, c.y, c.z, 6, 0.08, 0.08, 0.08, 0.01);
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, c.x, c.y, c.z, 12, radius * 0.4, radius * 0.5, radius * 0.4, 0.02);
    }

    public static void play(Level level, Vec3 pos, SoundEvent sound, float volume, float pitch) {
        level.playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.PLAYERS, volume, pitch);
    }

    public static float pitch(Level level, float base, float spread) {
        return base + (level.random.nextFloat() - 0.5F) * spread;
    }

    private Fx() {}
}
