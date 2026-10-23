package com.jester.jestertricks.block;

import com.jester.jestertricks.JesterLock;
import com.jester.jestertricks.event.JesterEvents;
import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Cañón de Circo: clic derecho y el Jester sale disparado hacia donde mira,
 * con estruendo y confeti. Aterriza sin daño de caída.
 */
public class CircusCannonBlock extends HorizontalDirectionalBlock {
    private static final VoxelShape SHAPE = Block.box(1, 0, 1, 15, 14, 15);

    public CircusCannonBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!JesterLock.check(player)) return InteractionResult.FAIL;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        long now = level.getGameTime();
        if (now - player.getPersistentData().getLong("jestertricks_cannon") < 20) return InteractionResult.CONSUME;
        player.getPersistentData().putLong("jestertricks_cannon", now);

        Vec3 top = Vec3.atBottomCenterOf(pos).add(0, 1.0, 0);
        player.teleportTo(top.x, top.y, top.z);
        Vec3 look = player.getLookAngle();
        player.setDeltaMovement(look.x * 3.2, Math.max(0.6, look.y * 3.2 + 0.4), look.z * 3.2);
        player.hurtMarked = true;
        player.fallDistance = 0.0F;
        JesterEvents.grantSafeLanding(player, 20 * 20);

        if (level instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.EXPLOSION, top.x, top.y, top.z, 2, 0.3, 0.3, 0.3, 0.0);
            sl.sendParticles(ParticleTypes.LARGE_SMOKE, top.x, top.y, top.z, 30, 0.4, 0.4, 0.4, 0.05);
            Fx.confetti(sl, top.add(0, 0.5, 0), 20, 0.8);
        }
        level.playSound(null, top.x, top.y, top.z, ModSounds.BANG.get(), SoundSource.BLOCKS, 1.4F, 0.8F);
        level.playSound(null, top.x, top.y, top.z, ModSounds.JESTER_LAUGH.get(), SoundSource.PLAYERS, 0.8F, 1.2F);
        return InteractionResult.CONSUME;
    }
}
