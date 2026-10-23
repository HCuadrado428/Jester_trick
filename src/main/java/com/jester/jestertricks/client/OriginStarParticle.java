package com.jester.jestertricks.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

/** Estrella del origen: aparece, gira despacio, parpadea y se encoge. */
public class OriginStarParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final float spin;
    private final float baseSize;

    OriginStarParticle(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, 0, 0, 0);
        this.sprites = sprites;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.friction = 0.9F;
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.lifetime = 18 + random.nextInt(14);
        this.baseSize = 0.12F + random.nextFloat() * 0.1F;
        this.quadSize = baseSize;
        this.spin = (random.nextFloat() - 0.5F) * 0.25F;
        this.roll = random.nextFloat() * Mth.TWO_PI;
        this.oRoll = roll;
        setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        if (removed) return;
        oRoll = roll;
        roll += spin;
        float life = age / (float) lifetime;
        quadSize = baseSize * (life < 0.2F ? life / 0.2F : 1.0F - (life - 0.2F) / 0.8F * 0.7F);
        alpha = (age % 6 < 3 || life < 0.6F) ? 1.0F : 0.55F;
        setSpriteFromAge(sprites);
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 0xF000F0; // siempre brillante
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Provider(SpriteSet sprites) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new OriginStarParticle(level, x, y, z, vx, vy, vz, sprites);
        }
    }
}
