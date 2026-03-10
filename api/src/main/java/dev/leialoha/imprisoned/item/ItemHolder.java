package dev.leialoha.imprisoned.item;

import java.util.Map;
import java.util.UUID;

import dev.leialoha.imprisoned.data.ResourceKey;

public class ItemHolder {
    
    public Item item;

    public String name;
    public Rarity rarity;

    public UUID owner;
    public UUID identifier;
    public Map<ResourceKey, Integer> enchantments;

    public ItemHolder() {}

    public ItemHolder(Item item) {
        this.item = item;
    }

    public boolean hasEnchantment(ResourceKey enchantment) {
        return enchantments.containsKey(enchantment)
            && enchantments.get(enchantment) > 0;
    }

}
