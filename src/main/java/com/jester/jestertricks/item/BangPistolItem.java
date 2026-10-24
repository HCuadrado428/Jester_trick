package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.entity.BangFlagEntity;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Pistola BANG!: dispara banderitas de "BANG!".
 * Al impactar explotan: 4 corazones que atraviesan armadura (nunca rematan)
 * y aplican el efecto "Pranked!" durante 10 segundos.
 */
public class BangPistolItem extends Item {
    public BangPistolItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!JesterLock.check(player)) return InteractionResultHolder.fail(stack);
        if (!level.isClientSide) {
            BangFlagEntity flag = new BangFlagEntity(level, player);
            flag.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.0F, 0.1F);
            level.addFreshEntity(flag);
            Fx.play(level, player.position(), ModSounds.BANG_SHOT.get(), 1.0F, Fx.pitch(level, 1.0F, 0.2F));
            player.getCooldowns().addCooldown(this, JesterConfig.cooldown(12));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        // La postura se crea ya, al cargar el objeto: si se crea después de que Minecraft dibuje
        // el primer jugador, el juego no la conoce y se cuelga.
        final net.minecraft.client.model.HumanoidModel.ArmPose aim = com.jester.jestertricks.client.PistolArmPose.AIM;
        consumer.accept(new IClientItemExtensions() {
            @Override
            public net.minecraft.client.model.HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
                return aim;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 3);
    }
}
