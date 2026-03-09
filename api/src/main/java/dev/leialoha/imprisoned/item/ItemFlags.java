package dev.leialoha.imprisoned.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ItemFlags(
    boolean tradable,
    boolean soulbound
) {

    public static final Codec<ItemFlags> CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.BOOL.optionalFieldOf("tradable", false).forGetter(ItemFlags::tradable),
                Codec.BOOL.optionalFieldOf("soulbound", false).forGetter(ItemFlags::soulbound)
            ).apply(instance, ItemFlags::new)
        );
    }

}
