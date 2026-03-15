package dev.leialoha.imprisoned.item;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.leialoha.imprisoned.item.consumable.ConsumableItem;
import dev.leialoha.imprisoned.item.tool.ToolItem;

public abstract class Item {
    
    public static final Codec<Item> CODEC;

    public ItemDisplay display;
    public ItemPrices prices;
    public ItemFlags flags;

    protected Item(
        ItemDisplay display,
        ItemPrices prices,
        ItemFlags flags
    ) {
        this.display = display;
        this.prices = prices;
        this.flags = flags;
    }

    public abstract ItemType getType();
    private final String getTypeStr() {
        return this.getType().toString();
    }

    public ItemDisplay getDisplay() {
        return this.display;
    }

    public ItemPrices getPrices() {
        return this.prices;
    }

    public ItemFlags getFlags() {
        return this.flags;
    }

    public int getMaxStackSize() {
        return 1;
    }

    private static MapCodec<? extends Item> codecFromType(String type) {
        ItemType iType = ItemType.valueOf(type.toUpperCase());
        return switch (iType) {
            case CONSUMABLE -> ConsumableItem.CODEC;
            case TOOL -> ToolItem.CODEC;
            default -> GenericItem.CODEC;
        };
    }

    protected static <T extends Item> Products.P3<RecordCodecBuilder.Mu<T>,ItemDisplay,ItemPrices,ItemFlags> baseFields(
        RecordCodecBuilder.Instance<T> instance
    ) {
        return instance.group(
            ItemDisplay.CODEC.fieldOf("display").forGetter(Item::getDisplay),
            ItemPrices.CODEC.fieldOf("prices").forGetter(Item::getPrices),
            ItemFlags.CODEC.fieldOf("flags").forGetter(Item::getFlags)
        );
    }

    static {
        CODEC = Codec.STRING.dispatch(Item::getTypeStr, Item::codecFromType);
    }

}
