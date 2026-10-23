package com.jester.jestertricks.item;

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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Bomba de Humo: ¡puf, desaparecido!
 * Te da Invisibilidad y Velocidad III 10 s, ciega a todo lo que haya a 8 bloques
 * y hace que los monstruos que te perseguían te pierdan de vista.
 */
public class SmokeBombItem extends Item {
    public SmokeBombItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide && level instanceof ServerLevel sl) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 2, false, false, true));

            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(8.0),
                    e -> e != player && e.isAlive())) {
                e.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 120, 0), player);
                e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1), player);
                if (e instanceof Mob mob && mob.getTarget() == player) {
                    mob.setTarget(null);
                }
            }

            Vec3 c = player.position();
            sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, c.x, c.y + 0.8, c.z, 40, 1.2, 0.8, 1.2, 0.02);
            sl.sendParticles(ParticleTypes.LARGE_SMOKE, c.x, c.y + 1.0, c.z, 80, 1.5, 1.0, 1.5, 0.03);
            sl.sendParticles(ParticleTypes.POOF, c.x, c.y + 1.0, c.z, 30, 0.8, 0.8, 0.8, 0.05);
            Fx.play(level, c, ModSounds.MAGIC_POOF.get(), 1.2F, 0.7F);
            Fx.play(level, c, ModSounds.JESTER_LAUGH.get(), 0.6F, Fx.pitch(level, 1.1F, 0.2F));
            player.getCooldowns().addCooldown(this, JesterConfig.cooldown(200));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
