package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.entity.ClownBalloonEntity;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Globo de Payaso: clic derecho sobre una criatura o jugador para atarle un globo que lo eleva.
 * Clic derecho al aire: te lo atas a ti mismo.
 */
public class ClownBalloonItem extends Item {
    public ClownBalloonItem(Properties props) {
        super(props);
    }

    private boolean tie(Player player, LivingEntity target) {
        if (player.getCooldowns().isOnCooldown(this)) return false;
        Level level = player.level();
        level.addFreshEntity(ClownBalloonEntity.tie(level, target));
        Fx.play(level, target.position(), ModSounds.SQUIRT.get(), 0.6F, 1.8F);
        Fx.play(level, target.position(), ModSounds.JESTER_LAUGH.get(), 0.5F, 1.4F);
        player.getCooldowns().addCooldown(this, JesterConfig.cooldown(100));
        return true;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack ignored, Player player, LivingEntity target, InteractionHand hand) {
        if (!JesterLock.check(player)) return InteractionResult.FAIL;
        if (player.level().isClientSide) return InteractionResult.SUCCESS;
        return tie(player, target) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide) tie(player, player);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
