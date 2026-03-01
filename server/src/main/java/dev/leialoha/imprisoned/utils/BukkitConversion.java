package dev.leialoha.imprisoned.utils;

import dev.leialoha.imprisoned.data.IntLocation;
import dev.leialoha.imprisoned.data.ResourceKey;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

public final class BukkitConversion {

    private BukkitConversion() {
        throw new IllegalAccessError("Class doesn't need to be initalized");
    }

    public static ResourceKey from(NamespacedKey key) {
        return ResourceKey.fromNamespaceAndPath(key.namespace(), key.value());
    }

    public static NamespacedKey to(ResourceKey key) {
        return new NamespacedKey(key.getNamespace(), key.getPath());
    }

    public static IntLocation from(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        World world = location.getWorld();
        ResourceKey key = from(world.getKey());

        return new IntLocation(x, y, z, key);
    }

    public static Location to(IntLocation location) {
        int x = location.x();
        int y = location.y();
        int z = location.z();

        ResourceKey key = location.world();
        NamespacedKey namespacedKey = to(key);
        World world = Bukkit.getWorld(namespacedKey);

        return new Location(world, x, y, z);
    }

}
