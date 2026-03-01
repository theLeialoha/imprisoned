package dev.leialoha.imprisoned.block;

import dev.leialoha.imprisoned.data.ResourceKey;

public class BlockData {

    private final ResourceKey blockId;

    public BlockData(ResourceKey blockId) {
        this.blockId = blockId;
    }

    public ResourceKey getBlockId() {
        return this.blockId;
    }

    public ResourceKey getDropsId() {
        return this.blockId;
    }

    public boolean equals(BlockData data) {
        return data == this
            || data instanceof BlockData blockData
            && blockData.blockId.equals(this.blockId);
    }

}
