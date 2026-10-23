package com.jester.jestertricks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/** Servidor → cliente: qué jugadores del bolsillo son el Jester (se dibujan gigantes). */
public class PocketJestersPacket {
    public final List<UUID> jesters;

    public PocketJestersPacket(List<UUID> jesters) {
        this.jesters = jesters;
    }

    public PocketJestersPacket(FriendlyByteBuf buf) {
        int n = buf.readVarInt();
        this.jesters = new ArrayList<>(n);
        for (int i = 0; i < n; i++) jesters.add(buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(jesters.size());
        for (UUID u : jesters) buf.writeUUID(u);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> com.jester.jestertricks.client.ClientPocket.setJesters(jesters)));
        ctx.get().setPacketHandled(true);
    }

    public static void sendTo(ServerPlayer player, List<UUID> jesters) {
        JesterNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PocketJestersPacket(jesters));
    }
}
