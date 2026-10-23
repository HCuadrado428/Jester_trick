package com.jester.jestertricks.block;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.portal.HoopAnchors;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Aro de Feria. El fijo (PERMANENT) se queda para siempre y es el destino.
 * Cuando el Jester salta por un aro temporal, el temporal desaparece y él aparece en el fijo,
 * esté donde esté (aunque sea en otra dimensión). Nadie más puede usarlos.
 */
public class HoopBlock extends HorizontalDirectionalBlock {
    public static final BooleanProperty PERMANENT = BooleanProperty.create("permanent");
    private static final VoxelShape NS = Block.box(0, 0, 6.5, 16, 16, 9.5);
    private static final VoxelShape EW = Block.box(6.5, 0, 0, 9.5, 16, 16);
    /** Los aros temporales se desvanecen solos a los 2 minutos. */
    private static final int TEMP_LIFETIME = 20 * 120;

    public HoopBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PERMANENT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PERMANENT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return state.getValue(FACING).getAxis() == Direction.Axis.Z ? NS : EW;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moving) {
        super.onPlace(state, level, pos, old, moving);
        if (!level.isClientSide && !state.getValue(PERMANENT)) {
            level.scheduleTick(pos, this, TEMP_LIFETIME);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(PERMANENT)) {
            vanish(level, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!level.isClientSide && state.getValue(PERMANENT) && !newState.is(this) && level.getServer() != null) {
            HoopAnchors.get(level.getServer()).clearIf(GlobalPos.of(level.dimension(), pos));
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || state.getValue(PERMANENT) || !(entity instanceof ServerPlayer player)) return;
        if (!JesterLock.isJester(player)) return;
        long now = level.getGameTime();
        if (now - player.getPersistentData().getLong("jestertricks_hoop") < 20) return;
        player.getPersistentData().putLong("jestertricks_hoop", now);

        GlobalPos anchor = HoopAnchors.get(player.server).get(player.getUUID());
        ServerLevel destLevel = anchor == null ? null : player.server.getLevel(anchor.dimension());
        if (destLevel == null) {
            player.displayClientMessage(Component.translatable("message.jestertricks.no_anchor").withStyle(ChatFormatting.GRAY), true);
            return;
        }
        vanish((ServerLevel) level, pos);
        BlockPos a = anchor.pos();
        player.teleportTo(destLevel, a.getX() + 0.5, a.getY(), a.getZ() + 0.5, player.getYRot(), player.getXRot());
        player.fallDistance = 0.0F;
        player.getPersistentData().putLong("jestertricks_hoop", destLevel.getGameTime());
        Fx.rift(destLevel, new Vec3(a.getX() + 0.5, a.getY() + 1.0, a.getZ() + 0.5), player.getYRot(), 0.9);
        Fx.confetti(destLevel, new Vec3(a.getX() + 0.5, a.getY() + 1.0, a.getZ() + 0.5), 10, 0.6);
        destLevel.playSound(null, a.getX() + 0.5, a.getY(), a.getZ() + 0.5, ModSounds.RIFT.get(), SoundSource.PLAYERS, 1.0F, 1.3F);
    }

    private static void vanish(ServerLevel level, BlockPos pos) {
        level.removeBlock(pos, false);
        Vec3 c = Vec3.atCenterOf(pos).add(0, 0.5, 0);
        Fx.confetti(level, c, 10, 0.6);
        level.playSound(null, c.x, c.y, c.z, ModSounds.MAGIC_POOF.get(), SoundSource.BLOCKS, 0.8F, 1.4F);
    }
}
