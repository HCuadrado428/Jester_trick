package com.jester.jestertricks.entity;

import com.jester.jestertricks.registry.ModEntities;
import com.jester.jestertricks.registry.ModItems;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Cuchillo de malabarista: vuela recto, corta, y vuelve a su dueño como un bumerán
 * (atravesando paredes al volver).
 */
public class ThrownKnifeEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Boolean> RETURNING =
            SynchedEntityData.defineId(ThrownKnifeEntity.class, EntityDataSerializers.BOOLEAN);

    private float damage = 18.0F;

    public ThrownKnifeEntity(EntityType<? extends ThrownKnifeEntity> type, Level level) {
        super(type, level);
    }

    public ThrownKnifeEntity(Level level, LivingEntity owner, float damage) {
        super(ModEntities.THROWN_KNIFE.get(), owner, level);
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RETURNING, false);
    }

    public boolean isReturning() {
        return this.entityData.get(RETURNING);
    }

    private void startReturning() {
        this.entityData.set(RETURNING, true);
        this.setNoGravity(true);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.THROWN_KNIFE.get();
    }

    @Override
    protected float getGravity() {
        return isReturning() ? 0.0F : 0.01F;
    }

    @Override
    public void tick() {
        super.tick();
        if (!isReturning()) {
            if (!level().isClientSide && tickCount > 14) startReturning();
            return;
        }
        Entity owner = getOwner();
        if (owner == null || !owner.isAlive() || owner.level() != level()) {
            if (!level().isClientSide) discard();
            return;
        }
        Vec3 to = owner.getEyePosition().subtract(0, 0.4, 0).subtract(position());
        if (to.length() < 1.4) {
            if (!level().isClientSide) {
                level().playSound(null, owner.getX(), owner.getY(), owner.getZ(), ModSounds.CARD_THROW.get(),
                        SoundSource.PLAYERS, 0.5F, 1.8F);
                discard();
            }
            return;
        }
        setDeltaMovement(to.normalize().scale(1.5));
        hasImpulse = true;
        if (!level().isClientSide && tickCount > 200) discard();
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return entity != getOwner() && super.canHitEntity(entity);
    }

    @Override
    protected void onHit(HitResult result) {
        if (isReturning()) return;   // al volver no choca con nada
        super.onHit(result);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        if (target instanceof LivingEntity living) living.invulnerableTime = 0;
        target.hurt(damageSources().thrown(this, getOwner()), damage);
        if (!level().isClientSide) startReturning();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            if (level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.CRIT, getX(), getY(), getZ(), 5, 0.1, 0.1, 0.1, 0.1);
            }
            startReturning();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("KnifeDamage", damage);
        tag.putBoolean("Returning", isReturning());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.getFloat("KnifeDamage");
        if (tag.getBoolean("Returning")) startReturning();
    }
}
