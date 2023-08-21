package treeden.thealchemistscauldron.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;


public class AlchemistCauldronEmptyBlock extends AbstractCauldronBlock {
    public AlchemistCauldronEmptyBlock() {
        super(FabricBlockSettings.copyOf(Blocks.CAULDRON), AlchemistCauldronBehavior.EMPTY_CAULDRON_BEHAVIOR);
    }

    @Override
    public boolean isFull(BlockState blockState) {
        return false;
    }
}
