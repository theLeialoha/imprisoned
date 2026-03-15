package dev.leialoha.imprisoned.item.consumable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.leialoha.imprisoned.item.Item;
import dev.leialoha.imprisoned.item.ItemDisplay;
import dev.leialoha.imprisoned.item.ItemFlags;
import dev.leialoha.imprisoned.item.ItemPrices;
import dev.leialoha.imprisoned.item.ItemType;

public class ConsumableItem extends Item {

    public static final MapCodec<ConsumableItem> CODEC;
    private ConsumableAttributes attributes;

    private ConsumableItem(
        ItemDisplay display,
        ItemPrices prices,
        ItemFlags flags,
        ConsumableAttributes attributes
    ) {
        super(display, prices, flags);
        this.attributes = attributes;
    }

    @Override
    public ItemType getType() {
        return ItemType.CONSUMABLE;
    }

    public ConsumableAttributes getConsumableAttributes() {
        return attributes;
    }

    static {
        CODEC = RecordCodecBuilder.mapCodec(instance ->
            baseFields(instance).and(
                ConsumableAttributes.CODEC.optionalFieldOf("consumable", ConsumableAttributes.EMPTY)
                    .forGetter(ConsumableItem::getConsumableAttributes)
            ).apply(instance, ConsumableItem::new)
        );
    }

}
