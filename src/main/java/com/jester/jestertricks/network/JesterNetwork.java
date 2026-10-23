package com.jester.jestertricks.network;

import com.jester.jestertricks.JesterTricks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/** Canal de red del mod: el cliente avisa al servidor cuando el Jester pulsa una tecla de poder. */
public final class JesterNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            JesterTricks.id("main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, AbilityPacket.class, AbilityPacket::encode, AbilityPacket::new, AbilityPacket::handle);
    }

    private JesterNetwork() {}
}
