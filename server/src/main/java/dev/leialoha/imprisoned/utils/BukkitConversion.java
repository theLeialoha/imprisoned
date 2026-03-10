package dev.leialoha.imprisoned.utils;

import dev.leialoha.imprisoned.data.IntLocation;
import dev.leialoha.imprisoned.data.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

public final class BukkitConversion {

    private BukkitConversion() {
        throw new IllegalAccessError("Class doesn't need to be initalized");
    }

    public static ResourceKey asResourceKey(NamespacedKey key) {
        return ResourceKey.fromNamespaceAndPath(key.namespace(), key.value());
    }

    public static NamespacedKey asNamepacedKey(ResourceKey key) {
        return new NamespacedKey(key.getNamespace(), key.getPath());
    }

    public static Identifier asIdentifier(ResourceKey key) {
        return Identifier.fromNamespaceAndPath(key.getNamespace(), key.getPath());
    }

    public static IntLocation asIntLocation(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        World world = location.getWorld();
        ResourceKey key = asResourceKey(world.getKey());

        return new IntLocation(x, y, z, key);
    }

    public static Location asLocation(IntLocation location) {
        int x = location.x();
        int y = location.y();
        int z = location.z();

        ResourceKey key = location.world();
        NamespacedKey namespacedKey = asNamepacedKey(key);
        World world = Bukkit.getWorld(namespacedKey);

        return new Location(world, x, y, z);
    }

    public static Component asComponent(String string) {
        if (string == null) return null;
        return Component.literal(string);
    }

    public static Component asUnstyledComponent(String string) {
        return Component.empty().withStyle(
            Style.EMPTY.withItalic(false)
        ).append(asComponent(string));
    }
}
