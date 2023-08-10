package treeden.thealchemistscauldron.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import treeden.thealchemistscauldron.TheAlchemistsCauldronMod;
import treeden.thealchemistscauldron.recipe.WaterPotionCauldronRecipe;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

public class WaterPotionRecipeSerializer implements RecipeSerializer<WaterPotionCauldronRecipe> {
    public static final WaterPotionRecipeSerializer INSTANCE = new WaterPotionRecipeSerializer();
    public static final Identifier ID = new Identifier(TheAlchemistsCauldronMod.MOD_ID, "water_ingredient");

    public WaterPotionRecipeSerializer() {
    }

    @Override
    public WaterPotionCauldronRecipe read(Identifier id, JsonObject json) {
        WaterPotionCauldronRecipe recipe = new WaterPotionCauldronRecipe(id);

        if (!json.has("item"))
            throw new JsonSyntaxException("Missing 'item' attribute (id: %s)".formatted(id));
        recipe.setItem(Registries.ITEM.get((new Identifier(json.get("item").getAsString()))));

        if (json.has("base_potion")) {
            Identifier identifier = new Identifier(json.get("base_potion").getAsString());
            Item basePotion = Registries.ITEM.get(identifier);
            recipe.setBasePotion(basePotion);
        }

        if (json.has("effects")) {
            Map<StatusEffect, Float> effects = new HashMap<>(5);
            for (JsonElement effect : json.getAsJsonArray("effects")) {
                if (!effect.isJsonObject())
                    throw new JsonSyntaxException("Element '%s' is not an object (id: %s)".formatted(effect.getAsString(), id));

                JsonObject object = effect.getAsJsonObject();
                if (!object.has("type") || !object.has("amount"))
                    throw new JsonSyntaxException("Missing 'type' or 'amount' attribute (id: %s)".formatted(id));

                effects.put(Registries.STATUS_EFFECT.get(new Identifier(object.get("type").getAsString())), object.get("amount").getAsFloat());
            }

            recipe.setEffects(effects);
        }

        return recipe;
    }

    @Override
    public WaterPotionCauldronRecipe read(Identifier id, PacketByteBuf buf) {
        WaterPotionCauldronRecipe recipe = new WaterPotionCauldronRecipe(id);

        recipe.setItem(Registries.ITEM.get(new Identifier(buf.readString())));
        recipe.setBasePotion(Registries.ITEM.get(new Identifier(buf.readString())));
        recipe.setDuration(buf.readInt());
        recipe.setEffects(
                buf.readMap((IntFunction<Map<StatusEffect, Float>>) HashMap::new,
                        packetByteBuf -> Registries.STATUS_EFFECT.get(packetByteBuf.readIdentifier()),
                        PacketByteBuf::readFloat)
        );

        return recipe;
    }

    @Override
    public void write(PacketByteBuf buf, WaterPotionCauldronRecipe recipe) {
        buf.writeIdentifier(Registries.ITEM.getId(recipe.getItem()));
        buf.writeIdentifier(Registries.ITEM.getId(recipe.getBasePotion()));
        buf.writeInt(recipe.getDuration());
        buf.writeMap(recipe.getEffects(), (packetByteBuf, statusEffect) ->
                        packetByteBuf.writeIdentifier(Registries.STATUS_EFFECT.getId(statusEffect)),
                PacketByteBuf::writeFloat);
    }
}
