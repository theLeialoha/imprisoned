package dev.leialoha.imprisoned.drop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.leialoha.imprisoned.data.ResourceKey;

public class DropEntry {
    
    public static final Codec<DropEntry> CODEC;

    public ResourceKey item;
    public float chance;

    public DropEntry(ResourceKey item, float chance) {
        this.item = item;
        this.chance = chance;
    }

    public ResourceKey getItem() {
        return item;
    }

    public float getChance() {
        return chance;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                ResourceKey.CODEC.fieldOf("item").forGetter(DropEntry::getItem),
                Codec.floatRange(0, 1).optionalFieldOf("chance", 1f).forGetter(DropEntry::getChance)
            ).apply(instance, DropEntry::new)
        );
    }

}
