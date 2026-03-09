package dev.leialoha.imprisoned.item;

import com.mojang.serialization.Codec;

public enum Rarity {

    COMMON(0xd6d5d6),
    UNCOMMON(0x14d463),
    UNIQUE(0x53d2ff),
    RARE(0xff63ff),
    ELEGANT(0xf3d323),
    DIVINE(0xf37373);

    public static final Codec<Rarity> CODEC;

    final int color;

    Rarity(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
    
    static {
        CODEC = Codec.STRING.xmap(s -> Rarity.valueOf(s.toUpperCase()), Rarity::name);
    }

}
