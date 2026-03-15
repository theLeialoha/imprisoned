package dev.leialoha.imprisoned.item;

import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.leialoha.imprisoned.data.ResourceKey;
import dev.leialoha.imprisoned.text.StyledLore;

public record ItemDisplay(
    String name,
    Rarity rarity,
    List<StyledLore> lore,
    ResourceKey modelId,
    Optional<String> head,
    Optional<Integer> customModelData
) {

    public static final Codec<ItemDisplay> CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("name").forGetter(ItemDisplay::name),
                Rarity.CODEC.fieldOf("rarity").forGetter(ItemDisplay::rarity),
                StyledLore.CODEC.listOf().fieldOf("lore").forGetter(ItemDisplay::lore),
                ResourceKey.CODEC.fieldOf("model_id").forGetter(ItemDisplay::modelId),
                Codec.STRING.optionalFieldOf("head").forGetter(ItemDisplay::head),
                Codec.INT.optionalFieldOf("custom_model_data").forGetter(ItemDisplay::customModelData)
            ).apply(instance, ItemDisplay::new)
        );
    }

}
