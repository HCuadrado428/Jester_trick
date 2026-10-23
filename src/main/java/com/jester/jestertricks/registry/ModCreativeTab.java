package com.jester.jestertricks.registry;

import com.jester.jestertricks.JesterTricks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/** La única forma de conseguir los objetos: esta pestaña del modo creativo. */
public final class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, JesterTricks.MOD_ID);

    public static final RegistryObject<CreativeModeTab> JESTER_TAB = TABS.register("jester_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.jestertricks"))
                    .icon(() -> new ItemStack(ModItems.ORIGIN_STAR.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.JESTER_SCEPTER.get());
                        output.accept(ModItems.SHARP_CARDS.get());
                        output.accept(ModItems.JUGGLER_KNIVES.get());
                        output.accept(ModItems.RUBBER_MALLET.get());
                        output.accept(ModItems.BANG_PISTOL.get());
                        output.accept(ModItems.MAGIC_TOP_HAT.get());
                        output.accept(ModItems.MAGIC_WAND.get());
                        output.accept(ModItems.SMOKE_BOMB.get());
                        output.accept(ModItems.ENDLESS_SCARVES.get());
                        output.accept(ModItems.SQUIRTING_FLOWER.get());
                        output.accept(ModItems.SURPRISE_BOX.get());
                        output.accept(ModItems.WHOOPEE_CUSHION.get());
                        output.accept(ModItems.ORIGIN_STAR.get());
                        output.accept(ModItems.FAIR_HOOP.get());
                        output.accept(ModItems.BLACK_BOX.get());
                        output.accept(ModItems.FATE_DECK.get());
                        output.accept(ModItems.LAUGH_MIRROR.get());
                        output.accept(ModItems.JESTER_BELLS.get());
                    })
                    .build());

    private ModCreativeTab() {}
}
