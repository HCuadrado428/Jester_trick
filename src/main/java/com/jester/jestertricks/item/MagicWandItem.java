package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Varita: "¿Dónde está la bolita?"
 * Clic derecho apuntando a una criatura (hasta 32 bloques): intercambias tu posición con la suya.
 * Agachado (o sin criatura): te teletransportas al bloque al que miras.
 */
public class MagicWandItem extends Item {
    private static final double RANGE = 32.0;

    public MagicWandItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (level.isClientSide || !(level instanceof ServerLevel sl)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = eye.add(look.scale(RANGE));
        BlockHitResult blockHit = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        double blockDistSq = blockHit.getType() == HitResult.Type.MISS ? RANGE * RANGE : blockHit.getLocation().distanceToSqr(eye);

        boolean done = false;
        if (!player.isShiftKeyDown()) {
            AABB box = player.getBoundingBox().expandTowards(look.scale(RANGE)).inflate(1.0);
            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(player, eye, end, box,
                    e -> !e.isSpectator() && e.isPickable() && e instanceof LivingEntity, RANGE * RANGE);
            if (entityHit != null && entityHit.getLocation().distanceToSqr(eye) < blockDistSq) {
                Entity target = entityHit.getEntity();
                Vec3 mine = player.position();
                Vec3 theirs = target.position();
                poof(sl, mine);
                poof(sl, theirs);
                target.teleportTo(mine.x, mine.y, mine.z);
                player.teleportTo(theirs.x, theirs.y, theirs.z);
                player.fallDistance = 0.0F;
                target.fallDistance = 0.0F;
                done = true;
            }
        }

        if (!done && blockHit.getType() == HitResult.Type.BLOCK) {
            BlockPos base = blockHit.getBlockPos().relative(blockHit.getDirection());
            if (blockHit.getDirection() != Direction.UP && blockHit.getDirection() != Direction.DOWN) {
                // si apuntas a una pared, intenta quedarte de pie junto a ella
                if (level.getBlockState(base.below()).isAir()) base = base.below();
            }
            for (int dy = 0; dy <= 2 && !done; dy++) {
                Vec3 dest = new Vec3(base.getX() + 0.5, base.getY() + dy, base.getZ() + 0.5);
                AABB moved = player.getBoundingBox().move(dest.subtract(player.position()));
                if (level.noCollision(player, moved)) {
                    poof(sl, player.position());
                    player.teleportTo(dest.x, dest.y, dest.z);
                    player.fallDistance = 0.0F;
                    poof(sl, dest);
                    done = true;
                }
            }
        }

        if (done) {
            Fx.play(level, player.position(), ModSounds.RIFT.get(), 0.8F, Fx.pitch(level, 1.5F, 0.2F));
            player.getCooldowns().addCooldown(this, 30);
        } else {
            Fx.play(level, player.position(), ModSounds.JESTER_LAUGH.get(), 0.4F, 1.6F);
            player.getCooldowns().addCooldown(this, 10);
        }
        return InteractionResultHolder.sidedSuccess(stack, false);
    }

    /** Mini portal: una grieta pequeña al mundo del Jester en cada extremo del salto. */
    private static void poof(ServerLevel level, Vec3 at) {
        level.sendParticles(ParticleTypes.POOF, at.x, at.y + 1.0, at.z, 8, 0.3, 0.6, 0.3, 0.02);
        Fx.rift(level, at.add(0, 1.0, 0), 0.0F, 0.6);
        Fx.rift(level, at.add(0, 1.0, 0), 90.0F, 0.6);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
