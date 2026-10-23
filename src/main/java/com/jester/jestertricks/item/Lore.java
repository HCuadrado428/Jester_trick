package com.jester.jestertricks.item;

import com.jester.jestertricks.JesterTricks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/** Añade las líneas de descripción "tooltip.jestertricks.<objeto>.<n>" traducidas. */
public final class Lore {
    public static void add(Item item, List<Component> tooltip, int lines) {
        var key = ForgeRegistries.ITEMS.getKey(item);
        String name = key == null ? "unknown" : key.getPath();
        for (int i = 0; i < lines; i++) {
            tooltip.add(Component.translatable("tooltip." + JesterTricks.MOD_ID + "." + name + "." + i)
                    .withStyle(i == 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_GRAY));
        }
    }

    private Lore() {}
}
