package com.jester.jestertricks;

import com.jester.jestertricks.registry.ModBlocks;
import com.jester.jestertricks.registry.ModCreativeTab;
import com.jester.jestertricks.registry.ModEffects;
import com.jester.jestertricks.registry.ModEntities;
import com.jester.jestertricks.registry.ModItems;
import com.jester.jestertricks.registry.ModParticles;
import com.jester.jestertricks.network.JesterNetwork;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * Jester's Tricks — armas y objetos del Jester.
 * Todo es solo de creativo: no hay recetas ni botín.
 */
@Mod(JesterTricks.MOD_ID)
public class JesterTricks {
    public static final String MOD_ID = "jestertricks";

    public JesterTricks() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModSounds.SOUNDS.register(modBus);
        ModEffects.EFFECTS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModCreativeTab.TABS.register(modBus);
        ModParticles.PARTICLES.register(modBus);
        JesterNetwork.register();
        modBus.addListener(ModEntities::registerAttributes);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, JesterConfig.SPEC);
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
