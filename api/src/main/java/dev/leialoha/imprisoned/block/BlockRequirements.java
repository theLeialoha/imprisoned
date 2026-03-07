package dev.leialoha.imprisoned.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.leialoha.imprisoned.tool.ToolType;

public class BlockRequirements {
    
    public static final Codec<BlockRequirements> CODEC;

    public float underLevelDamageMultiplier;
    public float overLevelDamageMultiplier;

    public ToolType requiredToolType;
    public boolean destroyableByHand;
    public boolean harvestable;

    public BlockRequirements(
        float underLevelDamageMultiplier,
        float overLevelDamageMultiplier,
        ToolType requiredToolType,
        boolean destroyableByHand,
        boolean harvestable
    ) {
        this.underLevelDamageMultiplier = underLevelDamageMultiplier;
        this.overLevelDamageMultiplier = overLevelDamageMultiplier;
        this.requiredToolType = requiredToolType;
        this.destroyableByHand = destroyableByHand;
        this.harvestable = harvestable;
    }

    public float getUnderLevelDamageMultiplier() {
        return this.underLevelDamageMultiplier;
    }

    public float getOverLevelDamageMultiplier() {
        return this.overLevelDamageMultiplier;
    }

    public ToolType getRequiredToolType() {
        return this.requiredToolType;
    }

    public boolean getDestroyableByHand() {
        return this.destroyableByHand;
    }

    public boolean getHarvestable() {
        return this.harvestable;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.FLOAT.optionalFieldOf("under_level_damage_multiplier", 0f).forGetter(BlockRequirements::getUnderLevelDamageMultiplier),
                Codec.FLOAT.optionalFieldOf("over_level_damage_multiplier", 1.1f).forGetter(BlockRequirements::getOverLevelDamageMultiplier),
                Codec.STRING.xmap(ToolType::valueOf, ToolType::toString).optionalFieldOf("required_tool_type", ToolType.PICKAXE).forGetter(BlockRequirements::getRequiredToolType),
                Codec.BOOL.optionalFieldOf("destroyable_by_hand", false).forGetter(BlockRequirements::getDestroyableByHand),
                Codec.BOOL.optionalFieldOf("harvestable", true).forGetter(BlockRequirements::getHarvestable)
            ).apply(instance, BlockRequirements::new)
        );
    }
}

