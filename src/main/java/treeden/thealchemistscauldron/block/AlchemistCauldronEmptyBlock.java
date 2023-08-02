package treeden.thealchemistscauldron.block;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;


public class AlchemistCauldronEmptyBlock extends AbstractCauldronBlock {
    public AlchemistCauldronEmptyBlock(Settings settings) {
        super(settings, AlchemistCauldronBehavior.EMPTY_CAULDRON_BEHAVIOR);
    }

    @Override
    public boolean isFull(BlockState blockState) {
        return false;
    }
}
