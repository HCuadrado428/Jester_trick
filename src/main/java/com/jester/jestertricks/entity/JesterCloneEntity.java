package com.jester.jestertricks.entity;

import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.registry.ModEntities;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

/**
 * Copia falsa del Jester (con su misma skin). Pasea, atrae a los monstruos
 * y al recibir cualquier golpe estalla en confeti con una carcajada.
 */
public class JesterCloneEntity extends PathfinderMob {
    private static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(JesterCloneEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final int LIFETIME = 20 * 20;

    public JesterCloneEntity(EntityType<? extends JesterCloneEntity> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    public static JesterCloneEntity create(Level level, Player owner, Vec3 at) {
        JesterCloneEntity clone = new JesterCloneEntity(ModEntities.JESTER_CLONE.get(), level);
        clone.entityData.set(OWNER, Optional.of(owner.getUUID()));
        clone.moveTo(at.x, at.y, at.z, owner.getYRot() + (level.random.nextFloat() - 0.5F) * 90F, 0.0F);
        return clone;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    public Optional<UUID> getOwnerId() {
        return this.entityData.get(OWNER);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER, Optional.empty());
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && tickCount > LIFETIME) pop();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide) return false;
        if (isRemoved()) return false;
        pop();
        return true;
    }

    private void pop() {
        if (level() instanceof ServerLevel sl) {
            Vec3 c = position().add(0, 1.0, 0);
            Fx.confetti(sl, c, 16, 0.6);
            sl.sendParticles(ParticleTypes.POOF, c.x, c.y, c.z, 14, 0.3, 0.6, 0.3, 0.03);
        }
        level().playSound(null, getX(), getY(), getZ(), ModSounds.MIRROR.get(), SoundSource.NEUTRAL, 0.9F,
                0.9F + random.nextFloat() * 0.3F);
        discard();
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }
}
