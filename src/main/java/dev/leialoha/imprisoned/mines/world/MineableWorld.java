package dev.leialoha.imprisoned.mines.world;

import org.bukkit.block.Block;

import dev.leialoha.imprisoned.mines.world.data.BlockMetaData;

public class MineableWorld {
    
    public static BlockMetaData getMetaData(BlockLocation pos) {
        Block block = pos.getBlock();
        return BlockMetaData.get(block);
    }

    public static boolean withinRegion(BlockLocation location) {
        return true;
    }

}
