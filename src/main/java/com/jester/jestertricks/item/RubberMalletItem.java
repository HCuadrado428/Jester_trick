package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Mazo de Goma: poco daño (4) pero manda a volar a lo que golpea con un "¡BOING!".
 * Clic derecho: martillazo al suelo que lanza por los aires a todo lo que hay a 5 bloques.
 * En la mano se ve en 3D; en el inventario, en 2D (ver models/item/rubber_mallet.json).
 */
public class RubberMalletItem extends SwordItem {
    public RubberMalletItem(Properties props) {
        super(JesterTier.INSTANCE, 3, -2.8F, props);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player p && !JesterLock.isJester(p)) return super.hurtEnemy(stack, target, attacker);
        Vec3 dir = target.position().subtract(attacker.position());
        dir = new Vec3(dir.x, 0, dir.z);
        dir = dir.lengthSqr() < 1.0E-4 ? attacker.getLookAngle() : dir.normalize();
        target.setDeltaMovement(dir.x * 2.6, 0.9, dir.z * 2.6);
        target.hurtMarked = true;
        Level level = attacker.level();
        Fx.play(level, target.position(), ModSounds.BOING.get(), 1.0F, Fx.pitch(level, 1.0F, 0.4F));
        if (level instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.POOF, target.getX(), target.getY(0.5), target.getZ(), 10, 0.3, 0.3, 0.3, 0.05);
            Fx.confetti(sl, target.position().add(0, target.getBbHeight() * 0.6, 0), 6, 0.4);
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide && level instanceof ServerLevel sl) {
            Vec3 c = player.position();
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(5.0),
                    e -> e != player && e.isAlive())) {
                Vec3 away = e.position().subtract(c);
                away = new Vec3(away.x, 0, away.z);
                away = away.lengthSqr() < 1.0E-4 ? Vec3.ZERO : away.normalize();
                e.hurt(level.damageSources().playerAttack(player), 4.0F);
                e.setDeltaMovement(away.x * 1.2, 1.3, away.z * 1.2);
                e.hurtMarked = true;
            }
            for (int i = 0; i < 32; i++) {
                double a = Math.PI * 2 * i / 32;
                sl.sendParticles(ParticleTypes.CLOUD, c.x + Math.cos(a) * 2.5, c.y + 0.1, c.z + Math.sin(a) * 2.5,
                        1, 0, 0, 0, 0.05);
            }
            Fx.confetti(sl, c.add(0, 0.5, 0), 12, 1.5);
            Fx.play(level, c, ModSounds.BOING.get(), 1.4F, 0.6F);
            player.getCooldowns().addCooldown(this, 40);
        }
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
