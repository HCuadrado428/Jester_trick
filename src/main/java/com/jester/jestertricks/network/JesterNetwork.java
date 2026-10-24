package com.jester.jestertricks.network;

import com.jester.jestertricks.JesterTricks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/** Canal de red del mod (pasivas, forma real, bolsillo). */
public final class JesterNetwork {
    private static final String PROTOCOL = "5";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            JesterTricks.id("main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, TogglePassivePacket.class, TogglePassivePacket::encode, TogglePassivePacket::new, TogglePassivePacket::handle);
        CHANNEL.registerMessage(id++, PassivesSyncPacket.class, PassivesSyncPacket::encode, PassivesSyncPacket::new, PassivesSyncPacket::handle);
        CHANNEL.registerMessage(id++, TrueFormPacket.class, TrueFormPacket::encode, TrueFormPacket::new, TrueFormPacket::handle);
        CHANNEL.registerMessage(id++, PocketJestersPacket.class, PocketJestersPacket::encode, PocketJestersPacket::new, PocketJestersPacket::handle);
    }

    private JesterNetwork() {}
}
