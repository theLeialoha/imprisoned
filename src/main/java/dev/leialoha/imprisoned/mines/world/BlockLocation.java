package dev.leialoha.imprisoned.mines.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public record BlockLocation(int x, int y, int z, World world) {
    
    public static BlockLocation from(Location location) {
        return new BlockLocation(
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ(),
            location.getWorld()
        );
    }

    public Location getLocation() {
        return new Location(world, x, y, z);
    }

    public Block getBlock() {
        return getLocation().getBlock();
    }

}
