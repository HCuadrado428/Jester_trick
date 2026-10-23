package com.jester.jestertricks.entity;

import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.registry.ModEntities;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Muñeco arlequín: pequeño seguidor del Jester. Le sigue a saltitos, ataca a quien le
 * ataca o a quien él ataca, y al caer estalla en confeti con una risita.
 */
public class HarlequinDollEntity extends TamableAnimal {

    public HarlequinDollEntity(EntityType<? extends HarlequinDollEntity> type, Level level) {
        super(type, level);
        this.xpReward = 0;
    }

    public static HarlequinDollEntity create(Level level, Player owner, Vec3 at) {
        HarlequinDollEntity doll = new HarlequinDollEntity(ModEntities.HARLEQUIN_DOLL.get(), level);
        doll.tame(owner);
        doll.moveTo(at.x, at.y, at.z, owner.getYRot(), 0.0F);
        return doll;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0)
                .add(Attributes.MOVEMENT_SPEED, 0.36)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.2, 4.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        // saltitos de payaso al andar
        if (!level().isClientSide && onGround() && getDeltaMovement().horizontalDistanceSqr() > 0.002 && tickCount % 12 == 0) {
            setDeltaMovement(getDeltaMovement().add(0, 0.28, 0));
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) level().playSound(null, getX(), getY(), getZ(), ModSounds.BOING.get(), SoundSource.NEUTRAL, 0.5F, 1.8F);
        return hit;
    }

    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        return !(target instanceof HarlequinDollEntity) && target != owner && super.wantsToAttack(target, owner);
    }

    @Override
    public void die(DamageSource source) {
        if (level() instanceof ServerLevel sl) {
            Vec3 c = position().add(0, 0.5, 0);
            Fx.confetti(sl, c, 14, 0.4);
            sl.sendParticles(ParticleTypes.POOF, c.x, c.y, c.z, 10, 0.2, 0.3, 0.2, 0.03);
            sl.playSound(null, getX(), getY(), getZ(), ModSounds.BALLOON_POP.get(), SoundSource.NEUTRAL, 0.8F, 1.4F);
            sl.playSound(null, getX(), getY(), getZ(), ModSounds.JESTER_LAUGH.get(), SoundSource.NEUTRAL, 0.5F, 1.9F);
        }
        super.die(source);
        discard();
    }

    /** Desaparece con un ¡puf! (cuando el Jester los guarda en la caja). */
    public void poof() {
        if (level() instanceof ServerLevel sl) {
            Vec3 c = position().add(0, 0.5, 0);
            Fx.confetti(sl, c, 6, 0.3);
            sl.sendParticles(ParticleTypes.POOF, c.x, c.y, c.z, 6, 0.2, 0.3, 0.2, 0.02);
        }
        discard();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return random.nextInt(4) == 0 ? ModSounds.BELLS.get() : null;
    }

    @Override
    protected float getSoundVolume() {
        return 0.25F;
    }

    @Override
    public float getVoicePitch() {
        return 1.6F;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }
}
