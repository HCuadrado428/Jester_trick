package com.jester.jestertricks.entity;

import com.jester.jestertricks.registry.ModDamageTypes;
import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.registry.ModEntities;
import com.jester.jestertricks.registry.ModItems;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.particles.DustParticleOptions;
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

/** Nudo de pañuelos: gancho que tira del dueño hacia el bloque o de la criatura hacia el dueño. */
public class ScarfHookEntity extends ThrowableItemProjectile {
    private static final DustParticleOptions[] SCARF = {Fx.WHITE, Fx.GREY, Fx.BLACK};
    private boolean hooked;
    private Vec3 anchor = Vec3.ZERO;
    private int hookTicks;

    public ScarfHookEntity(EntityType<? extends ScarfHookEntity> type, Level level) {
        super(type, level);
    }

    public ScarfHookEntity(Level level, LivingEntity owner) {
        super(ModEntities.SCARF_HOOK.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SCARF_KNOT.get();
    }

    @Override
    protected float getGravity() {
        return 0.0F;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        Entity owner = getOwner();
        if (!(owner instanceof LivingEntity user) || !owner.isAlive() || owner.level() != level()) {
            discard();
            return;
        }
        if (!hooked) {
            if (tickCount > 25) discard();
            return;
        }
        hookTicks++;
        setPos(anchor.x, anchor.y, anchor.z);
        setDeltaMovement(Vec3.ZERO);
        Vec3 to = anchor.subtract(user.position());
        double dist = to.length();
        if (dist < 1.8 || hookTicks > 60 || user.isShiftKeyDown()) {
            user.fallDistance = 0.0F;
            discard();
            return;
        }
        Vec3 pull = to.normalize().scale(Math.min(1.6, 0.5 + dist * 0.12));
        user.setDeltaMovement(pull);
        user.hurtMarked = true;
        user.fallDistance = 0.0F;
    }

    private void drawScarves(ServerLevel sl, Vec3 from, Vec3 to) {
        int steps = 12;
        for (int i = 1; i < steps; i++) {
            Vec3 p = from.lerp(to, i / (double) steps);
            sl.sendParticles(SCARF[i % SCARF.length], p.x, p.y, p.z, 1, 0, 0, 0, 0);
        }
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return entity != getOwner() && super.canHitEntity(entity);
    }

    @Override
    protected void onHit(HitResult result) {
        if (hooked) return;
        super.onHit(result);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!level().isClientSide) {
            hooked = true;
            anchor = result.getLocation();
            setNoGravity(true);
            setDeltaMovement(Vec3.ZERO);
            level().playSound(null, anchor.x, anchor.y, anchor.z, ModSounds.MAGIC_POOF.get(), SoundSource.PLAYERS, 0.6F, 1.6F);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (level().isClientSide) return;
        Entity target = result.getEntity();
        Entity owner = getOwner();
        if (owner != null) {
            Vec3 dir = owner.position().subtract(target.position()).normalize();
            target.setDeltaMovement(dir.x * 1.6, 0.45, dir.z * 1.6);
            target.hurtMarked = true;
        }
        target.hurt(ModDamageTypes.of(level(), ModDamageTypes.SCARF, this, owner), JesterConfig.damage(4.0F));
        discard();
    }
}
