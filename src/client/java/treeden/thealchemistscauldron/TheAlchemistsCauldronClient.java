package treeden.thealchemistscauldron;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;

public class TheAlchemistsCauldronClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            assert world != null;
            return BiomeColors.getWaterColor(world, pos);
        }, TheAlchemistsCauldronMod.ALCHEMIST_CAULDRON_BLOCK);
    }
}