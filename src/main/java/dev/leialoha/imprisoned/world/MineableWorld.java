package dev.leialoha.imprisoned.world;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import dev.leialoha.imprisoned.world.data.BlockMetaData;
import net.minecraft.core.BlockPos;

public class MineableWorld {
    
    public static BlockMetaData getMetaData(BlockPos pos) {
        World world = getMiningWorld();
        Block block = world.getBlockAt(pos.getX(), pos.getY(), pos.getZ());
        return BlockMetaData.get(block);
    }

    private static World getMiningWorld() {
        // This should be the overworld
        // without hardcoding it

        return Bukkit.getWorlds().stream()
            .filter(w -> !w.hasCeiling())
            .filter(w -> w.getCoordinateScale() == 1)
            .filter(w -> !w.isPiglinSafe())
            .findFirst().orElse(Bukkit.getWorld("world"));
    }

}
