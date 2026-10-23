package com.jester.jestertricks.entity;

import com.jester.jestertricks.registry.ModDamageTypes;
import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.registry.ModEntities;
import com.jester.jestertricks.registry.ModItems;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

/**
 * Bolo de malabares: golpea y rebota hacia el enemigo más cercano que aún no haya tocado
 * (hasta 2 rebotes). Contra un bloque, rebota una vez y cae.
 */
public class JugglingPinEntity extends ThrowableItemProjectile {
    private static final int MAX_BOUNCES = 2;
    private final Set<Integer> hit = new HashSet<>();
    private int bounces;
    private boolean bouncedOffBlock;

    public JugglingPinEntity(EntityType<? extends JugglingPinEntity> type, Level level) {
        super(type, level);
    }

    public JugglingPinEntity(Level level, LivingEntity owner) {
        super(ModEntities.JUGGLING_PIN.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.JUGGLING_PIN.get();
    }

    @Override
    protected float getGravity() {
        return 0.02F;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount > 100) discard();
    }

    @Override
    protected boolean canHitEntity(Entity e) {
        return e != getOwner() && !hit.contains(e.getId()) && super.canHitEntity(e);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide) return;
        Entity target = result.getEntity();
        hit.add(target.getId());
        if (target instanceof LivingEntity living) living.invulnerableTime = 0;
        target.hurt(ModDamageTypes.of(level(), ModDamageTypes.JUGGLING_PIN, this, getOwner()), JesterConfig.damage(6.0F));
        level().playSound(null, getX(), getY(), getZ(), ModSounds.BOING.get(), SoundSource.PLAYERS, 0.7F, 1.5F + bounces * 0.2F);
        if (level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.CRIT, getX(), getY(), getZ(), 8, 0.2, 0.2, 0.2, 0.2);
            Fx.confetti(sl, position(), 3, 0.3);
        }
        if (bounces >= MAX_BOUNCES) {
            discard();
            return;
        }
        LivingEntity next = null;
        double best = 8.0 * 8.0;
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(8.0),
                e -> e.isAlive() && e != getOwner() && !hit.contains(e.getId()))) {
            double d = e.distanceToSqr(this);
            if (d < best) {
                best = d;
                next = e;
            }
        }
        if (next == null) {
            discard();
            return;
        }
        bounces++;
        Vec3 to = next.getBoundingBox().getCenter().subtract(position()).normalize();
        setDeltaMovement(to.scale(1.4).add(0, 0.12, 0));
        hasImpulse = true;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (level().isClientSide) return;
        if (bouncedOffBlock) {
            discard();
            return;
        }
        bouncedOffBlock = true;
        Vec3 v = getDeltaMovement();
        switch (result.getDirection().getAxis()) {
            case X -> setDeltaMovement(-v.x * 0.5, v.y * 0.5 + 0.2, v.z * 0.5);
            case Y -> setDeltaMovement(v.x * 0.5, -v.y * 0.5, v.z * 0.5);
            case Z -> setDeltaMovement(v.x * 0.5, v.y * 0.5 + 0.2, -v.z * 0.5);
        }
        level().playSound(null, getX(), getY(), getZ(), ModSounds.BOING.get(), SoundSource.PLAYERS, 0.4F, 1.8F);
    }
}
