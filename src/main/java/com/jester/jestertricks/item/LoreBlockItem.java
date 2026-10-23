package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterLock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LoreBlockItem extends BlockItem {
    private final int loreLines;

    public LoreBlockItem(Block block, Properties props, int loreLines) {
        super(block, props);
        this.loreLines = loreLines;
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        if (context.getPlayer() != null && !JesterLock.check(context.getPlayer())) return InteractionResult.FAIL;
        return super.place(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        Lore.add(this, tooltip, loreLines);
    }
}
