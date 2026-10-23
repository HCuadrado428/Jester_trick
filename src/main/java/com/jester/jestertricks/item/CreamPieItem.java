package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.entity.CreamPieEntity;
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

/** Tarta en la Cara (infinitas): ciega, marea y ralentiza al que la recibe. */
public class CreamPieItem extends Item {
    public CreamPieItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide) {
            CreamPieEntity pie = new CreamPieEntity(level, player);
            pie.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.6F, 0.3F);
            level.addFreshEntity(pie);
            Fx.play(level, player.position(), ModSounds.CARD_THROW.get(), 0.9F, 0.6F);
            player.getCooldowns().addCooldown(this, JesterConfig.cooldown(30));
        }
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
