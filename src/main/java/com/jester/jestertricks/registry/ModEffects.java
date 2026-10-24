package com.jester.jestertricks.registry;

import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.effect.DancingEffect;
import com.jester.jestertricks.effect.PrankedEffect;
import com.jester.jestertricks.effect.ShadowStepEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, JesterTricks.MOD_ID);

    /** "Pranked!": anula los encantamientos de la armadura mientras dura. */
    public static final RegistryObject<MobEffect> PRANKED = EFFECTS.register("pranked", PrankedEffect::new);

    /** "¡A bailar!": vueltas y saltitos (Cascabeles y Baraja del Destino). */
    public static final RegistryObject<MobEffect> DANCING = EFFECTS.register("dancing", DancingEffect::new);

    /** Paso de sombra: los golpes atraviesan al Jester durante un momento. */
    /** Forma real del Jester (tecla H). */

    public static final RegistryObject<MobEffect> SHADOW_STEP = EFFECTS.register("shadow_step", ShadowStepEffect::new);

    private ModEffects() {}
}
