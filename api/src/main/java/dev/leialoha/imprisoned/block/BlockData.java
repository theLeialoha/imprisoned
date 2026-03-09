package dev.leialoha.imprisoned.block;

import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.leialoha.imprisoned.data.ResourceKey;

public class BlockData {

    public static final Codec<BlockData> CODEC;

    public ResourceKey blockId;
    public Map<String, String> blockStates;

    public BlockData(
        ResourceKey blockId,
        Map<String, String> blockStates
    ) {
        this.blockId = blockId;
        this.blockStates = blockStates;
    }

    public ResourceKey getBlockId() {
        return blockId;
    }

    public Map<String, String> getBlockStates() {
        return blockStates;
    }

    // public boolean equals(BlockData data) {
    //     return data == this
    //         || data instanceof BlockData blockData
    //         && blockData.blockId.equals(this.blockId);
    // }

    public boolean matches(ResourceKey block, Map<String, String> states) {
        if (blockStates.isEmpty())
            return blockId.equals(block) && (states == null || states.isEmpty());

        return blockId.equals(block) && this.blockStates.equals(states);
    }

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                ResourceKey.CODEC.fieldOf("id").forGetter(BlockData::getBlockId),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("states", Map.of()).forGetter(BlockData::getBlockStates)
            ).apply(instance, BlockData::new)
        );
    }

}
