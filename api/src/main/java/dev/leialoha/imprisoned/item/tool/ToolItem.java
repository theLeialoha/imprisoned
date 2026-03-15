package dev.leialoha.imprisoned.item.tool;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.leialoha.imprisoned.item.Item;
import dev.leialoha.imprisoned.item.ItemDisplay;
import dev.leialoha.imprisoned.item.ItemFlags;
import dev.leialoha.imprisoned.item.ItemPrices;
import dev.leialoha.imprisoned.item.ItemType;

public class ToolItem extends Item {

    private ToolAttributes attributes;

    public static final MapCodec<ToolItem> CODEC;

    private ToolItem(
        ItemDisplay display,
        ItemPrices prices,
        ItemFlags flags,
        ToolAttributes attributes
    ) {
        super(display, prices, flags);
        this.attributes = attributes;
    }

    @Override
    public ItemType getType() {
        return ItemType.TOOL;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public ToolAttributes getToolAttributes() {
        return attributes;
    }

    static {
        CODEC = RecordCodecBuilder.mapCodec(instance ->
            baseFields(instance).and(
                ToolAttributes.CODEC.optionalFieldOf("tool", ToolAttributes.EMPTY).forGetter(ToolItem::getToolAttributes)
            ).apply(instance, ToolItem::new)
        );
    }

}
