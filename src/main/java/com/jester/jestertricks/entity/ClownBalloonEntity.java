package com.jester.jestertricks.entity;

import com.jester.jestertricks.event.JesterEvents;
import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.registry.ModEntities;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Globo de payaso atado a una criatura o jugador: lo eleva durante 5 segundos,
 * luego explota y el atado baja flotando (sin daño de caída).
 */
public class ClownBalloonEntity extends Entity {
    private static final EntityDataAccessor<Integer> TARGET =
            SynchedEntityData.defineId(ClownBalloonEntity.class, EntityDataSerializers.INT);
    public static final int FLIGHT_TICKS = 100;
    private int age;

    public ClownBalloonEntity(EntityType<? extends ClownBalloonEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static ClownBalloonEntity tie(Level level, LivingEntity target) {
        ClownBalloonEntity b = new ClownBalloonEntity(ModEntities.CLOWN_BALLOON.get(), level);
        b.entityData.set(TARGET, target.getId());
        b.moveTo(target.getX(), target.getY() + target.getBbHeight() + 1.4, target.getZ());
        return b;
    }

    @Nullable
    public Entity getTarget() {
        return level().getEntity(entityData.get(TARGET));
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(TARGET, -1);
    }

    @Override
    public void tick() {
        super.tick();
        Entity target = getTarget();
        if (target == null || !target.isAlive()) {
            if (!level().isClientSide) pop(null);
            return;
        }
        // flota sobre la cabeza, con un ligero vaivén
        double sway = Math.sin(tickCount * 0.15) * 0.15;
        setPos(target.getX() + sway, target.getY() + target.getBbHeight() + 1.4, target.getZ() + sway * 0.6);
        if (level().isClientSide) return;
        age++;
        Vec3 v = target.getDeltaMovement();
        target.setDeltaMovement(v.x * 0.8, Math.max(v.y, 0.16), v.z * 0.8);
        target.hurtMarked = true;
        target.fallDistance = 0.0F;
        if (age >= FLIGHT_TICKS) pop(target);
    }

    private void pop(@Nullable Entity target) {
        if (level() instanceof ServerLevel sl) {
            Fx.confetti(sl, position(), 14, 0.5);
        }
        level().playSound(null, getX(), getY(), getZ(), ModSounds.BALLOON_POP.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        if (target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 120, 0));
            JesterEvents.grantSafeLanding(living, 400);
        }
        discard();
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
    }
}
