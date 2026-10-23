package com.jester.jestertricks.entity;

import com.jester.jestertricks.registry.ModDamageTypes;
import com.jester.jestertricks.registry.ModEntities;
import com.jester.jestertricks.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

/** Naipe afilado lanzado: casi sin gravedad, corta lo que toca. */
public class ThrownCardEntity extends ThrowableItemProjectile {
    private float damage = 10.0F;

    public ThrownCardEntity(EntityType<? extends ThrownCardEntity> type, Level level) {
        super(type, level);
    }

    public ThrownCardEntity(Level level, LivingEntity owner, float damage) {
        super(ModEntities.THROWN_CARD.get(), owner, level);
        this.damage = damage;
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.THROWN_CARD.get();
    }

    @Override
    protected float getGravity() {
        return 0.004F;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount > 80) {
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        if (target instanceof LivingEntity living) {
            living.invulnerableTime = 0;   // que el abanico entero pueda cortar al mismo objetivo
        }
        target.hurt(ModDamageTypes.of(level(), ModDamageTypes.CARD, this, getOwner()), damage);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            if (level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.CRIT, getX(), getY(), getZ(), 6, 0.1, 0.1, 0.1, 0.2);
            }
            discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("CardDamage", damage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damage = tag.getFloat("CardDamage");
    }
}
