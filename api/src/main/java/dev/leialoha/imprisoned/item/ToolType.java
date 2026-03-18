package dev.leialoha.imprisoned.item;

import com.mojang.serialization.Codec;

public enum ToolType {
    
    NONE,
    PICKAXE;
    
    public static final Codec<ToolType> CODEC;

    protected static ToolType fromString(String string) {
        ToolType type = ToolType.valueOf(string.toUpperCase());
        return (type == null) ? NONE : type;
    }

    static {
        CODEC = Codec.STRING.xmap(ToolType::fromString, ToolType::name);
    }

}
