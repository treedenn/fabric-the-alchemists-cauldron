package treeden.thealchemistscauldron.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class AlchemistTableBlock extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    private static final VoxelShape BASE = Block.createCuboidShape(0.0, 0.0, 0.0, 2.0, 14.0, 2.0);
    private static final VoxelShape X_LEG_1 = Block.createCuboidShape(0, 0, 14, 2, 14, 16);
    private static final VoxelShape X_LEG_2 = Block.createCuboidShape(30, 0, 0, 32, 14, 2);
    private static final VoxelShape X_LEG_3 = Block.createCuboidShape(30, 0, 14, 32, 14, 16);
    private static final VoxelShape X_TOP = Block.createCuboidShape(0, 14, 0, 32, 16, 16);
    private static final VoxelShape Z_LEG_1 = Block.createCuboidShape(14, 0, 0, 16, 14, 2);
    private static final VoxelShape Z_LEG_2 = Block.createCuboidShape(0, 0, 30, 32, 14, 32);
    private static final VoxelShape Z_LEG_3 = Block.createCuboidShape(14, 0, 30, 16, 14, 32);
    private static final VoxelShape Z_TOP = Block.createCuboidShape(0, 14, 0, 16, 16, 32);
    private static final VoxelShape X_AXIS = VoxelShapes.union(BASE, X_LEG_1, X_LEG_2, X_LEG_3, X_TOP);
    private static final VoxelShape Z_AXIS = VoxelShapes.union(BASE, Z_LEG_1, Z_LEG_2, Z_LEG_3, Z_TOP);

    public AlchemistTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().rotateYClockwise());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        if (direction.getAxis() == Direction.Axis.X) {
            return X_AXIS;
        }
        return Z_AXIS;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
