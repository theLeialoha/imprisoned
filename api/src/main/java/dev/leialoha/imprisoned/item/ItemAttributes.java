package dev.leialoha.imprisoned.item;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.leialoha.imprisoned.data.ResourceKey;

public record ItemAttributes(
    ResourceKey id,
    Optional<String> head,
    Optional<Integer> customModelData
) {

    public static final Codec<ItemAttributes> CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                ResourceKey.CODEC.fieldOf("id").forGetter(ItemAttributes::id),
                Codec.STRING.optionalFieldOf("head").forGetter(ItemAttributes::head),
                Codec.INT.optionalFieldOf("custom_model_data").forGetter(ItemAttributes::customModelData)
            ).apply(instance, ItemAttributes::new)
        );
    }

}
