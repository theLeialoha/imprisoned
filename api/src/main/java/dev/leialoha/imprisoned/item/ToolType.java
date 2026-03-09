package dev.leialoha.imprisoned.item;

import com.mojang.serialization.Codec;

public enum ToolType {
    
    NONE,
    PICKAXE;
    
    public static final Codec<ToolType> CODEC;

    static {
        CODEC = Codec.STRING.xmap(s -> ToolType.valueOf(s.toUpperCase()), ToolType::name);
    }

}
