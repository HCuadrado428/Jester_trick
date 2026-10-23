package com.jester.jestertricks.registry;

import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.block.CircusCannonBlock;
import com.jester.jestertricks.block.FairSignBlock;
import com.jester.jestertricks.block.HoopBlock;
import com.jester.jestertricks.block.SurpriseBoxBlock;
import com.jester.jestertricks.block.WhoopeeCushionBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
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
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

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

    // --- Trucos ---
    public static final RegistryObject<Block> CIRCUS_CANNON = BLOCKS.register("circus_cannon",
            () -> new CircusCannonBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK).strength(2.0F, 6.0F).sound(SoundType.METAL).noOcclusion()));

    // --- Decoración ---
    public static final RegistryObject<Block> HARLEQUIN_BLOCK = BLOCKS.register("harlequin_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> HARLEQUIN_SLAB = BLOCKS.register("harlequin_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(HARLEQUIN_BLOCK.get())));
    public static final RegistryObject<Block> HARLEQUIN_STAIRS = BLOCKS.register("harlequin_stairs",
            () -> new StairBlock(() -> HARLEQUIN_BLOCK.get().defaultBlockState(), BlockBehaviour.Properties.copy(HARLEQUIN_BLOCK.get())));

    public static final RegistryObject<Block> CIRCUS_TENT = BLOCKS.register("circus_tent",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(0.8F).sound(SoundType.WOOL)));
    public static final RegistryObject<Block> CIRCUS_TENT_SLAB = BLOCKS.register("circus_tent_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(CIRCUS_TENT.get())));

    public static final RegistryObject<Block> FAIR_LANTERN = BLOCKS.register("fair_lantern",
            () -> new LanternBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(1.0F)
                    .sound(SoundType.LANTERN).lightLevel(s -> 15).noOcclusion()));

    public static final RegistryObject<Block> FAIR_SIGN = BLOCKS.register("fair_sign",
            () -> new FairSignBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0F)
                    .sound(SoundType.WOOD).noOcclusion().lightLevel(s -> 7)));

    private ModBlocks() {}
}
