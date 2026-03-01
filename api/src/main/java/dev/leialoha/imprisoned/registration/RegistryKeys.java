package dev.leialoha.imprisoned.registration;

import dev.leialoha.imprisoned.block.Block;
import dev.leialoha.imprisoned.job.Tickable;

public class RegistryKeys {

    public static final RegistryKey<Block> BLOCKS;
    public static final RegistryKey<Tickable> TICKABLES;



    static {
        BLOCKS = new RegistryKey<>("blocks");
        TICKABLES = new RegistryKey<>("tickables");
    }

}
