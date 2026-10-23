package com.jester.jestertricks.portal;

import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/** Viajes del Jester entre dimensiones a través de sus pequeños portales. */
public final class OriginTravel {
    public static final List<ResourceKey<Level>> DESTINATIONS = List.of(Level.OVERWORLD, Level.NETHER, Level.END);

    public static String nameKey(ResourceKey<Level> dim) {
        if (dim == Level.NETHER) return "dimension.jestertricks.nether";
        if (dim == Level.END) return "dimension.jestertricks.end";
        return "dimension.jestertricks.overworld";
    }

    /** Lleva al jugador a la posición equivalente de otra dimensión, siempre a un sitio seguro. */
    public static boolean travel(ServerPlayer player, ResourceKey<Level> dim) {
        ServerLevel from = player.serverLevel();
        ServerLevel dest = player.server.getLevel(dim);
        if (dest == null || dest == from) return false;

        BlockPos target;
        if (dim == Level.END) {
            target = new BlockPos(100, 49, 0);
            buildPlatform(dest, target, 2);
        } else {
            double scale = from.dimensionType().coordinateScale() / dest.dimensionType().coordinateScale();
            int x = (int) Math.floor(player.getX() * scale);
            int z = (int) Math.floor(player.getZ() * scale);
            if (from.dimension() == Level.END) {
                BlockPos spawn = dest.getSharedSpawnPos();
                x = spawn.getX();
                z = spawn.getZ();
            }
            BlockPos clamped = dest.getWorldBorder().clampToBounds(x, 64, z);
            target = findSafe(dest, clamped.getX(), clamped.getZ());
        }

        Fx.rift(from, player.position().add(0, 1, 0), player.getYRot(), 1.0);
        from.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.RIFT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        player.teleportTo(dest, target.getX() + 0.5, target.getY(), target.getZ() + 0.5, player.getYRot(), player.getXRot());
        player.fallDistance = 0.0F;
        Fx.rift(dest, new Vec3(target.getX() + 0.5, target.getY() + 1, target.getZ() + 0.5), player.getYRot(), 1.0);
        dest.playSound(null, target.getX() + 0.5, target.getY(), target.getZ() + 0.5, ModSounds.RIFT.get(), SoundSource.PLAYERS, 1.0F, 1.2F);
        return true;
    }

    private static BlockPos findSafe(ServerLevel level, int x, int z) {
        int min = level.getMinBuildHeight() + 1;
        int max = level.dimension() == Level.NETHER ? 120 : level.getMaxBuildHeight() - 3;
        if (level.dimension() != Level.NETHER) {
            int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos p = new BlockPos(x, top, z);
            if (top > min && isSafe(level, p)) return p;
        }
        // busca un hueco seguro en la columna, empezando por el centro
        int mid = level.dimension() == Level.NETHER ? 64 : 70;
        for (int d = 0; d < max - min; d++) {
            for (int sign : new int[]{1, -1}) {
                int y = mid + d * sign;
                if (y < min || y > max) continue;
                BlockPos p = new BlockPos(x, y, z);
                if (isSafe(level, p)) return p;
            }
        }
        // si no hay hueco, el Jester se fabrica uno
        BlockPos p = new BlockPos(x, mid, z);
        buildPlatform(level, p, 1);
        return p;
    }

    private static boolean isSafe(ServerLevel level, BlockPos feet) {
        BlockState below = level.getBlockState(feet.below());
        if (!below.isFaceSturdy(level, feet.below(), Direction.UP)) return false;
        if (below.is(Blocks.MAGMA_BLOCK) || below.is(Blocks.CAMPFIRE) || below.is(Blocks.SOUL_CAMPFIRE)) return false;
        return level.getBlockState(feet).getCollisionShape(level, feet).isEmpty()
                && level.getBlockState(feet.above()).getCollisionShape(level, feet.above()).isEmpty()
                && level.getFluidState(feet).isEmpty()
                && level.getFluidState(feet.above()).isEmpty();
    }

    private static void buildPlatform(ServerLevel level, BlockPos feet, int r) {
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                level.setBlockAndUpdate(feet.offset(dx, -1, dz), Blocks.OBSIDIAN.defaultBlockState());
                for (int dy = 0; dy < 3; dy++) {
                    level.setBlockAndUpdate(feet.offset(dx, dy, dz), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private OriginTravel() {}
}
