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

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.comment("Jester's Tricks").push("jester");
        JESTER_NAMES = b.comment(
                        "Nombres de jugador que pueden usar los objetos del Jester (no distingue mayúsculas).",
                        "Si la lista está vacía, cualquiera puede usarlos (útil para probar el mod).",
                        "Ejemplo: jesterNames = [\"NombreDelJester\"]")
                .defineListAllowEmpty("jesterNames", new ArrayList<>(), o -> o instanceof String);
        b.pop();
        SPEC = b.build();
    }

    private JesterConfig() {}
}
