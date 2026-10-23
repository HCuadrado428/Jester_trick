package com.jester.jestertricks.entity;

import com.jester.jestertricks.registry.ModDamageTypes;
import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.registry.ModEntities;
import com.jester.jestertricks.registry.ModItems;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Tarta en la cara: ciega y ralentiza al que la recibe, con un ¡SPLAT! de nata. */
public class CreamPieEntity extends ThrowableItemProjectile {
    public CreamPieEntity(EntityType<? extends CreamPieEntity> type, Level level) {
        super(type, level);
    }

    public CreamPieEntity(Level level, LivingEntity owner) {
        super(ModEntities.CREAM_PIE.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.CREAM_PIE.get();
    }

    @Override
    protected float getGravity() {
        return 0.035F;
    }

    @Override
    protected boolean canHitEntity(Entity e) {
        return e != getOwner() && super.canHitEntity(e);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide) return;
        Entity target = result.getEntity();
        target.hurt(ModDamageTypes.of(level(), ModDamageTypes.CREAM_PIE, this, getOwner()), JesterConfig.damage(2.0F));
        if (target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0), getOwner());
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1), getOwner());
            living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0), getOwner());
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide) return;
        Vec3 at = result.getLocation();
        if (level() instanceof ServerLevel sl) {
            sl.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.CAKE)), at.x, at.y, at.z, 30, 0.3, 0.3, 0.3, 0.15);
            sl.sendParticles(Fx.WHITE, at.x, at.y, at.z, 20, 0.4, 0.4, 0.4, 0.0);
            sl.sendParticles(ParticleTypes.SNOWFLAKE, at.x, at.y, at.z, 10, 0.3, 0.3, 0.3, 0.05);
        }
        level().playSound(null, at.x, at.y, at.z, ModSounds.SPLAT.get(), SoundSource.PLAYERS, 1.0F, 0.9F + random.nextFloat() * 0.2F);
        discard();
    }
}
