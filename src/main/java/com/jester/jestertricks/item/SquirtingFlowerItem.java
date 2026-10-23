package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Flor de Solapa: "¡Huele esta flor!" ... ¡chorro a la cara!
 * Lanza un chorro de agua de 8 bloques: empuja, apaga el fuego (también el tuyo)
 * y destroza a las criaturas que odian el agua (Endermen, Blazes...: 14 de daño).
 */
public class SquirtingFlowerItem extends Item {
    private static final double RANGE = 8.0;

    public SquirtingFlowerItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide && level instanceof ServerLevel sl) {
            Vec3 eye = player.getEyePosition();
            Vec3 look = player.getLookAngle();

            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().expandTowards(look.scale(RANGE)).inflate(1.5), e -> e != player && e.isAlive())) {
                Vec3 to = e.getBoundingBox().getCenter().subtract(eye);
                double dist = to.length();
                if (dist > RANGE || to.normalize().dot(look) < 0.85) continue;
                e.clearFire();
                if (hatesWater(e)) {
                    e.hurt(level.damageSources().indirectMagic(player, player), 14.0F);
                } else {
                    e.hurt(level.damageSources().playerAttack(player), 3.0F);
                }
                e.setDeltaMovement(e.getDeltaMovement().add(look.x * 1.4, 0.3, look.z * 1.4));
                e.hurtMarked = true;
            }

            // apaga el fuego a lo largo del chorro
            for (double d = 1.0; d <= RANGE; d += 0.5) {
                Vec3 p = eye.add(look.scale(d));
                BlockPos bp = BlockPos.containing(p);
                for (BlockPos q : BlockPos.betweenClosed(bp.offset(-1, -1, -1), bp.offset(1, 1, 1))) {
                    if (level.getBlockState(q).is(BlockTags.FIRE)) {
                        level.removeBlock(q, false);
                    }
                }
                sl.sendParticles(ParticleTypes.SPLASH, p.x, p.y, p.z, 4, 0.08, 0.08, 0.08, 0.05);
                if (((int) (d * 2)) % 3 == 0) {
                    sl.sendParticles(ParticleTypes.FALLING_WATER, p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0.0);
                }
            }
            player.clearFire();
            Fx.play(level, player.position(), ModSounds.SQUIRT.get(), 1.0F, Fx.pitch(level, 1.0F, 0.3F));
            player.getCooldowns().addCooldown(this, 6);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private static boolean hatesWater(LivingEntity e) {
        return e instanceof EnderMan || e instanceof Blaze || e instanceof Strider || e instanceof SnowGolem;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
