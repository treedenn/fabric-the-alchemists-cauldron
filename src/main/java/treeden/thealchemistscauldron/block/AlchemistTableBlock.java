package treeden.thealchemistscauldron.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import treeden.thealchemistscauldron.TheAlchemistsCauldronMod;
import treeden.thealchemistscauldron.entity.AlchemistTableBlockEntity;

import java.util.Optional;
import java.util.stream.Stream;

public class AlchemistTableBlock extends BlockWithEntity {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    private static final VoxelShape OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(Block.createCuboidShape(0, 14, 0, 16, 16, 16), Stream.of(
            Block.createCuboidShape(0, 0, 0, 2, 14, 2),
            Block.createCuboidShape(0, 0, 14, 2, 14, 16),
            Block.createCuboidShape(14, 0, 14, 16, 14, 16),
            Block.createCuboidShape(14, 0, 0, 16, 14, 2)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get(), BooleanBiFunction.OR);

    public AlchemistTableBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            Optional<AlchemistTableBlockEntity> entityOptional = world.getBlockEntity(pos, TheAlchemistsCauldronMod.ALCHEMIST_TABLE_BLOCK_ENTITY_TYPE);
            if (entityOptional.isEmpty())
                return ActionResult.FAIL;

            AlchemistTableBlockEntity entity = entityOptional.get();
            ItemStack stackInHand = player.getStackInHand(hand);
            if (player.isSneaking()) {
                entity.extract(player.getInventory());
            } else {
                if (stackInHand.getItem().equals(TheAlchemistsCauldronMod.LIQUID_DROPPER_ITEM)) {
                    return entity.canInsert(stackInHand)
                            ? ActionResult.SUCCESS
                            : ActionResult.PASS;
                }

                Optional.ofNullable(state.createScreenHandlerFactory(world, pos))
                        .ifPresent(player::openHandledScreen);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemistTableBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().rotateYClockwise());
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }
}
