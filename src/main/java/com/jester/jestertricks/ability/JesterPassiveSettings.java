package com.jester.jestertricks.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * Qué pasivas tiene encendidas el Jester. Se guarda en los datos del jugador
 * (en la parte que Forge conserva al morir), así que se recuerda siempre.
 */
public final class JesterPassiveSettings {
    public enum Passive {
        /** Sin daño de caída. */
        NO_FALL,
        /** Visión nocturna permanente. */
        NIGHT_VISION,
        /** Risa y confeti al acabar con algo. */
        KILL_LAUGH;

        public String key() {
            return name().toLowerCase();
        }
    }

    private static final String ROOT = "jestertricks_passives";

    private static CompoundTag data(Player player) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        if (!player.getPersistentData().contains(Player.PERSISTED_NBT_TAG)) {
            player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
        }
        if (!persisted.contains(ROOT)) persisted.put(ROOT, new CompoundTag());
        return persisted.getCompound(ROOT);
    }

    /** Todas las pasivas están encendidas mientras el Jester no las apague. */
    public static boolean isOn(Player player, Passive passive) {
        CompoundTag d = data(player);
        return !d.contains(passive.key()) || d.getBoolean(passive.key());
    }

    public static void set(Player player, Passive passive, boolean on) {
        data(player).putBoolean(passive.key(), on);
    }

    private JesterPassiveSettings() {}
}
