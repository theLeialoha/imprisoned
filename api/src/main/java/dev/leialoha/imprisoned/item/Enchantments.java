package dev.leialoha.imprisoned.item;

import dev.leialoha.imprisoned.data.ResourceKey;

public final class Enchantments {
    
    private static final String NAMESPACE = "imprisoned";

    public static final ResourceKey TELEKINESIS;



    static {
        TELEKINESIS = ResourceKey.fromNamespaceAndPath(NAMESPACE, "telekinesis");
    }

}
