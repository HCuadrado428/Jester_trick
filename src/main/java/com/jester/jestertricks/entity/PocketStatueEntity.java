package com.jester.jestertricks.entity;

import com.jester.jestertricks.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Estatua gigante del bolsillo: un objeto o una criatura de la Caja Negra, enorme e intocable.
 * No tiene cuerpo (nada choca con ella, no se puede golpear) y no se guarda: se vuelve a poner
 * cada vez que alguien entra.
 */
public class PocketStatueEntity extends Entity {
    private static final EntityDataAccessor<ItemStack> ITEM =
            SynchedEntityData.defineId(PocketStatueEntity.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<CompoundTag> MOB =
            SynchedEntityData.defineId(PocketStatueEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<Float> SCALE =
            SynchedEntityData.defineId(PocketStatueEntity.class, EntityDataSerializers.FLOAT);

    /** Solo en el cliente: la criatura de muestra que se dibuja en grande. */
    public Entity cachedMob;

    public PocketStatueEntity(EntityType<? extends PocketStatueEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
        this.setInvulnerable(true);
    }

    public static PocketStatueEntity ofItem(Level level, ItemStack stack, float scale) {
        PocketStatueEntity e = new PocketStatueEntity(ModEntities.POCKET_STATUE.get(), level);
        e.entityData.set(ITEM, stack.copy());
        e.entityData.set(SCALE, scale);
        return e;
    }

    public static PocketStatueEntity ofMob(Level level, CompoundTag mob, float scale) {
        PocketStatueEntity e = new PocketStatueEntity(ModEntities.POCKET_STATUE.get(), level);
        e.entityData.set(MOB, mob.copy());
        e.entityData.set(SCALE, scale);
        return e;
    }

    public ItemStack getItem() {
        return entityData.get(ITEM);
    }

    public CompoundTag getMob() {
        return entityData.get(MOB);
    }

    public float getScale() {
        return entityData.get(SCALE);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(ITEM, ItemStack.EMPTY);
        entityData.define(MOB, new CompoundTag());
        entityData.define(SCALE, 1.0F);
    }

    @Override
    public void tick() {
        // quieta: no se mueve ni cae
        this.xo = getX();
        this.yo = getY();
        this.zo = getZ();
        this.yRotO = getYRot();
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distSq) {
        return distSq < 160.0 * 160.0;
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
