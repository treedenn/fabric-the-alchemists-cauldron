package treeden.thealchemistscauldron.screen;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;
import treeden.thealchemistscauldron.TheAlchemistsCauldronMod;
import treeden.thealchemistscauldron.nbt.CauldronNbt;

import java.util.List;
import java.util.Map;

import static treeden.thealchemistscauldron.TheAlchemistsCauldronMod.ALCHEMIST_TABLE_SCREEN_HANDLER_TYPE;

public class AlchemistTableScreenHandler extends ScreenHandler {
    private final SimpleInventory inventory;

    protected float mixed;
    protected float temperature;
    protected int duration;
    protected Item basePotion;
    protected Map<StatusEffect, Float> effects;
    protected List<Map.Entry<StatusEffect, Float>> orderedEffects;

    public AlchemistTableScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(1));
    }

    public AlchemistTableScreenHandler(int syncId, PlayerInventory playerInventory, SimpleInventory inventory) {
        super(ALCHEMIST_TABLE_SCREEN_HANDLER_TYPE, syncId);

        checkSize(inventory, 1);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        // table slot
        this.addSlot(new Slot(inventory, 0, 168, 14) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.getItem().equals(TheAlchemistsCauldronMod.LIQUID_DROPPER_ITEM) && stack.getNbt() != null;
            }
        });

        // players inventory
        for (int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, 9 + column + inventoryRow * 9, 24 + column * 18, 84 + inventoryRow * 18));
            }
        }

        // hotbar
        for (int hotbarRow = 0; hotbarRow < 9; ++hotbarRow) {
            this.addSlot(new Slot(playerInventory, hotbarRow, 24 + hotbarRow * 18, 142));
        }

        this.inventory.addListener(this::onContentChanged);
    }

    public ItemStack getLiquidDropper() {
        ItemStack stack = this.getSlot(0).getStack();
        return stack.isEmpty() ? null : stack;
    }

    public List<Map.Entry<StatusEffect, Float>> getOrderedEffects() {
        return orderedEffects;
    }

    protected int getOverflowRows() {
        return this.effects.size() - 3;
    }

    protected int getRow(float scrollPosition) {
        return Math.max((int)((scrollPosition * this.getOverflowRows()) + 0.5), 0);
    }

    protected float getScrollPosition(int row) {
        return MathHelper.clamp(((float)row / (float)this.getOverflowRows()), 0.0f, 1.0f);
    }

    protected float getScrollPosition(float current, double amount) {
        return MathHelper.clamp((current - (float)(amount / (double)this.getOverflowRows())), 0.0f, 1.0f);
    }

    public void scrollItems(float position) {
        int i = this.getRow(position);
        this.orderedEffects = this.effects.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((o1, o2) -> Float.compare(o2, o1)))
                .toList()
                .subList(effects.size() + i - 4, i + 3);
    }

    public boolean shouldShowScrollbar() {
        return this.effects != null && this.effects.size() > 3;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if (inventory.size() == 0)
            return;

        ItemStack liquidDropper = getLiquidDropper();
        if (liquidDropper == null || liquidDropper.isEmpty())
            return;

        NbtCompound nbt = liquidDropper.getNbt();
        assert nbt != null;

        this.mixed = CauldronNbt.getMixed(nbt);
        this.temperature = CauldronNbt.getTemperature(nbt);
        this.duration = CauldronNbt.getDuration(nbt);
        this.basePotion = CauldronNbt.getBasePotion(nbt);
        this.effects = CauldronNbt.getEffects(nbt);
        this.scrollItems(0.0f);

        super.onContentChanged(inventory);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;

        Slot objSlot = this.getSlot(slot);
        if (objSlot != null && objSlot.hasStack()) {
            ItemStack originalItem = objSlot.getStack();
            itemStack = originalItem.copy();

            if (slot < this.inventory.size() && !this.insertItem(originalItem, this.inventory.size(), this.slots.size(), true)) {
                return  ItemStack.EMPTY;
            } else if (!this.insertItem(originalItem, 0, this.inventory.size(), false)) {
                return  ItemStack.EMPTY;
            }

            // item has been inserted at this stage
            if (!objSlot.getStack().isEmpty()) {
                objSlot.setStack(ItemStack.EMPTY);
            } else {
                objSlot.markDirty();
            }
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }
}
