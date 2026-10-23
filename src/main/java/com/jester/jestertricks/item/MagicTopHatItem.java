package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Chistera: "¡Y ahora... sacaré algo de la chistera!"
 * 40 %: un conejo. 40 %: un objeto útil al azar (a veces muy bueno). 20 %: una nube de murciélagos.
 */
public class MagicTopHatItem extends Item {
    private static final Item[] COMMON = {
            Items.GOLDEN_APPLE, Items.ENDER_PEARL, Items.CAKE, Items.FIREWORK_ROCKET, Items.DIAMOND,
            Items.EMERALD, Items.GOLDEN_CARROT, Items.EXPERIENCE_BOTTLE, Items.BLAZE_ROD, Items.COOKIE
    };
    private static final Item[] RARE = {
            Items.ENCHANTED_GOLDEN_APPLE, Items.TOTEM_OF_UNDYING, Items.NETHERITE_INGOT, Items.NETHER_STAR
    };

    public MagicTopHatItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide && level instanceof ServerLevel sl) {
            Vec3 look = player.getLookAngle();
            Vec3 spot = player.getEyePosition().add(look.scale(1.2)).subtract(0, 0.4, 0);
            Vec3 toss = look.scale(0.35).add(0, 0.35, 0);
            float roll = level.random.nextFloat();

            if (roll < 0.4F) {
                Entity rabbit = EntityType.RABBIT.create(level);
                if (rabbit != null) {
                    rabbit.moveTo(spot.x, spot.y, spot.z, player.getYRot(), 0.0F);
                    rabbit.setDeltaMovement(toss);
                    level.addFreshEntity(rabbit);
                }
            } else if (roll < 0.8F) {
                Item pick = level.random.nextFloat() < 0.1F
                        ? RARE[level.random.nextInt(RARE.length)]
                        : COMMON[level.random.nextInt(COMMON.length)];
                ItemEntity drop = new ItemEntity(level, spot.x, spot.y, spot.z, new ItemStack(pick));
                drop.setDeltaMovement(toss);
                drop.setPickUpDelay(10);
                level.addFreshEntity(drop);
            } else {
                for (int i = 0; i < 5; i++) {
                    Entity bat = EntityType.BAT.create(level);
                    if (bat != null) {
                        bat.moveTo(spot.x, spot.y + 0.3, spot.z, level.random.nextFloat() * 360F, 0.0F);
                        level.addFreshEntity(bat);
                    }
                }
                Fx.play(level, spot, ModSounds.JESTER_LAUGH.get(), 0.8F, Fx.pitch(level, 1.2F, 0.2F));
            }

            sl.sendParticles(ParticleTypes.POOF, spot.x, spot.y, spot.z, 20, 0.25, 0.25, 0.25, 0.05);
            sl.sendParticles(ParticleTypes.WITCH, spot.x, spot.y, spot.z, 12, 0.3, 0.3, 0.3, 0.1);
            Fx.confetti(sl, spot, 6, 0.4);
            Fx.play(level, spot, ModSounds.MAGIC_POOF.get(), 1.0F, Fx.pitch(level, 1.0F, 0.2F));
            player.getCooldowns().addCooldown(this, 40);
        }
        player.swing(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 2);
    }
}
