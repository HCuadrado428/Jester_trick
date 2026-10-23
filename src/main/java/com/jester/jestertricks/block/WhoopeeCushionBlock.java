package com.jester.jestertricks.block;

import com.jester.jestertricks.item.Fx;
import com.jester.jestertricks.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Cojín de Pedorretas: trampa en el suelo. Quien la pisa suelta una pedorreta
 * enorme y queda mareado (Náusea) y lento. Todo lo que hay cerca, también.
 */
public class WhoopeeCushionBlock extends Block {
    public static final BooleanProperty PRESSED = BlockStateProperties.POWERED;
    private static final VoxelShape FULL = Block.box(2, 0, 2, 14, 4, 14);
    private static final VoxelShape FLAT = Block.box(2, 0, 2, 14, 1.5, 14);

    public WhoopeeCushionBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(PRESSED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PRESSED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return state.getValue(PRESSED) ? FLAT : FULL;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return !level.isEmptyBlock(pos.below());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, dir, neighbor, level, pos, neighborPos);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || state.getValue(PRESSED) || !(entity instanceof LivingEntity)) return;
        level.setBlock(pos, state.setValue(PRESSED, true), Block.UPDATE_ALL);
        level.scheduleTick(pos, this, 40);

        double x = pos.getX() + 0.5, y = pos.getY() + 0.2, z = pos.getZ() + 0.5;
        level.playSound(null, x, y, z, ModSounds.FART.get(), SoundSource.BLOCKS, 1.4F, 0.8F + level.random.nextFloat() * 0.4F);
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(3.0), LivingEntity::isAlive)) {
            e.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160, 0));
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 1));
        }
        if (level instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 6, 0.3, 0.1, 0.3, 0.01);
            sl.sendParticles(Fx.GREY, x, y + 0.3, z, 20, 0.6, 0.3, 0.6, 0.0);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(PRESSED)) {
            level.setBlock(pos, state.setValue(PRESSED, false), Block.UPDATE_ALL);
        }
    }
}
