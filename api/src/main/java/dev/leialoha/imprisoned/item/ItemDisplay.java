package dev.leialoha.imprisoned.item;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.leialoha.imprisoned.data.ResourceKey;

public record ItemDisplay(
    ResourceKey id,
    Optional<String> head,
    Optional<Integer> customModelData
) {

    public static final Codec<ItemDisplay> CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                ResourceKey.CODEC.fieldOf("id").forGetter(ItemDisplay::id),
                Codec.STRING.optionalFieldOf("head").forGetter(ItemDisplay::head),
                Codec.INT.optionalFieldOf("custom_model_data").forGetter(ItemDisplay::customModelData)
            ).apply(instance, ItemDisplay::new)
        );
    }

}
