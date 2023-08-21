package treeden.thealchemistscauldron.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import treeden.thealchemistscauldron.TheAlchemistsCauldronMod;
import treeden.thealchemistscauldron.screen.AlchemistTableScreenHandler;

public class AlchemistTableBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    protected SimpleInventory inventory;

    public AlchemistTableBlockEntity(BlockPos pos, BlockState state) {
        super(TheAlchemistsCauldronMod.ALCHEMIST_TABLE_BLOCK_ENTITY_TYPE, pos, state);

        this.inventory = new SimpleInventory(1) {
            @Override
            public void markDirty() {
                super.markDirty();
                AlchemistTableBlockEntity.this.markDirty();
            }
        };
    }

    public boolean canInsert(ItemStack itemStack) {
        boolean canInsert = this.inventory.canInsert(itemStack);
        if (canInsert) {
            this.inventory.addStack(itemStack);
            itemStack.decrement(1);
            markDirty();
        }

        return canInsert;
    }

    public void extract(PlayerInventory inventory) {
        ItemStack stack = this.inventory.getStack(0);
        if (!stack.isEmpty()) {
            inventory.insertStack(stack);
            markDirty();
        }
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AlchemistTableScreenHandler(syncId, playerInventory, inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory.stacks);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory.stacks);
        super.writeNbt(nbt);
    }
}
