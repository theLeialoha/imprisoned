package dev.leialoha.imprisoned.registration;

import dev.leialoha.imprisoned.block.Block;
import dev.leialoha.imprisoned.drop.DropTable;
import dev.leialoha.imprisoned.item.Item;

public class RegistryKeys {

    public static final RegistryKey<Block> BLOCKS;
    public static final RegistryKey<Item> ITEMS;
    public static final RegistryKey<DropTable> DROPS;



    static {
        BLOCKS = new RegistryKey<>("blocks");
        ITEMS = new RegistryKey<>("items");
        DROPS = new RegistryKey<>("drops");
    }

}
