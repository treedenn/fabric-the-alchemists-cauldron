package treeden.thealchemistscauldron.block;

import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

import java.util.Map;

public abstract class AbstractAlchemistCauldronBlock extends AbstractCauldronBlock {
    public AbstractAlchemistCauldronBlock(Settings settings, Map<Item, CauldronBehavior> behaviorMap) {
        super(settings, behaviorMap);
    }


}
