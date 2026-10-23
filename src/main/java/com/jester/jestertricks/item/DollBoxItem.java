package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.entity.HarlequinDollEntity;
import com.jester.jestertricks.registry.ModEntities;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
 * Caja de Muñecos: clic derecho y salen 3 muñecos arlequín que siguen al Jester y pelean por él.
 * Agachado + clic derecho: vuelven a la caja. Si ya había muñecos fuera, se sustituyen.
 */
public class DollBoxItem extends Item {
    public static final int DOLLS = 3;

    public DollBoxItem(Properties props) {
        super(props);
    }

    private static List<HarlequinDollEntity> dollsOf(Player owner) {
        List<HarlequinDollEntity> out = new ArrayList<>();
        if (owner.getServer() == null) return out;
        for (ServerLevel l : owner.getServer().getAllLevels()) {
            for (var e : l.getEntities(ModEntities.HARLEQUIN_DOLL.get(), d -> owner.getUUID().equals(d.getOwnerUUID()))) {
                out.add(e);
            }
        }
        return out;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        List<HarlequinDollEntity> old = dollsOf(player);
        old.forEach(HarlequinDollEntity::poof);
        if (player.isShiftKeyDown()) {
            player.displayClientMessage(Component.translatable("message.jestertricks.dolls_back").withStyle(ChatFormatting.GRAY), true);
            Fx.play(level, player.position(), ModSounds.JACK_BOX.get(), 0.6F, 1.5F);
            return InteractionResultHolder.success(stack);
        }

        for (int i = 0; i < DOLLS; i++) {
            double a = Math.toRadians(player.getYRot() + 90 + (i - 1) * 50);
            Vec3 at = player.position().add(-Math.sin(a) * 1.6, 0.1, Math.cos(a) * 1.6);
            HarlequinDollEntity doll = HarlequinDollEntity.create(level, player, at);
            level.addFreshEntity(doll);
            if (level instanceof ServerLevel sl) Fx.confetti(sl, at.add(0, 0.5, 0), 6, 0.3);
        }
        Fx.play(level, player.position(), ModSounds.JACK_BOX.get(), 1.0F, 1.0F);
        Fx.play(level, player.position(), ModSounds.JESTER_LAUGH.get(), 0.6F, 1.8F);
        player.getCooldowns().addCooldown(this, JesterConfig.cooldown(60));
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
