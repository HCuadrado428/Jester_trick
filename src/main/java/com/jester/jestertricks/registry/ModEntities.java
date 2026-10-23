package com.jester.jestertricks.registry;

import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.entity.BangFlagEntity;
import com.jester.jestertricks.entity.ClownBalloonEntity;
import com.jester.jestertricks.entity.CreamPieEntity;
import com.jester.jestertricks.entity.JugglingPinEntity;
import com.jester.jestertricks.entity.JesterCloneEntity;
import com.jester.jestertricks.entity.RiftEntity;
import com.jester.jestertricks.entity.ScarfHookEntity;
import com.jester.jestertricks.entity.ThrownCardEntity;
import com.jester.jestertricks.entity.ThrownKnifeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, JesterTricks.MOD_ID);

    public static final RegistryObject<EntityType<ThrownCardEntity>> THROWN_CARD = ENTITIES.register("thrown_card",
            () -> EntityType.Builder.<ThrownCardEntity>of(ThrownCardEntity::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F).clientTrackingRange(6).updateInterval(2).build("thrown_card"));

    public static final RegistryObject<EntityType<ThrownKnifeEntity>> THROWN_KNIFE = ENTITIES.register("thrown_knife",
            () -> EntityType.Builder.<ThrownKnifeEntity>of(ThrownKnifeEntity::new, MobCategory.MISC)
                    .sized(0.35F, 0.35F).clientTrackingRange(6).updateInterval(1).build("thrown_knife"));

    public static final RegistryObject<EntityType<BangFlagEntity>> BANG_FLAG = ENTITIES.register("bang_flag",
            () -> EntityType.Builder.<BangFlagEntity>of(BangFlagEntity::new, MobCategory.MISC)
                    .sized(0.35F, 0.35F).clientTrackingRange(6).updateInterval(2).build("bang_flag"));

    public static final RegistryObject<EntityType<ScarfHookEntity>> SCARF_HOOK = ENTITIES.register("scarf_hook",
            () -> EntityType.Builder.<ScarfHookEntity>of(ScarfHookEntity::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F).clientTrackingRange(6).updateInterval(1).build("scarf_hook"));

    public static final RegistryObject<EntityType<RiftEntity>> RIFT = ENTITIES.register("rift",
            () -> EntityType.Builder.<RiftEntity>of(RiftEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.5F).clientTrackingRange(8).updateInterval(20).build("rift"));

    public static final RegistryObject<EntityType<JesterCloneEntity>> JESTER_CLONE = ENTITIES.register("jester_clone",
            () -> EntityType.Builder.<JesterCloneEntity>of(JesterCloneEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F).clientTrackingRange(8).build("jester_clone"));

    public static final RegistryObject<EntityType<JugglingPinEntity>> JUGGLING_PIN = ENTITIES.register("juggling_pin",
            () -> EntityType.Builder.<JugglingPinEntity>of(JugglingPinEntity::new, MobCategory.MISC)
                    .sized(0.35F, 0.35F).clientTrackingRange(6).updateInterval(1).build("juggling_pin"));

    public static final RegistryObject<EntityType<CreamPieEntity>> CREAM_PIE = ENTITIES.register("cream_pie",
            () -> EntityType.Builder.<CreamPieEntity>of(CreamPieEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F).clientTrackingRange(6).updateInterval(2).build("cream_pie"));

    public static final RegistryObject<EntityType<ClownBalloonEntity>> CLOWN_BALLOON = ENTITIES.register("clown_balloon",
            () -> EntityType.Builder.<ClownBalloonEntity>of(ClownBalloonEntity::new, MobCategory.MISC)
                    .sized(0.6F, 0.8F).clientTrackingRange(8).updateInterval(1).build("clown_balloon"));

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(JESTER_CLONE.get(), JesterCloneEntity.createAttributes().build());
    }

    private ModEntities() {}
}
