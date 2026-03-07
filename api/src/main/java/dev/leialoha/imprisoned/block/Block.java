package dev.leialoha.imprisoned.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Block(BlockData data, BlockAttributes attributes, BlockDrops drops, BlockRequirements requirements) {

    public static final Codec<Block> CODEC;

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
