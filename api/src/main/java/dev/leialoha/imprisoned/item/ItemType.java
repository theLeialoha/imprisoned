package dev.leialoha.imprisoned.item;

import com.mojang.serialization.Codec;

public enum ItemType {

    ITEM,
    TOOL,
    COLLECTABLE;

    public static final Codec<ItemType> CODEC;

    static {
        CODEC = Codec.STRING.xmap(s -> ItemType.valueOf(s.toUpperCase()), ItemType::name);
    }

}
