package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.block.HoopBlock;
import com.jester.jestertricks.portal.HoopAnchors;
import com.jester.jestertricks.registry.ModBlocks;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Aro de Feria.
 * Agachado + clic derecho en el suelo: coloca el aro FIJO (si ya había uno, se mueve aquí).
 * Clic derecho en el suelo: coloca un aro temporal. Al saltar por él, el Jester aparece en el fijo.
 */
public class FairHoopItem extends Item {
    public FairHoopItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        Level level = ctx.getLevel();
        if (player == null) return InteractionResult.PASS;
        if (!JesterLock.check(player)) return InteractionResult.FAIL;

        BlockPlaceContext place = new BlockPlaceContext(ctx);
        BlockPos pos = place.getClickedPos();
        if (!place.canPlace()) return InteractionResult.FAIL;
        if (level.isClientSide) return InteractionResult.SUCCESS;

        boolean permanent = player.isShiftKeyDown();
        BlockState state = ModBlocks.HOOP.get().defaultBlockState()
                .setValue(HoopBlock.FACING, player.getDirection().getOpposite())
                .setValue(HoopBlock.PERMANENT, permanent);

        if (permanent) {
            HoopAnchors anchors = HoopAnchors.get(level.getServer());
            GlobalPos old = anchors.get(player.getUUID());
            if (old != null) {
                ServerLevel oldLevel = level.getServer().getLevel(old.dimension());
                if (oldLevel != null && oldLevel.isLoaded(old.pos()) && oldLevel.getBlockState(old.pos()).is(ModBlocks.HOOP.get())) {
                    oldLevel.removeBlock(old.pos(), false);
                }
            }
            level.setBlockAndUpdate(pos, state);
            anchors.set(player.getUUID(), GlobalPos.of(level.dimension(), pos));
            player.displayClientMessage(Component.translatable("message.jestertricks.anchor_set").withStyle(ChatFormatting.WHITE), true);
        } else {
            level.setBlockAndUpdate(pos, state);
            boolean hasAnchor = HoopAnchors.get(level.getServer()).get(player.getUUID()) != null;
            player.displayClientMessage(Component.translatable(hasAnchor ? "message.jestertricks.hoop_temp" : "message.jestertricks.no_anchor")
                    .withStyle(hasAnchor ? ChatFormatting.WHITE : ChatFormatting.GRAY), true);
        }

        Vec3 c = Vec3.atCenterOf(pos).add(0, 0.5, 0);
        if (level instanceof ServerLevel sl) {
            Fx.rift(sl, c, player.getYRot(), 0.8);
            Fx.confetti(sl, c, 8, 0.5);
        }
        Fx.play(level, c, ModSounds.JACK_BOX.get(), 0.6F, permanent ? 0.8F : 1.2F);
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, 3);
    }
}
