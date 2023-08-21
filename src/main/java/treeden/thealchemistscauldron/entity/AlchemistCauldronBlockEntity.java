package treeden.thealchemistscauldron.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import treeden.thealchemistscauldron.TheAlchemistsCauldronMod;
import treeden.thealchemistscauldron.block.AlchemistCauldronBlock;
import treeden.thealchemistscauldron.inventory.SingleStackInventoryImpl;
import treeden.thealchemistscauldron.nbt.CauldronNbt;
import treeden.thealchemistscauldron.recipe.WaterPotionCauldronRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AlchemistCauldronBlockEntity extends BlockEntity {

    protected final int UPDATE_RATE = 10;
    protected final float TEMPERATURE_COST_PER_ITEM = 15f;
    protected final float MIN_TEMP_CONSUME = 50f;
    protected final float MIN_TEMP = -50f;
    protected final float MAX_TEMP = 700f;
    protected final float BOIL_TEMP = 100f;
    protected final int MAX_POTION_EFFECTS = 5;
    protected final int MAX_POTION_LEVEL = 3;
    protected final int MAX_POTION_DURATION = 6000; // 5 minutes

    protected static Map<Block, Float> tempBlocks = new HashMap<>() {{
        put(Blocks.BLUE_ICE, -100f);
        put(Blocks.PACKED_ICE, -30f);
        put(Blocks.ICE, -15f);
        put(Blocks.WATER, 20f);
        put(Blocks.TORCH, 70f);
        put(Blocks.FIRE, 100f);
        put(Blocks.CAMPFIRE, 100f);
        put(Blocks.SOUL_CAMPFIRE, 125f);
        put(Blocks.SOUL_FIRE, 125f);
        put(Blocks.MAGMA_BLOCK, 500f);
        put(Blocks.LAVA, 1000f);
    }};

    protected float mixed;
    protected float temperature;
    protected int duration;
    protected Item basePotion;
    protected Map<StatusEffect, Float> effects;

    public AlchemistCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(TheAlchemistsCauldronMod.ALCHEMIST_CAULDRON_BLOCK_ENTITY_TYPE, pos, state);
        this.mixed = .4f;
        this.temperature = 0;
        this.duration = 0;
        this.basePotion = Items.POTION;
        this.effects = new HashMap<>(7);
    }

    public void stir(float power) {
        this.mixed = MathHelper.clamp(this.mixed + power, 0f, 1f);
    }

    public void sampleLiquid(ItemStack itemStack) {
        if (!itemStack.getItem().equals(TheAlchemistsCauldronMod.LIQUID_DROPPER_ITEM))
            return;

        NbtCompound nbt = new NbtCompound();
        CauldronNbt.writeToNbtCompound(nbt, this.mixed, this.temperature, this.duration, this.basePotion, this.effects);
        itemStack.setNbt(nbt);
    }

    public void showClientBoilingParticles(World world, BlockPos pos, BlockState state) {
        Integer waterLevel = state.get(AlchemistCauldronBlock.LEVEL);
        if (waterLevel != null && Math.random() < 0.5f) {
            int bubbles = (int) MathHelper.clampedMap(this.temperature, MIN_TEMP, MAX_TEMP, 2f, 7f);

            double waterHeight = (6.0 + (double) waterLevel * 3.0) / 16.0;
            for (int i = 0; i < bubbles; i++) {
                double xInsideCauldron = pos.getX() + 0.2f + Math.random() * 0.6f;
                double zInsideCauldron = pos.getZ() + 0.2f + Math.random() * 0.6f;
                world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP,
                        xInsideCauldron, pos.getY() + waterHeight, zInsideCauldron,
                        0f, 0.0f, 0f);
                world.addParticle(ParticleTypes.BUBBLE_POP,
                        xInsideCauldron, pos.getY() + waterHeight + 0.05f, zInsideCauldron,
                        0f, 0.03f, 0f);
            }
        }
    }

    public void showClientStirParticles(World world, BlockPos pos, BlockState state) {
        Integer waterLevel = state.get(AlchemistCauldronBlock.LEVEL);
        if (waterLevel != null && Math.random() < 0.2f) {
            double waterHeight = (6.0 + (double) waterLevel * 3.0) / 16.0;
            double xInsideCauldron = pos.getX() + 0.2f + Math.random() * 0.6f;
            double zInsideCauldron = pos.getZ() + 0.2f + Math.random() * 0.6f;
            world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    xInsideCauldron, pos.getY() + waterHeight+ 0.2f, zInsideCauldron,
                    0f, 0.007f, 0f);
        }
    }

    public void clientTick(World world, BlockPos pos, BlockState state) {
        if (this.temperature >= BOIL_TEMP) {
            this.showClientBoilingParticles(world, pos, state);

            if (this.mixed < 0.2f) {
                showClientStirParticles(world, pos, state);
            }
        }
    }

    public float getBiomeTemperature(World world, BlockPos pos) {
        Float biomeTemperature = world.getBiome(pos)
                .getKey()
                .map(key -> world.getRegistryManager().get(RegistryKeys.BIOME).get(key))
                .map(Biome::getTemperature)
                .orElse(0.8f);

        return MathHelper.clampedMap(biomeTemperature, -2.5f, -2.5f, -40, 65f);
    }

    public float getTemperatureFromUnderneath(World world, float biomeTemperature) {
        BlockState blockStateDown = world.getBlockState(pos.offset(Direction.DOWN, 1));
        Block block = blockStateDown.getBlock();

        float temperature = biomeTemperature;
        if (tempBlocks.containsKey(block)) {
            if (block == Blocks.CAMPFIRE || block == Blocks.SOUL_CAMPFIRE) {
                if (blockStateDown.get(CampfireBlock.LIT))
                    temperature = tempBlocks.get(block);
            } else {
                temperature = tempBlocks.get(block);
            }
        }

        return temperature;
    }

    public float calculateTemperature(float blockTemperature, float biomeTemperature) {
        return (blockTemperature - this.temperature) * 0.09f + (biomeTemperature - this.temperature) * 0.03f;
    }

    public void serverTick(World world, BlockPos pos, BlockState state) {
        float biomeTemperature = getBiomeTemperature(world, pos);
        float blockTemperature = getTemperatureFromUnderneath(world, biomeTemperature);
        float deltaTemperature = calculateTemperature(blockTemperature, biomeTemperature);
        this.temperature = MathHelper.clamp(this.temperature + deltaTemperature, MIN_TEMP, MAX_TEMP);

        if (this.temperature >= BOIL_TEMP) {
            this.mixed = MathHelper.clamp(this.mixed - (this.mixed > 0.15f ? 0.021f : 0.006f), 0f, 1f);

            if (this.mixed == 0 && Math.random() > .5f) {
                AlchemistCauldronBlock.decreaseFluid(world, pos, state);
            }
        } else {
            this.mixed = MathHelper.clamp(this.mixed + (.3f - this.mixed) * .1f, 0f, 1f);
        }

        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient()) {
            this.clientTick(world, pos, state);
        } else {
            if (world.getTime() % UPDATE_RATE == 0) {
                this.serverTick(world, pos, state);
            }
        }
    }

    public boolean tryAddIngredients(World world, ItemStack itemStack) {
        Optional<WaterPotionCauldronRecipe> recipeOptional = world.getRecipeManager()
                .getFirstMatch(WaterPotionCauldronRecipe.Type.INSTANCE, new SingleStackInventoryImpl(itemStack.copy()), world);

        if (recipeOptional.isEmpty())
            return false;

        WaterPotionCauldronRecipe recipe = recipeOptional.get();
        if (!canConsumeIngredients(recipe, itemStack.getCount()))
            return false;

        mergeIngredients(recipe, itemStack.getCount());
        return true;
    }

    public ItemStack createPotion() {
        List<StatusEffectInstance> statusEffectInstances = this.effects.entrySet().stream()
                .filter(entry -> entry.getValue() >= 1f)
                .sorted(Map.Entry.comparingByValue((o1, o2) -> Float.compare(o2, o1)))
                .limit(MAX_POTION_EFFECTS)
                .map(entry -> new StatusEffectInstance(entry.getKey(), 600, entry.getValue().intValue() - 1))
                .collect(Collectors.toList());

        ItemStack potion = new ItemStack(basePotion);
        PotionUtil.setPotion(potion, Potions.WATER);
        PotionUtil.setCustomPotionEffects(potion, statusEffectInstances);
        potion.setCustomName(Text.of("Elixir of Dangerous Mixture"));

        return potion;
    }

    protected boolean canConsumeIngredients(WaterPotionCauldronRecipe recipe, int count) {
        if (MIN_TEMP_CONSUME >= this.temperature - count * TEMPERATURE_COST_PER_ITEM)
            return false;

        if (duration + recipe.getDuration() * count > MAX_POTION_DURATION)
            return false;

        for (Map.Entry<StatusEffect, Float> entry : recipe.getEffects().entrySet()) {
            float currLevel = this.effects.getOrDefault(entry.getKey(), 0f);
            float newLevel = currLevel + entry.getValue() * count;

            if (newLevel > MAX_POTION_LEVEL)
                return false;
        }

        return true;
    }

    protected void mergeIngredients(WaterPotionCauldronRecipe recipe, int count) {
        this.temperature -= count * TEMPERATURE_COST_PER_ITEM;
        this.duration += recipe.getDuration() * count;
        this.basePotion = recipe.getBasePotion() == null ? this.basePotion : recipe.getBasePotion();

        for (Map.Entry<StatusEffect, Float> entry : recipe.getEffects().entrySet()) {
            this.effects.merge(entry.getKey(), entry.getValue(),
                    ((current, statusAmount) -> {
                        float sum = Float.sum(current, statusAmount * count);
                        return Math.min(sum, MAX_POTION_LEVEL);
                    }));
        }

        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        CauldronNbt.writeToNbtCompound(nbt, this.mixed, this.temperature, this.duration, this.basePotion, this.effects);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.mixed = CauldronNbt.getMixed(nbt);
        this.temperature = CauldronNbt.getTemperature(nbt);
        this.duration = CauldronNbt.getDuration(nbt);
        this.basePotion = CauldronNbt.getBasePotion(nbt);
        this.effects = CauldronNbt.getEffects(nbt);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
