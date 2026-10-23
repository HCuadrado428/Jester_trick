package com.jester.jestertricks.network;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.ability.JesterPassiveSettings;
import com.jester.jestertricks.ability.JesterPassiveSettings.Passive;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/** Servidor → cliente: "eres (o no) el Jester, y estas son tus pasivas". */
public class PassivesSyncPacket {
    public final boolean jester;
    public final boolean[] on;

    public PassivesSyncPacket(boolean jester, boolean[] on) {
        this.jester = jester;
        this.on = on;
    }

    public PassivesSyncPacket(FriendlyByteBuf buf) {
        this.jester = buf.readBoolean();
        int n = buf.readVarInt();
        this.on = new boolean[n];
        for (int i = 0; i < n; i++) on[i] = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(jester);
        buf.writeVarInt(on.length);
        for (boolean b : on) buf.writeBoolean(b);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> com.jester.jestertricks.client.ClientPassives.apply(this)));
        ctx.get().setPacketHandled(true);
    }

    public static void sendTo(ServerPlayer player) {
        Passive[] all = Passive.values();
        boolean[] on = new boolean[all.length];
        for (int i = 0; i < all.length; i++) on[i] = JesterPassiveSettings.isOn(player, all[i]);
        JesterNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new PassivesSyncPacket(JesterLock.isJester(player), on));
    }
}
