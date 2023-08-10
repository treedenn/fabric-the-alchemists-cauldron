package treeden.thealchemistscauldron.nbt;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public interface CauldronNbt {
    String MIXED = "mixed";
    String TEMPERATURE = "temperature";
    String DURATION = "duration";
    String BASE_POTION = "base_potion";
    String POTION_EFFECTS_SIZE = "potion_effects_size";
    String POTION_EFFECT_ID = "potion_effect_id_";
    String POTION_EFFECT_AMOUNT = "potion_effect_amount_";

    static void writeToNbtCompound(NbtCompound nbt, float mixed, float temperature, int duration, Item basePotion, Map<StatusEffect, Float> effects) {
        nbt.putFloat(MIXED, mixed);
        nbt.putFloat(TEMPERATURE, temperature);
        nbt.putInt(DURATION, duration);
        nbt.putString(BASE_POTION, Registries.ITEM.getId(basePotion).toString());
        nbt.putInt(POTION_EFFECTS_SIZE, effects.size());

        int count = 0;
        for (Map.Entry<StatusEffect, Float> entry : effects.entrySet()) {
            nbt.putString(POTION_EFFECT_ID + count, Objects.requireNonNull(Registries.STATUS_EFFECT.getId(entry.getKey())).toString());
            nbt.putFloat(POTION_EFFECT_AMOUNT + count, entry.getValue());
            count++;
        }
    }

    static float getMixed(NbtCompound nbt) {
        return nbt.getFloat(TEMPERATURE);
    }

    static float getTemperature(NbtCompound nbt) {
        return nbt.getFloat(TEMPERATURE);
    }

    static int getDuration(NbtCompound nbt) {
        return nbt.getInt(DURATION);
    }

    static Item getBasePotion(NbtCompound nbt) {
        return Registries.ITEM.get(new Identifier(nbt.getString(BASE_POTION)));
    }

    static Map<StatusEffect, Float> getEffects(NbtCompound nbt) {
        Map<StatusEffect, Float> effects = new HashMap<>();
        for (int i = 0; i < nbt.getInt(POTION_EFFECTS_SIZE); i++) {
            effects.put(Registries.STATUS_EFFECT.get(new Identifier(nbt.getString(POTION_EFFECT_ID + i))), nbt.getFloat(POTION_EFFECT_AMOUNT + i));
        }

        return effects;
    }
}
