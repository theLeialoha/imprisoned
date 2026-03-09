package dev.leialoha.imprisoned.catalog;

import dev.leialoha.imprisoned.item.Item;
import dev.leialoha.imprisoned.registration.RegistrationProvider;
import dev.leialoha.imprisoned.registration.RegistryKeys;

public class Items extends Catalog {

    private static final RegistrationProvider<Item> PROVIDER = RegistrationProvider.of(RegistryKeys.ITEMS, "imprisoned");

    public static void init() {
        register(PROVIDER, Item.CODEC, "items");
    }
    
}
