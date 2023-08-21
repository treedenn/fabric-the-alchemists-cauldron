package treeden.thealchemistscauldron.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import treeden.thealchemistscauldron.entity.AlchemistCauldronBlockEntity;


public class LiquidDropperItem extends Item {

    public LiquidDropperItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockEntity entity = context.getWorld().getBlockEntity(context.getBlockPos());
        ItemStack stack = context.getStack();

        if (entity instanceof AlchemistCauldronBlockEntity) {
            ((AlchemistCauldronBlockEntity) entity).sampleLiquid(stack);
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.hasNbt();
    }
}
