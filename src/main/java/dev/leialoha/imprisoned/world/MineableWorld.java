package dev.leialoha.imprisoned.world;

import org.bukkit.block.Block;

import dev.leialoha.imprisoned.world.data.BlockMetaData;

public class MineableWorld {
    
    public static BlockMetaData getMetaData(BlockLocation pos) {
        Block block = pos.getBlock();
        return BlockMetaData.get(block);
    }

}
