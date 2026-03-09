package dev.leialoha.imprisoned.item;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Item {
    
    public static final Codec<Item> CODEC;

    public String name;
    public ItemType type;
    public Rarity rarity;
    public List<String> lore;
    public ItemAttributes attributes;
    public ItemPrices prices;
    public ItemFlags flags;

    public Item(
        String name,
        ItemType type,
        Rarity rarity,
        List<String> lore,
        ItemAttributes attributes,
        ItemPrices prices,
        ItemFlags flags
    ) {
        this.name = name;
        this.type = type;
        this.rarity = rarity;
        this.lore = lore;
        this.attributes = attributes;
        this.prices = prices;
        this.flags = flags;
    }

    public String getName() {
        return this.name;
    }

    public ItemType getType() {
        return this.type;
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public ItemAttributes getAttributes() {
        return this.attributes;
    }

    public ItemPrices getPrices() {
        return this.prices;
    }

    public ItemFlags getFlags() {
        return this.flags;
    }


    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("name").forGetter(Item::getName),
                ItemType.CODEC.fieldOf("type").forGetter(Item::getType),
                Rarity.CODEC.fieldOf("rarity").forGetter(Item::getRarity),
                Codec.STRING.listOf().fieldOf("lore").forGetter(Item::getLore),
                ItemAttributes.CODEC.fieldOf("item").forGetter(Item::getAttributes),
                ItemPrices.CODEC.fieldOf("prices").forGetter(Item::getPrices),
                ItemFlags.CODEC.fieldOf("flags").forGetter(Item::getFlags)
            ).apply(instance, Item::new)
        );
    }

}
