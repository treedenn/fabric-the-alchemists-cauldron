package treeden.thealchemistscauldron.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Map;
import java.util.function.Predicate;

import static treeden.thealchemistscauldron.TheAlchemistsCauldronMod.ALCHEMIST_CAULDRON_BLOCK;
import static treeden.thealchemistscauldron.TheAlchemistsCauldronMod.ALCHEMIST_CAULDRON_EMPTY_BLOCK;

public interface AlchemistCauldronBehavior {
    Map<Item, CauldronBehavior> EMPTY_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();
    Map<Item, CauldronBehavior> WATER_CAULDRON_BEHAVIOR = CauldronBehavior.createMap();

    static void registerBehaviors() {
        EMPTY_CAULDRON_BEHAVIOR.putAll(CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR);
        EMPTY_CAULDRON_BEHAVIOR.put(Items.WATER_BUCKET, FILL_CAULDRON);
        EMPTY_CAULDRON_BEHAVIOR.remove(Items.LAVA_BUCKET);

        WATER_CAULDRON_BEHAVIOR.putAll(CauldronBehavior.WATER_CAULDRON_BEHAVIOR);
        WATER_CAULDRON_BEHAVIOR.put(Items.BUCKET, EMPTY_CAULDRON);
    }

    CauldronBehavior EMPTY_CAULDRON = (state, world, pos, player, hand, stack) ->
            emptyCauldron(state, world, pos, player, hand, stack, new ItemStack(Items.WATER_BUCKET), statex -> statex.get(AlchemistCauldronBlock.LEVEL) == 3, SoundEvents.ITEM_BUCKET_FILL);

    CauldronBehavior FILL_CAULDRON = (state, world, pos, player, hand, stack) ->
            CauldronBehavior.fillCauldron(world, pos, player, hand, stack, ALCHEMIST_CAULDRON_BLOCK.getDefaultState().with(AlchemistCauldronBlock.LEVEL, 3), SoundEvents.ITEM_BUCKET_EMPTY);

    private static ActionResult emptyCauldron(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, ItemStack output, Predicate<BlockState> fullPredicate, SoundEvent soundEvent) {
        if (!fullPredicate.test(state)) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, output));
            world.setBlockState(pos, ALCHEMIST_CAULDRON_EMPTY_BLOCK.getDefaultState());
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
        }

        return ActionResult.success(world.isClient);
    }

}
