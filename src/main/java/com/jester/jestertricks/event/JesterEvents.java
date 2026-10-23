package com.jester.jestertricks.event;

import com.jester.jestertricks.JesterConfig;
import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.JesterTricks;
import com.jester.jestertricks.registry.ModEffects;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/** Lógica del efecto "Pranked!": la armadura del afectado pierde sus encantamientos. */
@Mod.EventBusSubscriber(modid = JesterTricks.MOD_ID)
public final class JesterEvents {

    /**
     * Minecraft reduce el daño con los encantamientos de protección DESPUÉS de este evento
     * (daño x (1 - protección/25)). Aquí lo compensamos por adelantado, así que
     * el resultado es exactamente el daño que harías sin esos encantamientos.
     */
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        // los golpes cuerpo a cuerpo con armas del Jester siguen el multiplicador de daño
        if (event.getSource().getDirectEntity() instanceof Player p && event.getSource().getEntity() == p) {
            var key = ForgeRegistries.ITEMS.getKey(p.getMainHandItem().getItem());
            if (key != null && JesterTricks.MOD_ID.equals(key.getNamespace())) {
                event.setAmount(JesterConfig.damage(event.getAmount()));
            }
        }
        if (!target.hasEffect(ModEffects.PRANKED.get())) return;
        DamageSource source = event.getSource();
        if (source.is(DamageTypeTags.BYPASSES_ENCHANTMENTS)) return;
        int protection = EnchantmentHelper.getDamageProtection(target.getArmorSlots(), source);
        if (protection <= 0) return;
        float factor = 1.0F - Mth.clamp(protection, 0, 20) / 25.0F;
        event.setAmount(event.getAmount() / factor);
    }

    /** Las Espinas tampoco funcionan mientras dura la broma; y las armas del Jester no hieren en otras manos. */
    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        if (source.getDirectEntity() instanceof Player p && !JesterLock.isJester(p)) {
            var key = ForgeRegistries.ITEMS.getKey(p.getMainHandItem().getItem());
            if (key != null && JesterTricks.MOD_ID.equals(key.getNamespace())) {
                event.setCanceled(true);
                return;
            }
        }
        // Paso de sombra: los golpes y proyectiles atraviesan al Jester
        if (event.getEntity().hasEffect(ModEffects.SHADOW_STEP.get()) && source.getEntity() != null) {
            event.setCanceled(true);
            return;
        }
        if (source.is(DamageTypes.THORNS) && source.getEntity() instanceof LivingEntity wearer
                && wearer.hasEffect(ModEffects.PRANKED.get())) {
            event.setCanceled(true);
        }
    }

    /** Quien sale disparado por el Cañón de circo (o cae del Globo) aterriza sin hacerse daño. */
    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        LivingEntity e = event.getEntity();
        long until = e.getPersistentData().getLong(SAFE_LANDING);
        if (until > 0 && e.level().getGameTime() <= until) {
            event.setCanceled(true);
            e.getPersistentData().remove(SAFE_LANDING);
        }
    }

    public static final String SAFE_LANDING = "jestertricks_safe_landing";

    public static void grantSafeLanding(LivingEntity e, int ticks) {
        e.getPersistentData().putLong(SAFE_LANDING, e.level().getGameTime() + ticks);
    }

    private JesterEvents() {}
}
