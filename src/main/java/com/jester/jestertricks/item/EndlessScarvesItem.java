package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.entity.ScarfHookEntity;
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
 * Pañuelos Infinitos: el truco del pañuelo que nunca se acaba, convertido en gancho.
 * Si el nudo se engancha a un bloque, tira de ti hasta allí (sin daño de caída).
 * Si golpea a una criatura, la arrastra hacia ti. Agáchate para soltarte.
 */
public class EndlessScarvesItem extends Item {
    public EndlessScarvesItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide) {
            ScarfHookEntity hook = new ScarfHookEntity(level, player);
            hook.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.6F, 0.0F);
            level.addFreshEntity(hook);
            Fx.play(level, player.position(), ModSounds.CARD_THROW.get(), 0.9F, 0.6F);
            player.getCooldowns().addCooldown(this, 20);
        }
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
