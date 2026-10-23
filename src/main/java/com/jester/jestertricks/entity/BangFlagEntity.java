package com.jester.jestertricks.entity;

import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.registry.ModDamageTypes;
import com.jester.jestertricks.registry.ModEffects;
import com.jester.jestertricks.registry.ModEntities;
import com.jester.jestertricks.registry.ModItems;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Banderita BANG!: al tocar algo explota (sin romper bloques).
 * Daño: 8 (4 corazones) que atraviesa armadura y encantamientos, y NUNCA mata:
 * como mucho deja al objetivo con medio corazón. Aplica "Pranked!" 10 segundos.
 */
public class BangFlagEntity extends ThrowableItemProjectile {
    public static final float BLAST_DAMAGE = 8.0F;
    public static final double RADIUS = 3.0;
    public static final int PRANKED_TICKS = 200;

    public BangFlagEntity(EntityType<? extends BangFlagEntity> type, Level level) {
        super(type, level);
    }

    public BangFlagEntity(Level level, LivingEntity owner) {
        super(ModEntities.BANG_FLAG.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BANG_FLAG.get();
    }

    @Override
    protected float getGravity() {
        return 0.0F;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            level().addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), 0, 0, 0);
        } else if (tickCount > 60) {
            discard();
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return entity != getOwner() && super.canHitEntity(entity);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            bang(result.getLocation());
            discard();
        }
    }

    private void bang(Vec3 at) {
        Level level = level();
        Entity owner = getOwner();
        AABB area = new AABB(at, at).inflate(RADIUS);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e.isAlive() && e != owner && e.distanceToSqr(at) <= RADIUS * RADIUS)) {
            // nunca mata: como mucho deja 1 de vida (medio corazón)
            float dmg = Math.min(JesterConfig.damage(BLAST_DAMAGE), e.getHealth() - 1.0F);
            if (dmg > 0) {
                e.invulnerableTime = 0;
                e.hurt(ModDamageTypes.bang(level, this, owner), dmg);
            }
            e.addEffect(new MobEffectInstance(ModEffects.PRANKED.get(), PRANKED_TICKS, 0), owner);
            Vec3 push = e.position().subtract(at);
            push = push.lengthSqr() < 1.0E-4 ? new Vec3(0, 1, 0) : push.normalize();
            e.setDeltaMovement(e.getDeltaMovement().add(push.x * 0.8, 0.45, push.z * 0.8));
            e.hurtMarked = true;
        }
        if (level instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.EXPLOSION, at.x, at.y, at.z, 3, 0.6, 0.6, 0.6, 0.0);
            sl.sendParticles(ParticleTypes.POOF, at.x, at.y, at.z, 20, 0.5, 0.5, 0.5, 0.1);
            Fx.confetti(sl, at, 14, 1.2);
        }
        level.playSound(null, at.x, at.y, at.z, ModSounds.BANG.get(), SoundSource.PLAYERS, 1.6F,
                0.9F + level.random.nextFloat() * 0.2F);
    }
}
