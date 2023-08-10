package treeden.thealchemistscauldron;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import treeden.thealchemistscauldron.screen.AlchemistTableScreen;

@Environment(EnvType.CLIENT)
public class TheAlchemistsCauldronClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(TheAlchemistsCauldronMod.ALCHEMIST_TABLE_SCREEN_HANDLER_TYPE, AlchemistTableScreen::new);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            assert world != null;
            return BiomeColors.getWaterColor(world, pos);
        }, TheAlchemistsCauldronMod.ALCHEMIST_CAULDRON_BLOCK);
    }
}