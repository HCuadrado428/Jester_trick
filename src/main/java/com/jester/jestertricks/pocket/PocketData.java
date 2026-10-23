package com.jester.jestertricks.pocket;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Quién está dentro del bolsillo, de dónde vino y cuándo sale; y qué guardaba
 * la Caja Negra de cada Jester (para las estatuas). Se guarda con el mundo.
 */
public class PocketData extends SavedData {
    private static final String NAME = "jestertricks_pocket";

    public static final class Session {
        public final UUID jester;
        public final ResourceKey<Level> returnDim;
        public final double x, y, z;
        public final float yaw, pitch;
        /** Momento (game time) en que sale solo; -1 = no sale solo (el propio Jester). */
        public final long expires;

        public Session(UUID jester, ResourceKey<Level> returnDim, double x, double y, double z, float yaw, float pitch, long expires) {
            this.jester = jester;
            this.returnDim = returnDim;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.expires = expires;
        }

        CompoundTag save() {
            CompoundTag t = new CompoundTag();
            t.putUUID("Jester", jester);
            t.putString("Dim", returnDim.location().toString());
            t.putDouble("X", x);
            t.putDouble("Y", y);
            t.putDouble("Z", z);
            t.putFloat("Yaw", yaw);
            t.putFloat("Pitch", pitch);
            t.putLong("Expires", expires);
            return t;
        }

        static Session load(CompoundTag t) {
            return new Session(t.getUUID("Jester"),
                    ResourceKey.create(Registries.DIMENSION, new ResourceLocation(t.getString("Dim"))),
                    t.getDouble("X"), t.getDouble("Y"), t.getDouble("Z"),
                    t.getFloat("Yaw"), t.getFloat("Pitch"), t.getLong("Expires"));
        }
    }

    private final Map<UUID, Session> sessions = new HashMap<>();
    private final Map<UUID, CompoundTag> contents = new HashMap<>();

    public static PocketData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(PocketData::load, PocketData::new, NAME);
    }

    public Map<UUID, Session> sessions() {
        return sessions;
    }

    @Nullable
    public Session session(UUID player) {
        return sessions.get(player);
    }

    public void putSession(UUID player, Session s) {
        sessions.put(player, s);
        setDirty();
    }

    public void removeSession(UUID player) {
        if (sessions.remove(player) != null) setDirty();
    }

    @Nullable
    public CompoundTag contents(UUID jester) {
        return contents.get(jester);
    }

    public void setContents(UUID jester, CompoundTag tag) {
        contents.put(jester, tag);
        setDirty();
    }

    public static PocketData load(CompoundTag tag) {
        PocketData d = new PocketData();
        ListTag ss = tag.getList("Sessions", Tag.TAG_COMPOUND);
        for (int i = 0; i < ss.size(); i++) {
            CompoundTag e = ss.getCompound(i);
            d.sessions.put(e.getUUID("Player"), Session.load(e.getCompound("Session")));
        }
        ListTag cs = tag.getList("Contents", Tag.TAG_COMPOUND);
        for (int i = 0; i < cs.size(); i++) {
            CompoundTag e = cs.getCompound(i);
            d.contents.put(e.getUUID("Jester"), e.getCompound("Box"));
        }
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag ss = new ListTag();
        sessions.forEach((uuid, s) -> {
            CompoundTag e = new CompoundTag();
            e.putUUID("Player", uuid);
            e.put("Session", s.save());
            ss.add(e);
        });
        tag.put("Sessions", ss);
        ListTag cs = new ListTag();
        contents.forEach((uuid, box) -> {
            CompoundTag e = new CompoundTag();
            e.putUUID("Jester", uuid);
            e.put("Box", box);
            cs.add(e);
        });
        tag.put("Contents", cs);
        return tag;
    }
}
