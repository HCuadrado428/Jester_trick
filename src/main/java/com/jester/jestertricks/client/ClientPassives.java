package com.jester.jestertricks.client;

import com.jester.jestertricks.ability.JesterPassiveSettings.Passive;
import com.jester.jestertricks.network.PassivesSyncPacket;
import net.minecraft.client.Minecraft;

/** Copia en el cliente de lo que dice el servidor sobre el Jester y sus pasivas. */
public final class ClientPassives {
    private static boolean jester;
    private static final boolean[] ON = new boolean[Passive.values().length];

    static {
        java.util.Arrays.fill(ON, true);
    }

    public static void apply(PassivesSyncPacket packet) {
        jester = packet.jester;
        for (int i = 0; i < Math.min(ON.length, packet.on.length); i++) ON[i] = packet.on[i];
        if (Minecraft.getInstance().screen instanceof PassivesScreen screen) screen.refresh();
    }

    public static boolean isJester() {
        return jester;
    }

    public static boolean isOn(Passive passive) {
        return ON[passive.ordinal()];
    }

    private ClientPassives() {}
}
