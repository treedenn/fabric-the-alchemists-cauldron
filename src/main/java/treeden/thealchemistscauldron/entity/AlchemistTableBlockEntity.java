package treeden.thealchemistscauldron.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import treeden.thealchemistscauldron.TheAlchemistsCauldronMod;
import treeden.thealchemistscauldron.screen.AlchemistTableScreenHandler;

public class AlchemistTableBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    protected SimpleInventory inventory = new SimpleInventory(1);

    public AlchemistTableBlockEntity(BlockPos pos, BlockState state) {
        super(TheAlchemistsCauldronMod.ALCHEMIST_TABLE_BLOCK_ENTITY_TYPE, pos, state);
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

        nbt.put("inventory", inventory.toNbtList());
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.getList("inventory", NbtElement.END_TYPE);
    }
}
