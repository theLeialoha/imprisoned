package dev.leialoha.imprisoned.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class GenericItem extends Item {

    public static final MapCodec<GenericItem> CODEC;

    private final ItemType type;

    private GenericItem(
        ItemDisplay display,
        ItemPrices prices,
        ItemFlags flags,
        ItemType type
    ) {
        super(display, prices, flags);
        this.type = type;
    }

    @Override
    public ItemType getType() {
        return type;
    }

    public boolean isArtifact() {
        return this.type.equals(ItemType.COLLECTABLE);
    }

    @Override
    public int getMaxStackSize() {
        return this.isArtifact() ? 1
            : super.getMaxStackSize();
    }

    static {
        CODEC = RecordCodecBuilder.mapCodec(instance ->
            baseFields(instance).and(
                ItemType.CODEC.fieldOf("type").forGetter(GenericItem::getType)
            ).apply(instance, GenericItem::new)
        );
    }

}
