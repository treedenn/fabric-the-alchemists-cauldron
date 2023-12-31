package treeden.thealchemistscauldron.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import treeden.thealchemistscauldron.entity.AlchemistCauldronBlockEntity;

public class SpoonItem extends Item {
    protected float stirPower;

    public SpoonItem(float stirPower) {
        super(new FabricItemSettings());
        this.stirPower = stirPower;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockEntity entity = context.getWorld().getBlockEntity(context.getBlockPos());
        if (entity instanceof AlchemistCauldronBlockEntity) {
            if (context.getPlayer() == null)
                return ActionResult.FAIL;

            ((AlchemistCauldronBlockEntity) entity).stir(this.stirPower);
            context.getPlayer().getItemCooldownManager().set(this, 10);
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }
}
