package com.jester.jestertricks.network;

import com.jester.jestertricks.ability.JesterAbilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** "El Jester ha pulsado la tecla del poder N". */
public class AbilityPacket {
    private final int ability;

    public AbilityPacket(JesterAbilities.Ability ability) {
        this.ability = ability.ordinal();
    }

    public AbilityPacket(FriendlyByteBuf buf) {
        this.ability = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(ability);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            JesterAbilities.Ability[] all = JesterAbilities.Ability.values();
            if (player != null && ability >= 0 && ability < all.length) {
                JesterAbilities.use(player, all[ability]);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
