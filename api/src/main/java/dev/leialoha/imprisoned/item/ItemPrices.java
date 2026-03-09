package dev.leialoha.imprisoned.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ItemPrices(
    int buy,
    int sell
) {

    public static final Codec<ItemPrices> CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("buy", 0).forGetter(ItemPrices::buy),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("sell", 0).forGetter(ItemPrices::sell)
            ).apply(instance, ItemPrices::new)
        );
    }

}
