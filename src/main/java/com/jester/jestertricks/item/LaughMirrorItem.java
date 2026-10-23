package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.entity.JesterCloneEntity;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Espejo de la Risa: tres copias del Jester salen del espejo (con su skin).
 * Los monstruos cercanos se lanzan a por las copias, que estallan en confeti al golpearlas.
 */
public class LaughMirrorItem extends Item {
    public LaughMirrorItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide && level instanceof ServerLevel sl) {
            List<JesterCloneEntity> clones = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                double a = Math.toRadians(player.getYRot() + 90 + (i - 1) * 70);
                Vec3 at = player.position().add(-Math.sin(a) * 2.0, 0, Math.cos(a) * 2.0);
                JesterCloneEntity clone = JesterCloneEntity.create(level, player, at);
                level.addFreshEntity(clone);
                clones.add(clone);
                Fx.confetti(sl, at.add(0, 1, 0), 6, 0.4);
                Fx.rift(sl, at.add(0, 1, 0), player.getYRot(), 0.7);
            }
            for (Mob mob : level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(16.0),
                    m -> m instanceof Enemy && m.isAlive())) {
                mob.setTarget(clones.get(level.random.nextInt(clones.size())));
            }
            Fx.play(level, player.position(), ModSounds.MIRROR.get(), 1.0F, 1.0F);
            Fx.play(level, player.position(), ModSounds.JESTER_LAUGH.get(), 0.8F, 1.1F);
            player.getCooldowns().addCooldown(this, JesterConfig.cooldown(300));
        }
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
