package dev.leialoha.imprisoned.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.leialoha.imprisoned.data.ResourceKey;

public class BlockDrops {

    public static final Codec<BlockDrops> CODEC;

    public ResourceKey dropTableId;

    public float xpReward;
    public int dropMultiplier;
    public float rareDropChance;
    public float fortuneScaling;

    public BlockDrops(ResourceKey dropTableId, float xpReward, int dropMultiplier, float rareDropChance, float fortuneScaling) {
        this.dropTableId = dropTableId;
        this.xpReward = xpReward;
        this.dropMultiplier = dropMultiplier;
        this.rareDropChance = rareDropChance;
        this.fortuneScaling = fortuneScaling;
    }

    public ResourceKey getDropTableId() {
        return dropTableId;
    }

    public float getXpReward() {
        return xpReward;
    }
    
    public int getDropMultiplier() {
        return dropMultiplier;
    }

    public float getRareDropChance() {
        return rareDropChance;
    }

    public float getFortuneScaling() {
        return fortuneScaling;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                ResourceKey.CODEC.fieldOf("drop_table_id").forGetter(BlockDrops::getDropTableId),
                Codec.FLOAT.optionalFieldOf("xp_reward", 1f).forGetter(BlockDrops::getXpReward),
                Codec.INT.optionalFieldOf("drop_multiplier", 1).forGetter(BlockDrops::getDropMultiplier),
                Codec.FLOAT.optionalFieldOf("rare_drop_chance", 0.0025f).forGetter(BlockDrops::getRareDropChance),
                Codec.FLOAT.optionalFieldOf("fortune_scaling", 0.0015f).forGetter(BlockDrops::getFortuneScaling)
            ).apply(instance, BlockDrops::new)
        );
    }
}
