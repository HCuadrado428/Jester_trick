package com.jester.jestertricks.network;

import com.jester.jestertricks.registry.ModEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/** Servidor → clientes: "este jugador está en su forma real durante N ticks" (para dibujarlo). */
public class TrueFormPacket {
    public final int entityId;
    public final int ticks;

    public TrueFormPacket(int entityId, int ticks) {
        this.entityId = entityId;
        this.ticks = ticks;
    }

    public TrueFormPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();
        this.ticks = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeVarInt(ticks);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> com.jester.jestertricks.client.ClientTrueForm.set(entityId, ticks)));
        ctx.get().setPacketHandled(true);
    }

    /** Avisa a todos los que ven al Jester (y a él mismo). */
    public static void broadcast(ServerPlayer jester) {
        MobEffectInstance fx = jester.getEffect(ModEffects.TRUE_FORM.get());
        int ticks = fx == null ? 0 : fx.getDuration();
        JesterNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> jester),
                new TrueFormPacket(jester.getId(), ticks));
    }

    /** Para quien empieza a ver al Jester a mitad de la transformación. */
    public static void sendTo(ServerPlayer viewer, ServerPlayer jester) {
        MobEffectInstance fx = jester.getEffect(ModEffects.TRUE_FORM.get());
        if (fx != null) {
            JesterNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer),
                    new TrueFormPacket(jester.getId(), fx.getDuration()));
        }
    }
}
