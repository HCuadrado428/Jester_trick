package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.registry.ModEffects;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * Baraja del Destino (no es la de lanzar: esta se "lee").
 * Clic derecho sobre otro jugador o criatura: le sale una carta MALA al azar.
 * Clic derecho al aire: el Jester saca una carta para sí mismo, siempre BUENA.
 */
public class FateDeckItem extends Item {
    private record Card(String id, Supplier<MobEffect> effect, int ticks, int amp) {}

    private static final Card[] GOOD = {
            new Card("strength", () -> MobEffects.DAMAGE_BOOST, 600, 1),
            new Card("speed", () -> MobEffects.MOVEMENT_SPEED, 600, 1),
            new Card("regeneration", () -> MobEffects.REGENERATION, 300, 1),
            new Card("resistance", () -> MobEffects.DAMAGE_RESISTANCE, 600, 1),
            new Card("absorption", () -> MobEffects.ABSORPTION, 600, 2),
            new Card("fire_resistance", () -> MobEffects.FIRE_RESISTANCE, 1200, 0),
            new Card("haste", () -> MobEffects.DIG_SPEED, 600, 1),
            new Card("jump", () -> MobEffects.JUMP, 600, 1),
            new Card("health_boost", () -> MobEffects.HEALTH_BOOST, 1200, 2),
            new Card("night_vision", () -> MobEffects.NIGHT_VISION, 2400, 0),
    };

    private static final Card[] BAD = {
            new Card("pranked", ModEffects.PRANKED, 300, 0),
            new Card("levitation", () -> MobEffects.LEVITATION, 60, 1),
            new Card("glowing", () -> MobEffects.GLOWING, 600, 0),
            new Card("slowness", () -> MobEffects.MOVEMENT_SLOWDOWN, 200, 2),
            new Card("mining_fatigue", () -> MobEffects.DIG_SLOWDOWN, 400, 1),
            new Card("nausea", () -> MobEffects.CONFUSION, 240, 0),
            new Card("blindness", () -> MobEffects.BLINDNESS, 120, 0),
            new Card("weakness", () -> MobEffects.WEAKNESS, 400, 1),
            new Card("hunger", () -> MobEffects.HUNGER, 400, 2),
            new Card("dancing", ModEffects.DANCING, 160, 0),
            new Card("teleport", null, 0, 0),
    };

    public FateDeckItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack ignored, Player player, LivingEntity target, InteractionHand hand) {
        if (!JesterLock.check(player)) return InteractionResult.FAIL;
        if (player.level().isClientSide) return InteractionResult.SUCCESS;
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResult.FAIL;

        Card card = BAD[player.getRandom().nextInt(BAD.length)];
        if (card.effect() == null) {
            for (int i = 0; i < 16; i++) {
                double x = target.getX() + (player.getRandom().nextDouble() - 0.5) * 16;
                double z = target.getZ() + (player.getRandom().nextDouble() - 0.5) * 16;
                if (target.randomTeleport(x, target.getY() + player.getRandom().nextInt(4) - 1, z, true)) break;
            }
        } else {
            target.addEffect(new MobEffectInstance(card.effect().get(), card.ticks(), card.amp()), player);
        }
        announce(player, target, "bad", card.id());
        player.getCooldowns().addCooldown(this, 60);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide) {
            Card card = GOOD[player.getRandom().nextInt(GOOD.length)];
            player.addEffect(new MobEffectInstance(card.effect().get(), card.ticks(), card.amp()));
            if (card.id().equals("health_boost")) player.heal(8.0F);
            announce(player, player, "good", card.id());
            player.getCooldowns().addCooldown(this, 60);
        }
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    private static void announce(Player jester, LivingEntity target, String kind, String id) {
        Component cardName = Component.translatable("card.jestertricks." + kind + "." + id)
                .withStyle(kind.equals("good") ? ChatFormatting.WHITE : ChatFormatting.DARK_GRAY, ChatFormatting.BOLD);
        jester.displayClientMessage(Component.translatable("message.jestertricks.card_drawn", cardName), true);
        if (target instanceof Player other && other != jester) {
            other.displayClientMessage(Component.translatable("message.jestertricks.card_received", cardName), true);
        }
        if (jester.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.ENCHANT, target.getX(), target.getY(1.0) + 0.3, target.getZ(), 30, 0.4, 0.4, 0.4, 0.6);
            Fx.confetti(sl, target.position().add(0, target.getBbHeight() + 0.3, 0), 6, 0.3);
        }
        Fx.play(jester.level(), target.position(), ModSounds.CARD_THROW.get(), 0.8F, 1.6F);
        if (kind.equals("bad")) {
            Fx.play(jester.level(), target.position(), ModSounds.JESTER_LAUGH.get(), 0.7F, 1.25F);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
