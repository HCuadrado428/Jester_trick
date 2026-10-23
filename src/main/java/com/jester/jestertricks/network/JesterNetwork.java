package com.jester.jestertricks.network;

import com.jester.jestertricks.JesterTricks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/** Canal de red del mod: el cliente avisa al servidor cuando el Jester pulsa una tecla de poder. */
public final class JesterNetwork {
    private static final String PROTOCOL = "2";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            JesterTricks.id("main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, AbilityPacket.class, AbilityPacket::encode, AbilityPacket::new, AbilityPacket::handle);
        CHANNEL.registerMessage(id++, TogglePassivePacket.class, TogglePassivePacket::encode, TogglePassivePacket::new, TogglePassivePacket::handle);
        CHANNEL.registerMessage(id++, PassivesSyncPacket.class, PassivesSyncPacket::encode, PassivesSyncPacket::new, PassivesSyncPacket::handle);
    }

    private JesterNetwork() {}
}
