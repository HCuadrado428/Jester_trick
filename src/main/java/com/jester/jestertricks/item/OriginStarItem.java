package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.entity.RiftEntity;
import com.jester.jestertricks.portal.OriginTravel;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Estrella del Origen: un trozo del mundo del que vino el Jester.
 * Clic derecho: abre una pequeña grieta que le lleva a otra dimensión (posición equivalente).
 * Agachado + clic derecho: elige el destino (mundo normal, Nether, End).
 */
public class OriginStarItem extends Item {
    private static final String DEST = "Destination";

    public OriginStarItem(Properties props) {
        super(props);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    public static ResourceKey<Level> destination(ItemStack stack, Level current) {
        if (stack.hasTag() && stack.getTag().contains(DEST)) {
            ResourceKey<Level> d = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(stack.getTag().getString(DEST)));
            if (d != current.dimension()) return d;
        }
        return current.dimension() == Level.OVERWORLD ? Level.NETHER : Level.OVERWORLD;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        if (player.isShiftKeyDown()) {
            // siguiente destino distinto de la dimensión actual
            ResourceKey<Level> cur = destination(stack, level);
            int i = OriginTravel.DESTINATIONS.indexOf(cur);
            ResourceKey<Level> next = cur;
            for (int k = 1; k <= OriginTravel.DESTINATIONS.size(); k++) {
                next = OriginTravel.DESTINATIONS.get((i + k) % OriginTravel.DESTINATIONS.size());
                if (next != level.dimension()) break;
            }
            stack.getOrCreateTag().putString(DEST, next.location().toString());
            player.displayClientMessage(Component.translatable("message.jestertricks.destination",
                    Component.translatable(OriginTravel.nameKey(next))).withStyle(ChatFormatting.WHITE), true);
            Fx.play(level, player.position(), ModSounds.MAGIC_POOF.get(), 0.6F, 1.6F);
            return InteractionResultHolder.success(stack);
        }

        Vec3 look = player.getLookAngle();
        Vec3 at = player.position().add(look.x * 1.8, 1.0, look.z * 1.8);
        level.addFreshEntity(RiftEntity.open(level, player, at, destination(stack, level)));
        Fx.play(level, at, ModSounds.RIFT.get(), 1.0F, 0.8F);
        player.getCooldowns().addCooldown(this, JesterConfig.cooldown(60));
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
        if (level != null) {
            tooltip.add(Component.translatable("message.jestertricks.destination",
                    Component.translatable(OriginTravel.nameKey(destination(stack, level)))).withStyle(ChatFormatting.DARK_PURPLE));
        }
    }
}
