package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.entity.ThrownKnifeEntity;
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
 * Cuchillos de Malabarista: se lanzan uno tras otro (18 de daño)
 * y vuelven volando a la mano como un bumerán, atravesando paredes.
 */
public class JugglerKnivesItem extends Item {
    public JugglerKnivesItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide) {
            ThrownKnifeEntity knife = new ThrownKnifeEntity(level, player, JesterConfig.damage(12.0F));
            knife.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.6F, 0.2F);
            level.addFreshEntity(knife);
            Fx.play(level, player.position(), ModSounds.CARD_THROW.get(), 0.8F, Fx.pitch(level, 0.75F, 0.2F));
            player.getCooldowns().addCooldown(this, JesterConfig.cooldown(10));
        }
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
