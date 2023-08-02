package treeden.thealchemistscauldron.mixin;

import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AbstractCauldronBlock.class)
public interface CauldronAccessor {
    @Accessor("behaviorMap")
    Map<Item, CauldronBehavior> getBehaviorMap();

    @Accessor("behaviorMap")
    void setBehaviorMap(Map<Item, CauldronBehavior> behaviorMap);
}