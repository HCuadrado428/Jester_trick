package com.jester.jestertricks.registry;

import com.jester.jestertricks.JesterTricks;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, JesterTricks.MOD_ID);

    public static final RegistryObject<SoundEvent> JESTER_LAUGH = register("jester_laugh");
    public static final RegistryObject<SoundEvent> BOING = register("boing");
    public static final RegistryObject<SoundEvent> BANG = register("bang");
    public static final RegistryObject<SoundEvent> BANG_SHOT = register("bang_shot");
    public static final RegistryObject<SoundEvent> CARD_THROW = register("card_throw");
    public static final RegistryObject<SoundEvent> MAGIC_POOF = register("magic_poof");
    public static final RegistryObject<SoundEvent> FART = register("fart");
    public static final RegistryObject<SoundEvent> SQUIRT = register("squirt");
    public static final RegistryObject<SoundEvent> JACK_BOX = register("jack_box");
    public static final RegistryObject<SoundEvent> RIFT = register("rift");
    public static final RegistryObject<SoundEvent> BELLS = register("bells");
    public static final RegistryObject<SoundEvent> MIRROR = register("mirror");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(JesterTricks.id(name)));
    }

    private ModSounds() {}
}
