package dev.leialoha.imprisoned.item.consumable;

import com.mojang.serialization.Codec;

public enum ConsumableType {
    
    FOOD,
    DRINK,
    OTHER;
    
    public static final Codec<ConsumableType> CODEC;

    protected static ConsumableType fromString(String string) {
        ConsumableType type = ConsumableType.valueOf(string.toUpperCase());
        return (type == null) ? OTHER : type;
    }

    static {
        CODEC = Codec.STRING.xmap(ConsumableType::fromString, ConsumableType::name);
    }

}
