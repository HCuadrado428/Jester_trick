package com.jester.jestertricks.item;

import com.jester.jestertricks.registry.ModDamageTypes;
import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Cetro del Bufón: la flor negra del gorro del Jester.
 * Golpe: 16 de daño + Wither II. Clic derecho: tormenta de pétalos negros
 * que marea, ciega, ralentiza y hiere a todo lo que haya a 7 bloques.
 */
public class JesterScepterItem extends SwordItem {
    private static final int COOLDOWN = 120;

    public JesterScepterItem(Properties props) {
        super(JesterTier.INSTANCE, 15, -2.2F, props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player p && !JesterLock.isJester(p)) return super.hurtEnemy(stack, target, attacker);
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 1), attacker);
        if (attacker.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SQUID_INK, target.getX(), target.getY(0.6), target.getZ(), 10, 0.3, 0.3, 0.3, 0.02);
            sl.sendParticles(Fx.BLACK_BIG, target.getX(), target.getY(0.6), target.getZ(), 12, 0.4, 0.4, 0.4, 0.0);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide && level instanceof ServerLevel sl) {
            AABB area = player.getBoundingBox().inflate(7.0);
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player && e.isAlive())) {
                if (e.distanceToSqr(player) > 49.0) continue;
                e.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), player);
                e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 0), player);
                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2), player);
                e.hurt(ModDamageTypes.of(level, ModDamageTypes.PETALS, player, player), JesterConfig.damage(6.0F));
                sl.sendParticles(ParticleTypes.SQUID_INK, e.getX(), e.getY(0.8), e.getZ(), 6, 0.3, 0.3, 0.3, 0.02);
            }
            Fx.petalRing(sl, player.position(), 2.5, 24);
            Fx.petalRing(sl, player.position(), 5.0, 40);
            Fx.play(level, player.position(), ModSounds.JESTER_LAUGH.get(), 1.0F, Fx.pitch(level, 0.95F, 0.15F));
            player.getCooldowns().addCooldown(this, JesterConfig.cooldown(COOLDOWN));
        }
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
