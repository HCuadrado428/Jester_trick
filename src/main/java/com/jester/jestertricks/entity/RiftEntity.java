package com.jester.jestertricks.entity;

import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.portal.OriginTravel;
import com.jester.jestertricks.registry.ModEntities;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Grieta que abre la Estrella del Origen. Se abre durante un segundo, absorbe al Jester
 * hacia ella y lo lleva a la dimensión elegida. Solo funciona con quien la abrió.
 */
public class RiftEntity extends Entity {
    private UUID owner;
    private ResourceKey<Level> destination = Level.NETHER;
    private int age;

    public RiftEntity(EntityType<? extends RiftEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static RiftEntity open(Level level, Player owner, Vec3 at, ResourceKey<Level> destination) {
        RiftEntity rift = new RiftEntity(ModEntities.RIFT.get(), level);
        rift.owner = owner.getUUID();
        rift.destination = destination;
        rift.moveTo(at.x, at.y, at.z, owner.getYRot(), 0.0F);
        return rift;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || !(level() instanceof ServerLevel sl)) return;
        age++;
        double radius = Math.min(1.0, 0.2 + age * 0.08);
        if (age % 2 == 0) Fx.rift(sl, position(), getYRot(), radius);

        Player p = owner == null ? null : level().getPlayerByUUID(owner);
        if (p instanceof ServerPlayer sp && sp.distanceToSqr(this) < 16.0) {
            // la grieta tira suavemente del Jester hacia dentro
            Vec3 pull = position().subtract(sp.position().add(0, 1, 0));
            if (pull.lengthSqr() > 0.01) {
                sp.setDeltaMovement(pull.normalize().scale(0.25));
                sp.hurtMarked = true;
            }
            if (age == 22) {
                OriginTravel.travel(sp, destination);
                discard();
                return;
            }
        }
        if (age > 40) discard();
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.hasUUID("Owner")) owner = tag.getUUID("Owner");
        if (tag.contains("Dest")) {
            destination = ResourceKey.create(Registries.DIMENSION, new net.minecraft.resources.ResourceLocation(tag.getString("Dest")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (owner != null) tag.putUUID("Owner", owner);
        tag.putString("Dest", destination.location().toString());
    }
}
