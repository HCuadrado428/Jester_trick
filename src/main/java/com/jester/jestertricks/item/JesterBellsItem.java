package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.registry.ModEffects;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Cascabeles: al sonar, todo lo que hay a 10 bloques se pone a bailar (vueltas y saltitos),
 * y cualquiera que fuera invisible deja de serlo y brilla.
 */
public class JesterBellsItem extends Item {
    public JesterBellsItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide && level instanceof ServerLevel sl) {
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10.0),
                    e -> e != player && e.isAlive())) {
                if (e.hasEffect(MobEffects.INVISIBILITY)) e.removeEffect(MobEffects.INVISIBILITY);
                e.setInvisible(false);
                e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 300, 0), player);
                e.addEffect(new MobEffectInstance(ModEffects.DANCING.get(), 120, 0), player);
                sl.sendParticles(ParticleTypes.NOTE, e.getX(), e.getY() + e.getBbHeight() + 0.4, e.getZ(), 3, 0.3, 0.2, 0.3, 1.0);
            }
            for (int i = 0; i < 16; i++) {
                double a = Math.PI * 2 * i / 16;
                sl.sendParticles(ParticleTypes.NOTE, player.getX() + Math.cos(a) * 2, player.getY() + 1.5, player.getZ() + Math.sin(a) * 2, 1, 0, 0, 0, i / 16.0);
            }
            Fx.play(level, player.position(), ModSounds.BELLS.get(), 1.2F, Fx.pitch(level, 1.0F, 0.15F));
            player.getCooldowns().addCooldown(this, 160);
        }
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
