package com.jester.jestertricks.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Guarda en el mundo dónde está el Aro de Feria fijo de cada Jester (sobrevive a reinicios). */
public class HoopAnchors extends SavedData {
    private static final String NAME = "jestertricks_hoops";
    private final Map<UUID, GlobalPos> anchors = new HashMap<>();

    public static HoopAnchors get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(HoopAnchors::load, HoopAnchors::new, NAME);
    }

    @Nullable
    public GlobalPos get(UUID player) {
        return anchors.get(player);
    }

    public void set(UUID player, GlobalPos pos) {
        anchors.put(player, pos);
        setDirty();
    }

    public void clearIf(GlobalPos pos) {
        if (anchors.values().removeIf(p -> p.equals(pos))) setDirty();
    }

    public static HoopAnchors load(CompoundTag tag) {
        HoopAnchors data = new HoopAnchors();
        ListTag list = tag.getList("Anchors", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag e = list.getCompound(i);
            ResourceKey<net.minecraft.world.level.Level> dim =
                    ResourceKey.create(Registries.DIMENSION, new ResourceLocation(e.getString("Dim")));
            data.anchors.put(e.getUUID("Player"), GlobalPos.of(dim, BlockPos.of(e.getLong("Pos"))));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        anchors.forEach((uuid, pos) -> {
            CompoundTag e = new CompoundTag();
            e.putUUID("Player", uuid);
            e.putString("Dim", pos.dimension().location().toString());
            e.putLong("Pos", pos.pos().asLong());
            list.add(e);
        });
        tag.put("Anchors", list);
        return tag;
    }
}
