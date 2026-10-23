package com.jester.jestertricks;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuración del servidor: world/serverconfig/jestertricks-server.toml
 * (en un servidor dedicado se copia también a defaultconfigs/ para mundos nuevos).
 */
public final class JesterConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> JESTER_NAMES;
    public static final ForgeConfigSpec.DoubleValue DAMAGE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue COOLDOWN_MULTIPLIER;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.comment("Jester's Tricks").push("jester");
        JESTER_NAMES = b.comment(
                        "Nombres de jugador que pueden usar los objetos y poderes del Jester (no distingue mayúsculas).",
                        "Si la lista está vacía, cualquiera puede usarlos (útil para probar el mod).",
                        "Ejemplo: jesterNames = [\"NombreDelJester\"]")
                .defineListAllowEmpty("jesterNames", new ArrayList<>(), o -> o instanceof String);
        b.pop().comment("Equilibrio").push("balance");
        DAMAGE_MULTIPLIER = b.comment("Multiplica el daño de todas las armas y trucos. 1.0 = normal, 0.5 = la mitad, 2.0 = el doble.")
                .defineInRange("damageMultiplier", 1.0, 0.0, 10.0);
        COOLDOWN_MULTIPLIER = b.comment("Multiplica los tiempos de recarga. 1.0 = normal, 0.5 = el doble de rápido, 2.0 = el doble de lento.")
                .defineInRange("cooldownMultiplier", 1.0, 0.0, 10.0);
        b.pop();
        SPEC = b.build();
    }

    public static float damage(float base) {
        try {
            return (float) (base * DAMAGE_MULTIPLIER.get());
        } catch (IllegalStateException notLoaded) {
            return base;
        }
    }

    public static int cooldown(int baseTicks) {
        try {
            return Math.max(0, (int) Math.round(baseTicks * COOLDOWN_MULTIPLIER.get()));
        } catch (IllegalStateException notLoaded) {
            return baseTicks;
        }
    }

    private JesterConfig() {}
}
