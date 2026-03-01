package dev.leialoha.imprisoned.block;

import java.util.function.Consumer;

import org.bukkit.Material;

import dev.leialoha.imprisoned.registration.RegistrationProvider;
import dev.leialoha.imprisoned.registration.RegistryEntry;
import dev.leialoha.imprisoned.registration.RegistryKeys;
import dev.leialoha.imprisoned.data.ResourceKey;
import dev.leialoha.imprisoned.utils.BukkitConversion;

public class Blocks {
    
    private static final RegistrationProvider<Block> PROVIDER = RegistrationProvider.of(RegistryKeys.BLOCKS, "imprisoned");

    public static final RegistryEntry<Block> DIRT = register(Material.DIRT)
        .with(maxHealth(100));
    public static final RegistryEntry<Block> STONE = register(Material.STONE)
        .with(maxHealth(100));
    public static final RegistryEntry<Block> COBBLESTONE = register(Material.COBBLESTONE)
        .with(maxHealth(100));
    public static final RegistryEntry<Block> ANDESITE = register(Material.ANDESITE)
        .with(maxHealth(100));
    public static final RegistryEntry<Block> IRON_ORE = register(Material.IRON_ORE)
        .with(maxHealth(100));






    private static RegistryEntry<Block> register(Material material) {
        ResourceKey key = BukkitConversion.from(material.getKey());
        BlockData data = new BlockData(key);

        String safe = key.toShortString().replace(':', '.');
        return register(safe, new Block(data));
    }

    private static RegistryEntry<Block> register(String name, Block entry) {
        return PROVIDER.register(name, entry);
    }

    private static Consumer<Block> maxHealth(int maxHealth) {
        return (block) -> block.setMaxHealth(maxHealth);
    }


    // I just need to load the class
    public static void init() {

    }
    
}
