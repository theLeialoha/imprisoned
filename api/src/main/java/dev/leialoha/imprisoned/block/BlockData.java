package dev.leialoha.imprisoned.block;

import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.leialoha.imprisoned.data.ResourceKey;

public record BlockData(ResourceKey blockId, Map<String, String> blockStates) {

    public static final Codec<BlockData> CODEC;

    // public boolean equals(BlockData data) {
    //     return data == this
    //         || data instanceof BlockData blockData
    //         && blockData.blockId.equals(this.blockId);
    // }


    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                ResourceKey.CODEC.fieldOf("id").forGetter(BlockData::blockId),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("states", Map.of()).forGetter(BlockData::blockStates)
            ).apply(instance, BlockData::new)
        );
    }

}
