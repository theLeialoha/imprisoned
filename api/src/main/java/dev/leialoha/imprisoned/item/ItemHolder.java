package dev.leialoha.imprisoned.item;

import java.util.Map;
import java.util.UUID;

import dev.leialoha.imprisoned.data.ResourceKey;

public class ItemHolder {
    
    public Item item;
    public UUID owner;
    public UUID identifier;
    public Map<ResourceKey, Integer> enchantments;

    public ItemHolder(
        Item item,
        UUID owner,
        UUID identifier,
        Map<ResourceKey, Integer> enchantments
    ) {
        this.item = item;
        this.owner = owner;
        this.identifier = identifier;
        this.enchantments  = enchantments;
    }

    public ItemHolder(Item item) {
        this.item = item;
    }

    public boolean hasEnchantment(ResourceKey enchantment) {
        return enchantments.containsKey(enchantment)
            && enchantments.get(enchantment) > 0;
    }

}
