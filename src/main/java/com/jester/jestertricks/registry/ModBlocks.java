package com.jester.jestertricks.registry;

import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.block.HoopBlock;
import com.jester.jestertricks.block.SurpriseBoxBlock;
import com.jester.jestertricks.block.WhoopeeCushionBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, JesterTricks.MOD_ID);

    public static final RegistryObject<Block> SURPRISE_BOX = BLOCKS.register("surprise_box",
            () -> new SurpriseBoxBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)));

    public static final RegistryObject<Block> WHOOPEE_CUSHION = BLOCKS.register("whoopee_cushion",
            () -> new WhoopeeCushionBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.2F)
                    .sound(SoundType.WOOL)
                    .noCollission()
                    .noOcclusion()));

    /** Aro de Feria: lo coloca el objeto Aro de Feria. Solo se rompe en creativo. */
    public static final RegistryObject<Block> HOOP = BLOCKS.register("fair_hoop",
            () -> new HoopBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(-1.0F, 3600000.0F)
                    .noCollission()
                    .noOcclusion()
                    .noLootTable()
                    .lightLevel(s -> 6)));

    private ModBlocks() {}
}
