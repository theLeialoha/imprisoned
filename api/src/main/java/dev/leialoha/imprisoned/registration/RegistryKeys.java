package dev.leialoha.imprisoned.registration;

import dev.leialoha.imprisoned.block.Block;

public class RegistryKeys {

    public static final RegistryKey<Block> BLOCKS;



    static {
        BLOCKS = new RegistryKey<>("blocks");
    }

}
