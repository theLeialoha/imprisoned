package dev.leialoha.imprisoned.utils;

import dev.leialoha.imprisoned.data.IntLocation;
import dev.leialoha.imprisoned.data.ResourceKey;
import dev.leialoha.imprisoned.text.Style;
import dev.leialoha.imprisoned.text.StyledLore;
import dev.leialoha.imprisoned.text.StyledText;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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

    public static Component asComponent(StyledLore lore) {
        if ( lore == null
            || lore.getContent() == null
            || lore.getContent().isBlank()
        ) return Component.empty();

        MutableComponent out = Component.empty();

        lore.prefix.map(BukkitConversion::asComponent).ifPresent(out::append);
        out.append(asComponent((StyledText) lore));
        lore.suffix.map(BukkitConversion::asComponent).ifPresent(out::append);

        return out;
    }

    public static Component asComponent(StyledText lore) {
        return Component.literal(lore.getContent())
            .withStyle(asNMSCopy(lore.getStyle()));
    }

    protected static net.minecraft.network.chat.Style asNMSCopy(Style style) {
        return net.minecraft.network.chat.Style.EMPTY
            .withColor(style.color())
            .withBold(style.bold().orElse(null))
            .withItalic(style.italic().orElse(null))
            .withUnderlined(style.underlined().orElse(null))
            .withStrikethrough(style.strikethrough().orElse(null))
            .withObfuscated(style.obfuscated().orElse(null));
    }

    public static Component asComponent(String string) {
        if (string == null) return null;
        return Component.literal(string);
    }
}
