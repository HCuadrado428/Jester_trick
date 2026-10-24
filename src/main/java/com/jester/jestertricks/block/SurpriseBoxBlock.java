package com.jester.jestertricks.block;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Caja Sorpresa (jack-in-the-box): si alguien la pisa (sin ir agachado) o la abres,
 * salta el Jester del muelle y manda por los aires a todo lo que hay encima.
 * Se vuelve a cerrar sola a los 2 segundos.
 */
public class SurpriseBoxBlock extends Block {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public SurpriseBoxBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(OPEN, false).setValue(FACING, Direction.NORTH));
    }

    private static final VoxelShape SHAPE = Block.box(1.5, 0, 1.5, 14.5, 12.5, 14.5);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(OPEN, FACING);
    }

    /** Se coloca mirando hacia quien la pone. */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && !state.getValue(OPEN)) {
            pop(level, pos, state);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && !state.getValue(OPEN)) {
            pop(level, pos, state);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private void pop(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(OPEN, true), Block.UPDATE_ALL);
        level.scheduleTick(pos, this, 40);
        AABB above = new AABB(pos.above()).inflate(0.3, 0.6, 0.3);
        for (Entity e : level.getEntities((Entity) null, above, Entity::isAlive)) {
            e.setDeltaMovement(e.getDeltaMovement().x * 0.3, 1.45, e.getDeltaMovement().z * 0.3);
            e.hurtMarked = true;
        }
        double x = pos.getX() + 0.5, y = pos.getY() + 1.1, z = pos.getZ() + 0.5;
        level.playSound(null, x, y, z, ModSounds.JACK_BOX.get(), SoundSource.BLOCKS, 1.2F, 1.0F);
        level.playSound(null, x, y, z, ModSounds.JESTER_LAUGH.get(), SoundSource.BLOCKS, 0.9F, 1.0F + level.random.nextFloat() * 0.2F);
        if (level instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.POOF, x, y, z, 16, 0.3, 0.3, 0.3, 0.05);
            Fx.confetti(sl, new net.minecraft.world.phys.Vec3(x, y + 0.5, z), 12, 0.6);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(OPEN)) {
            level.setBlock(pos, state.setValue(OPEN, false), Block.UPDATE_ALL);
        }
    }
}
