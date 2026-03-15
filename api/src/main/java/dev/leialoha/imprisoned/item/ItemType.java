package dev.leialoha.imprisoned.item;

import com.mojang.serialization.Codec;

public enum ItemType {

    ITEM,
    TOOL,
    CONSUMABLE,
    COLLECTABLE;

    public static final Codec<ItemType> CODEC;

    protected static ItemType fromString(String string) {
        ItemType type = ItemType.valueOf(string.toUpperCase());
        return (type == null) ? ITEM : type;
    }

    static {
        CODEC = Codec.STRING.xmap(ItemType::fromString, ItemType::name);
    }

}
