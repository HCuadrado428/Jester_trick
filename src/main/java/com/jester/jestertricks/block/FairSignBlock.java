package com.jester.jestertricks.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Cartel de feria "¡PASEN Y VEAN!": un tablón con bombillas que se cuelga en la pared. */
public class FairSignBlock extends HorizontalDirectionalBlock {
    private static final VoxelShape NORTH = Block.box(0, 2, 14, 16, 14, 16);
    private static final VoxelShape SOUTH = Block.box(0, 2, 0, 16, 14, 2);
    private static final VoxelShape EAST = Block.box(0, 2, 0, 2, 14, 16);
    private static final VoxelShape WEST = Block.box(14, 2, 0, 16, 14, 16);

    public FairSignBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction face = ctx.getClickedFace();
        Direction facing = face.getAxis().isHorizontal() ? face : ctx.getHorizontalDirection().getOpposite();
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            default -> NORTH;
        };
    }
}
