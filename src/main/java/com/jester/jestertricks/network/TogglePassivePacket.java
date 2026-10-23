package com.jester.jestertricks.network;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.ability.JesterPassiveSettings;
import com.jester.jestertricks.ability.JesterPassiveSettings.Passive;
import com.jester.jestertricks.event.JesterPassives;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Cliente → servidor: "enciende/apaga esta pasiva" (passive = -1 solo pide el estado actual). */
public class TogglePassivePacket {
    private final int passive;
    private final boolean on;

    public TogglePassivePacket(int passive, boolean on) {
        this.passive = passive;
        this.on = on;
    }

    public static TogglePassivePacket request() {
        return new TogglePassivePacket(-1, false);
    }

    public TogglePassivePacket(FriendlyByteBuf buf) {
        this.passive = buf.readVarInt();
        this.on = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(passive);
        buf.writeBoolean(on);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Passive[] all = Passive.values();
            if (passive >= 0 && passive < all.length && JesterLock.isJester(player)) {
                JesterPassiveSettings.set(player, all[passive], on);
                JesterPassives.refresh(player);
            }
            PassivesSyncPacket.sendTo(player);
        });
        ctx.get().setPacketHandled(true);
    }
}
