package dev.leialoha.imprisoned.catalog;

import dev.leialoha.imprisoned.block.Block;
import dev.leialoha.imprisoned.registration.RegistrationProvider;
import dev.leialoha.imprisoned.registration.RegistryKeys;

public class Blocks extends Catalog {

    private static final RegistrationProvider<Block> PROVIDER = RegistrationProvider.of(RegistryKeys.BLOCKS, "imprisoned");


    // public static final RegistryEntry<Block> DIRT = register(Material.DIRT)
    //     .with(maxHealth(100));
    // public static final RegistryEntry<Block> STONE = register(Material.STONE)
    //     .with(maxHealth(100));
    // public static final RegistryEntry<Block> COBBLESTONE = register(Material.COBBLESTONE)
    //     .with(maxHealth(100));
    // public static final RegistryEntry<Block> ANDESITE = register(Material.ANDESITE)
    //     .with(maxHealth(100));
    // public static final RegistryEntry<Block> IRON_ORE = register(Material.IRON_ORE)
    //     .with(maxHealth(100));






    // private static RegistryEntry<Block> register(Material material) {
    //     ResourceKey key = BukkitConversion.from(material.getKey());
    //     BlockData data = new BlockData(key);

    //     String safe = key.toShortString().replace(':', '.');
    //     return register(safe, new Block(data));
    // }

    // private static RegistryEntry<Block> register(String name, Block entry) {
    //     return PROVIDER.register(name, entry);
    // }

    // private static Consumer<Block> maxHealth(int maxHealth) {
    //     return (block) -> block.setMaxHealth(maxHealth);
    // }


    public static void init() {
        register(PROVIDER, Block.CODEC, "blocks");
    }

}
