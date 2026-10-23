package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.entity.ThrownCardEntity;
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

/**
 * Naipes Afilados (infinitos).
 * Clic derecho: abanico de 3 cartas (7 de daño cada una).
 * Agachado + clic derecho: una sola carta "as de picas" (16 de daño, más rápida).
 */
public class SharpCardsItem extends Item {
    public SharpCardsItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                throwCard(level, player, 0.0F, JesterConfig.damage(16.0F), 3.4F);
                player.getCooldowns().addCooldown(this, JesterConfig.cooldown(16));
            } else {
                for (int i = -1; i <= 1; i++) {
                    throwCard(level, player, i * 9.0F, JesterConfig.damage(7.0F), 2.8F);
                }
                player.getCooldowns().addCooldown(this, JesterConfig.cooldown(12));
            }
            Fx.play(level, player.position(), ModSounds.CARD_THROW.get(), 0.9F, Fx.pitch(level, 1.1F, 0.3F));
        }
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private static void throwCard(Level level, Player player, float yawOffset, float damage, float speed) {
        ThrownCardEntity card = new ThrownCardEntity(level, player, damage);
        card.shootFromRotation(player, player.getXRot(), player.getYRot() + yawOffset, 0.0F, speed, 0.4F);
        level.addFreshEntity(card);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
