package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.entity.JugglingPinEntity;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Bolos de Malabares: lanza 3 bolos que rebotan de un enemigo a otro (6 de daño por golpe). */
public class JugglingPinsItem extends Item {
    public JugglingPinsItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide) {
            for (int i = -1; i <= 1; i++) {
                JugglingPinEntity pin = new JugglingPinEntity(level, player);
                pin.shootFromRotation(player, player.getXRot() - 4.0F, player.getYRot() + i * 12.0F, 0.0F, 1.7F, 0.5F);
                level.addFreshEntity(pin);
            }
            Fx.play(level, player.position(), ModSounds.CARD_THROW.get(), 1.0F, 0.8F);
            player.getCooldowns().addCooldown(this, JesterConfig.cooldown(40));
        }
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
