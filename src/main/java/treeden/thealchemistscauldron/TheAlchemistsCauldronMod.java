package treeden.thealchemistscauldron;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import treeden.thealchemistscauldron.block.AlchemistCauldronBehavior;
import treeden.thealchemistscauldron.block.AlchemistCauldronBlock;
import treeden.thealchemistscauldron.block.AlchemistCauldronEmptyBlock;
import treeden.thealchemistscauldron.block.AlchemistTableBlock;
import treeden.thealchemistscauldron.entity.AlchemistCauldronBlockEntity;
import treeden.thealchemistscauldron.entity.AlchemistTableBlockEntity;
import treeden.thealchemistscauldron.item.LiquidDropperItem;
import treeden.thealchemistscauldron.item.SpoonItem;
import treeden.thealchemistscauldron.recipe.WaterPotionCauldronRecipe;
import treeden.thealchemistscauldron.screen.AlchemistTableScreenHandler;
import treeden.thealchemistscauldron.serializer.WaterPotionRecipeSerializer;

public class TheAlchemistsCauldronMod implements ModInitializer {
    public static final String MOD_ID = "thealchemistscauldron";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Item WOODEN_SPOON_ITEM = new SpoonItem(new FabricItemSettings(), 0.25f);
    public static final Item IRON_SPOON_ITEM = new SpoonItem(new FabricItemSettings(), 0.3f);
    public static final Item DIAMOND_SPOON_ITEM = new SpoonItem(new FabricItemSettings(), 0.4f);

    public static final Item LIQUID_DROPPER_ITEM = new LiquidDropperItem(new FabricItemSettings().maxCount(1));

    public static final Block ALCHEMIST_CAULDRON_EMPTY_BLOCK = new AlchemistCauldronEmptyBlock(FabricBlockSettings.copyOf(Blocks.CAULDRON).requiresTool());
    public static final Block ALCHEMIST_CAULDRON_BLOCK = new AlchemistCauldronBlock(FabricBlockSettings.copyOf(Blocks.WATER_CAULDRON).requiresTool());
    public static final BlockEntityType<AlchemistCauldronBlockEntity> ALCHEMIST_CAULDRON_BLOCK_ENTITY_TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(MOD_ID, "alchemist_cauldron_block_entity"),
            FabricBlockEntityTypeBuilder.create(AlchemistCauldronBlockEntity::new, ALCHEMIST_CAULDRON_BLOCK).build()
    );

    public static final Block ALCHEMIST_TABLE_BLOCK = new AlchemistTableBlock(FabricBlockSettings.create());
    public static BlockEntityType<AlchemistTableBlockEntity> ALCHEMIST_TABLE_BLOCK_ENTITY_TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            id("alchemist_table_block_entity"),
            FabricBlockEntityTypeBuilder.create(AlchemistTableBlockEntity::new, ALCHEMIST_CAULDRON_BLOCK).build()
    );

    public static ScreenHandlerType<AlchemistTableScreenHandler> ALCHEMIST_TABLE_SCREEN_HANDLER_TYPE = new ScreenHandlerType<>(AlchemistTableScreenHandler::new, FeatureSet.empty());

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        Registry.register(Registries.RECIPE_SERIALIZER, WaterPotionRecipeSerializer.ID, WaterPotionRecipeSerializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, id(WaterPotionCauldronRecipe.Type.ID), WaterPotionCauldronRecipe.Type.INSTANCE);

        Registry.register(Registries.SCREEN_HANDLER, id("alchemist_table_screen_handler"), ALCHEMIST_TABLE_SCREEN_HANDLER_TYPE);

        Registry.register(Registries.BLOCK, id("alchemist_table_block"), ALCHEMIST_TABLE_BLOCK);
        Registry.register(Registries.BLOCK, id("alchemist_cauldron_empty_block"), ALCHEMIST_CAULDRON_EMPTY_BLOCK);
        Registry.register(Registries.BLOCK, id("alchemist_cauldron_block"), ALCHEMIST_CAULDRON_BLOCK);

        Registry.register(Registries.ITEM, id("wooden_spoon"), WOODEN_SPOON_ITEM);
        Registry.register(Registries.ITEM, id("iron_spoon"), IRON_SPOON_ITEM);
        Registry.register(Registries.ITEM, id("diamond_spoon"), DIAMOND_SPOON_ITEM);
        Registry.register(Registries.ITEM, id("liquid_dropper"), LIQUID_DROPPER_ITEM);
        Registry.register(Registries.ITEM, id("alchemist_table_item"), new BlockItem(ALCHEMIST_TABLE_BLOCK, new FabricItemSettings()));
        Registry.register(Registries.ITEM, id("alchemist_cauldron_empty_item"), new BlockItem(ALCHEMIST_CAULDRON_EMPTY_BLOCK, new FabricItemSettings()));

        AlchemistCauldronBehavior.registerBehaviors();

        LOGGER.info("The Alchemists Cauldron initialized!");
    }
}