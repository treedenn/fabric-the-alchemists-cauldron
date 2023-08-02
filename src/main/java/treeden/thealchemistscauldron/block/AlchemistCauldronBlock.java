package treeden.thealchemistscauldron.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import treeden.thealchemistscauldron.TheAlchemistsCauldronMod;
import treeden.thealchemistscauldron.entity.AlchemistCauldronBlockEntity;

import java.util.Optional;

import static treeden.thealchemistscauldron.TheAlchemistsCauldronMod.ALCHEMIST_CAULDRON_EMPTY_BLOCK;

public class AlchemistCauldronBlock extends LeveledCauldronBlock implements BlockEntityProvider {
    public static void decreaseFluid(World world, BlockPos pos, BlockState state) {
        int newLevel = state.get(LEVEL) - 1;
        world.setBlockState(pos, newLevel == 0 ? ALCHEMIST_CAULDRON_EMPTY_BLOCK.getDefaultState() : state.with(LEVEL, newLevel));
    }

    public AlchemistCauldronBlock(Settings settings) {
        super(settings, RAIN_PREDICATE, AlchemistCauldronBehavior.WATER_CAULDRON_BEHAVIOR);
        this.setDefaultState(getDefaultState().with(LEVEL, 1));
    }

    public boolean isEntityInCauldron(BlockState state, BlockPos pos, Entity entity) {
        return entity.getY() < pos.getY() + .95d && entity.getBoundingBox().maxY > pos.getY() + 0.25d;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && this.isEntityInCauldron(state, pos, entity)) {
            if ((entity instanceof ItemEntity)) {
                ItemStack itemStack = ((ItemEntity) entity).getStack();
                world.getBlockEntity(pos, TheAlchemistsCauldronMod.ALCHEMIST_CAULDRON_BLOCK_ENTITY_TYPE).ifPresent(blockEntity -> {
                    if (blockEntity.tryAddIngredients(world, itemStack)) {
                        entity.kill();
                    } else {
                        // shoot the entity away
                        float deltaX = (0.04f + world.getRandom().nextFloat() * 0.02f) * (world.getRandom().nextFloat() > 0.5f ? 1 : -1);
                        float deltaY = 0.25f + world.getRandom().nextFloat() * 0.1f;
                        float deltaZ = (0.04f + world.getRandom().nextFloat() * 0.02f) * (world.getRandom().nextFloat() > 0.5f ? 1 : -1);
                        entity.setVelocity(deltaX, deltaY, deltaZ);
                    }
                });
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Optional<AlchemistCauldronBlockEntity> entityOptional = world.getBlockEntity(pos, TheAlchemistsCauldronMod.ALCHEMIST_CAULDRON_BLOCK_ENTITY_TYPE);
        if (entityOptional.isPresent()) {
            if (player.getStackInHand(hand).getItem() == Items.GLASS_BOTTLE) {
                ItemStack potion = entityOptional.get().createPotion();

                if (player.getInventory().main.stream().noneMatch(ItemStack::isEmpty)) {
                    // inventory is full
                    player.dropStack(potion);
                } else {
                    player.getInventory().insertStack(potion);
                }

                decreaseFluid(world, pos, state);

                return ActionResult.SUCCESS;
            } else if (entityOptional.get().tryAddIngredients(world, player.getStackInHand(hand))) {
                player.setStackInHand(hand, ItemStack.EMPTY);
                return ActionResult.SUCCESS;
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemistCauldronBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != TheAlchemistsCauldronMod.ALCHEMIST_CAULDRON_BLOCK_ENTITY_TYPE) return null;

        return (world1, pos, state1, blockEntity) -> {
            ((AlchemistCauldronBlockEntity) blockEntity).tick(world1, pos, state1);
        };
    }
}
