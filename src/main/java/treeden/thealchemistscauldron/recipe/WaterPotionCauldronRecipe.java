package treeden.thealchemistscauldron.recipe;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.inventory.SingleStackInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import treeden.thealchemistscauldron.TheAlchemistsCauldronMod;
import treeden.thealchemistscauldron.serializer.WaterPotionRecipeSerializer;

import java.util.HashMap;
import java.util.Map;

public class WaterPotionCauldronRecipe implements Recipe<SingleStackInventory> {
    private final Identifier id;
    private int duration;
    private Item item;
    private Item basePotion;
    private Map<StatusEffect, Float> effects;

    public WaterPotionCauldronRecipe(Identifier id) {
        this.id = id;
        this.duration = 0;
        this.item = null;
        this.basePotion = null;
        this.effects = new HashMap<>();
    }

    public Item getBasePotion() {
        return basePotion;
    }

    public void setBasePotion(Item basePotion) {
        if (basePotion instanceof PotionItem) {
            this.basePotion = basePotion;
        } else {
            TheAlchemistsCauldronMod.LOGGER.info("Something is trying to assign an invalid base potion { basePotion = {} }", basePotion);
        }
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Map<StatusEffect, Float> getEffects() {
        return effects;
    }

    public void setEffects(Map<StatusEffect, Float> effects) {
        this.effects = effects;
    }

    @Override
    public boolean matches(SingleStackInventory inventory, World world) {
        return this.item.equals(inventory.getStack().getItem());
    }

    @Override
    public ItemStack craft(SingleStackInventory inventory, DynamicRegistryManager registryManager) {
        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return new ItemStack(Items.POTION);
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return WaterPotionRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public String toString() {
        return "WaterPotionCauldronRecipe{" +
                "id=" + id +
                ", basePotion=" + basePotion +
                ", duration=" + duration +
                ", effects=" + effects +
                '}';
    }

    public static class Type implements RecipeType<WaterPotionCauldronRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "water_potion_recipe";

        private Type() {
        }
    }
}
