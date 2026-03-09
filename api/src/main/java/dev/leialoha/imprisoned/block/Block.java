package dev.leialoha.imprisoned.block;

import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.leialoha.imprisoned.data.ResourceKey;
import dev.leialoha.imprisoned.item.ItemHolder;

public record Block(BlockData data, BlockAttributes attributes, BlockDrops drops, BlockRequirements requirements) {

    public static final Codec<Block> CODEC;

    public boolean is(ResourceKey block) {
        return data.matches(block, null);
    }

    public boolean is(ResourceKey block, Map<String, String> states) {
        return data.matches(block, states);
    }

    public int getDamageAmount(ItemHolder holder) {
        boolean destroyable_by_hand = requirements.destroyableByHand;
        if (holder == null && !destroyable_by_hand) return 0;

        // TOOD: implement a item level system
        int level = (holder == null) ? 0 : 1;

        int required_level = attributes.requiredToolLevel;
        float damage_reduction = attributes.baseDamageReduction;

        int level_diff = level - required_level;

        float over_damage_mult = requirements.overLevelDamageMultiplier * (float) level_diff;
        float under_damage_mult = requirements.underLevelDamageMultiplier / (float) Math.max(-level_diff, 1);

        float mult = (level_diff > 0) ? over_damage_mult
            : (level_diff == 0) ? 1
            : under_damage_mult;

        // TODO: Replace 10 with a tool.getDamageInvoked
        int mult_damage = (int) Math.round(10 * mult * (1f - damage_reduction));

        int max_damage = attributes.maxDamagePerAttack;
        int min_damage = attributes.minDamagePerAttack;

        return Math.clamp(mult_damage, min_damage, max_damage);

        // ToolType required_tool_type = requirements.requiredToolType;
        
    }

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                BlockData.CODEC.fieldOf("block").forGetter(Block::data),
                BlockAttributes.CODEC.fieldOf("attributes").forGetter(Block::attributes),
                BlockDrops.CODEC.fieldOf("drops").forGetter(Block::drops),
                BlockRequirements.CODEC.fieldOf("requirements").forGetter(Block::requirements)
            ).apply(instance, Block::new)
        );
    }
}
