package treeden.thealchemistscauldron;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import treeden.thealchemistscauldron.block.AlchemistCauldronBehavior;
import treeden.thealchemistscauldron.block.AlchemistCauldronBlock;
import treeden.thealchemistscauldron.block.AlchemistCauldronEmptyBlock;
import treeden.thealchemistscauldron.block.AlchemistTableBlock;
import treeden.thealchemistscauldron.entity.AlchemistCauldronBlockEntity;
import treeden.thealchemistscauldron.item.SpoonItem;
import treeden.thealchemistscauldron.recipe.WaterPotionCauldronRecipe;
import treeden.thealchemistscauldron.serializer.WaterPotionRecipeSerializer;

public class TheAlchemistsCauldronMod implements ModInitializer {
    public static final String MOD_ID = "thealchemistscauldron";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Item WOODEN_SPOON_ITEM = new SpoonItem(new FabricItemSettings(), 0.25f);
    public static final Item IRON_SPOON_ITEM = new SpoonItem(new FabricItemSettings(), 0.3f);
    public static final Item DIAMOND_SPOON_ITEM = new SpoonItem(new FabricItemSettings(), 0.4f);

    public static final Block ALCHEMIST_TABLE_BLOCK = new AlchemistTableBlock(FabricBlockSettings.create());

    public static final Block ALCHEMIST_CAULDRON_EMPTY_BLOCK = new AlchemistCauldronEmptyBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON).requiresTool());
    public static final Block ALCHEMIST_CAULDRON_BLOCK = new AlchemistCauldronBlock(FabricBlockSettings.copyOf(Blocks.WATER_CAULDRON).requiresTool());
    public static final BlockEntityType<AlchemistCauldronBlockEntity> ALCHEMIST_CAULDRON_BLOCK_ENTITY_TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(MOD_ID, "alchemist_cauldron_block_entity"),
            FabricBlockEntityTypeBuilder.create(AlchemistCauldronBlockEntity::new, ALCHEMIST_CAULDRON_BLOCK).build()
    );

    @Override
    public void onInitialize() {
        AlchemistCauldronBehavior.registerBehaviors();

        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "wooden_spoon"), WOODEN_SPOON_ITEM);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "iron_spoon"), IRON_SPOON_ITEM);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "diamond_spoon"), DIAMOND_SPOON_ITEM);

        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "alchemist_table_block"), ALCHEMIST_TABLE_BLOCK);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "alchemist_table_item"), new BlockItem(ALCHEMIST_TABLE_BLOCK, new FabricItemSettings()));

        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "alchemist_cauldron_empty_block"), ALCHEMIST_CAULDRON_EMPTY_BLOCK);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "alchemist_cauldron_empty_item"), new BlockItem(ALCHEMIST_CAULDRON_EMPTY_BLOCK, new FabricItemSettings()));

        Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "alchemist_cauldron_block"), ALCHEMIST_CAULDRON_BLOCK);

        Registry.register(Registries.RECIPE_SERIALIZER, WaterPotionRecipeSerializer.ID, WaterPotionRecipeSerializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(MOD_ID, WaterPotionCauldronRecipe.Type.ID), WaterPotionCauldronRecipe.Type.INSTANCE);

        LOGGER.info("Hello Fabric world!");
    }
}