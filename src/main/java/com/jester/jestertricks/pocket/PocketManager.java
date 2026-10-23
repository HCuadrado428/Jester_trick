package com.jester.jestertricks.pocket;

import com.jester.jestertricks.entity.PocketStatueEntity;
import com.jester.jestertricks.item.BlackBoxItem;
import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.network.PocketJestersPacket;
import com.jester.jestertricks.registry.ModParticles;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Mete y saca jugadores del bolsillo, coloca las estatuas y pone el ambiente. */
public final class PocketManager {
    /** Lo que dura dentro un jugador tragado: 1 minuto. */
    public static final int TRAP_TICKS = 20 * 60;

    // ------------------------------------------------------------------ entrar
    /** El Jester mete a otro jugador en su bolsillo durante un minuto. */
    public static boolean sendIn(ServerPlayer target, ServerPlayer jester, ItemStack box) {
        MinecraftServer server = jester.server;
        ServerLevel pocket = server.getLevel(PocketDimension.POCKET);
        if (pocket == null || target == jester || PocketDimension.isPocket(target.level())) return false;
        PocketData data = PocketData.get(server);
        data.setContents(jester.getUUID(), BlackBoxItem.snapshot(box));

        Fx.rift(target.serverLevel(), target.position().add(0, 1, 0), jester.getYRot(), 1.0);
        target.serverLevel().playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.RIFT.get(), SoundSource.PLAYERS, 1.2F, 0.6F);

        long expires = server.overworld().getGameTime() + TRAP_TICKS;
        enter(target, jester.getUUID(), pocket, data, expires, 0.0);

        target.displayClientMessage(Component.translatable("message.jestertricks.pocket_swallowed")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), false);
        jester.displayClientMessage(Component.translatable("message.jestertricks.pocket_sent", target.getDisplayName(), TRAP_TICKS / 20)
                .withStyle(ChatFormatting.WHITE), true);
        return true;
    }

    /** El Jester entra en su propio bolsillo (sin límite de tiempo). */
    public static boolean jesterEnter(ServerPlayer jester, ItemStack box) {
        MinecraftServer server = jester.server;
        ServerLevel pocket = server.getLevel(PocketDimension.POCKET);
        if (pocket == null || PocketDimension.isPocket(jester.level())) return false;
        PocketData data = PocketData.get(server);
        data.setContents(jester.getUUID(), BlackBoxItem.snapshot(box));
        Fx.rift(jester.serverLevel(), jester.position().add(0, 1, 0), jester.getYRot(), 1.0);
        enter(jester, jester.getUUID(), pocket, data, -1L, 7.0);
        pocket.playSound(null, jester.getX(), jester.getY(), jester.getZ(), ModSounds.JESTER_LAUGH.get(), SoundSource.PLAYERS, 2.0F, 0.7F);
        return true;
    }

    private static void enter(ServerPlayer p, UUID jester, ServerLevel pocket, PocketData data, long expires, double back) {
        data.putSession(p.getUUID(), new PocketData.Session(jester, p.level().dimension(),
                p.getX(), p.getY(), p.getZ(), p.getYRot(), p.getXRot(), expires));
        Vec3 c = PocketDimension.center(jester);
        placeStatues(pocket, jester, data);
        double ox = (p.getRandom().nextDouble() - 0.5) * 2.0;
        // mirando al norte, hacia la criatura gigante; el Jester aparece un poco detrás
        p.teleportTo(pocket, c.x + ox, c.y, c.z + back, 180.0F, 0.0F);
        p.fallDistance = 0.0F;
        Fx.rift(pocket, p.position().add(0, 1, 0), 180.0F, 1.0);
        pocket.playSound(null, p.getX(), p.getY(), p.getZ(), ModSounds.RIFT.get(), SoundSource.PLAYERS, 1.0F, 0.8F);
        syncJesters(pocket.getServer());
    }

    // ------------------------------------------------------------------ salir
    /** Devuelve al jugador a donde estaba antes de entrar. */
    public static void leave(ServerPlayer p) {
        MinecraftServer server = p.server;
        PocketData data = PocketData.get(server);
        PocketData.Session s = data.session(p.getUUID());
        data.removeSession(p.getUUID());
        ServerLevel dest = s == null ? null : server.getLevel(s.returnDim);
        if (PocketDimension.isPocket(p.level())) {
            Fx.rift(p.serverLevel(), p.position().add(0, 1, 0), p.getYRot(), 1.0);
        }
        if (dest == null) {
            sendToSpawn(p);
        } else {
            p.teleportTo(dest, s.x, s.y, s.z, s.yaw, s.pitch);
        }
        p.fallDistance = 0.0F;
        Fx.rift(p.serverLevel(), p.position().add(0, 1, 0), p.getYRot(), 1.0);
        Fx.confetti(p.serverLevel(), p.position().add(0, 1, 0), 10, 0.6);
        p.serverLevel().playSound(null, p.getX(), p.getY(), p.getZ(), ModSounds.RIFT.get(), SoundSource.PLAYERS, 1.0F, 1.3F);
        syncJesters(server);
    }

    public static void sendToSpawn(ServerPlayer p) {
        ServerLevel over = p.server.overworld();
        BlockPos spawn = over.getSharedSpawnPos();
        p.teleportTo(over, spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, p.getYRot(), p.getXRot());
    }

    public static boolean isJesterInside(MinecraftServer server, UUID player) {
        PocketData.Session s = PocketData.get(server).session(player);
        return s != null && s.expires < 0;
    }

    // ------------------------------------------------------------------ estatuas
    private static void placeStatues(ServerLevel pocket, UUID jester, PocketData data) {
        Vec3 c = PocketDimension.center(jester);
        for (Entity old : pocket.getEntitiesOfClass(PocketStatueEntity.class, new AABB(c, c).inflate(96, 64, 96))) {
            old.discard();
        }
        CompoundTag box = data.contents(jester);
        if (box == null) return;

        NonNullList<ItemStack> all = NonNullList.withSize(27, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(box, all);
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack s : all) if (!s.isEmpty()) items.add(s);

        double radius = Math.min(30.0, 9.0 + items.size() * 0.8);
        for (int i = 0; i < items.size(); i++) {
            double a = Math.PI * 2 * i / items.size();
            double x = c.x + Math.sin(a) * radius;
            double z = c.z + Math.cos(a) * radius;
            PocketStatueEntity st = PocketStatueEntity.ofItem(pocket, items.get(i), 12.0F);
            spawnFacing(pocket, st, x, z, c);
        }

        if (box.contains("Mob")) {
            CompoundTag mob = box.getCompound("Mob");
            Entity probe = EntityType.create(mob, pocket).orElse(null);
            if (probe != null) {
                float h = Math.max(0.3F, probe.getBbHeight());
                float scale = Mth.clamp(9.0F / h, 2.5F, 14.0F);
                probe.discard();
                PocketStatueEntity st = PocketStatueEntity.ofMob(pocket, mob, scale);
                spawnFacing(pocket, st, c.x, c.z - radius - 10.0, c);
            }
        }
    }

    private static void spawnFacing(ServerLevel pocket, PocketStatueEntity st, double x, double z, Vec3 c) {
        pocket.getChunk(Mth.floor(x) >> 4, Mth.floor(z) >> 4);   // carga el trozo de suelo
        float yaw = (float) (Mth.atan2(c.x - x, c.z - z) * (180.0 / Math.PI));
        st.moveTo(x, PocketDimension.FLOOR_Y, z, -yaw, 0.0F);
        pocket.addFreshEntity(st);
    }

    // ------------------------------------------------------------------ cada segundo
    public static void tick(MinecraftServer server) {
        ServerLevel pocket = server.getLevel(PocketDimension.POCKET);
        if (pocket == null) return;
        long now = server.overworld().getGameTime();
        PocketData data = PocketData.get(server);
        for (ServerPlayer p : new ArrayList<>(pocket.players())) {
            PocketData.Session s = data.session(p.getUUID());
            if (s == null) {           // nadie le metió aquí (p. ej. /tp): fuera
                sendToSpawn(p);
                continue;
            }
            if (s.expires >= 0) {
                long left = s.expires - now;
                if (left <= 0) {
                    p.displayClientMessage(Component.translatable("message.jestertricks.pocket_released")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), true);
                    leave(p);
                    continue;
                }
                p.displayClientMessage(Component.translatable("message.jestertricks.pocket_timer", (left + 19) / 20)
                        .withStyle(ChatFormatting.DARK_GRAY), true);
            }
            ambience(pocket, p);
        }
        if (now % 40 == 0) syncJesters(server);
    }

    private static void ambience(ServerLevel pocket, ServerPlayer p) {
        var rnd = p.getRandom();
        // estrellas del origen flotando alrededor
        for (int i = 0; i < 3; i++) {
            double x = p.getX() + (rnd.nextDouble() - 0.5) * 16;
            double y = p.getY() + 1 + rnd.nextDouble() * 5;
            double z = p.getZ() + (rnd.nextDouble() - 0.5) * 16;
            pocket.sendParticles(p, ModParticles.ORIGIN_STAR.get(), false, x, y, z, 1, 0, 0, 0, 0.01);
        }
        // risas lejanas y susurros, desde direcciones al azar
        if (rnd.nextInt(8) == 0) {
            double a = rnd.nextDouble() * Math.PI * 2;
            pocket.playSound(null, p.getX() + Math.cos(a) * 12, p.getY() + 2, p.getZ() + Math.sin(a) * 12,
                    ModSounds.JESTER_LAUGH.get(), SoundSource.AMBIENT, 0.9F, 0.55F + rnd.nextFloat() * 0.25F);
        } else if (rnd.nextInt(6) == 0) {
            double a = rnd.nextDouble() * Math.PI * 2;
            pocket.playSound(null, p.getX() + Math.cos(a) * 4, p.getY() + 1.6, p.getZ() + Math.sin(a) * 4,
                    ModSounds.POCKET_WHISPER.get(), SoundSource.AMBIENT, 0.8F, 0.85F + rnd.nextFloat() * 0.3F);
        }
    }

    /** Cuenta a quienes están en el bolsillo qué jugadores son el Jester (para verlo gigante). */
    public static void syncJesters(MinecraftServer server) {
        ServerLevel pocket = server.getLevel(PocketDimension.POCKET);
        if (pocket == null) return;
        PocketData data = PocketData.get(server);
        List<UUID> jesters = new ArrayList<>();
        data.sessions().forEach((uuid, s) -> {
            if (s.expires < 0) jesters.add(uuid);
        });
        for (ServerPlayer p : pocket.players()) PocketJestersPacket.sendTo(p, jesters);
    }

    public static boolean inPocket(Level level) {
        return PocketDimension.isPocket(level);
    }

    private PocketManager() {}
}
