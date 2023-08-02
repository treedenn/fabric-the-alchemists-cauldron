package treeden.thealchemistscauldron.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class SingleStackInventoryImpl implements SingleStackInventory {
    private final List<ItemStack> inventory;

    public SingleStackInventoryImpl(ItemStack itemStack) {
        this.inventory = DefaultedList.ofSize(1, itemStack);
    }

    public SingleStackInventoryImpl() {
        this(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(0);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (this.getStack(slot).isEmpty())
            return null;

        ItemStack stack = this.getStack(slot);
        if (stack.getCount() < amount) {
            setStack(0, ItemStack.EMPTY);
            stack.setCount(0);
        } else
            stack.decrement(amount);

        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(0, stack);
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }
}
