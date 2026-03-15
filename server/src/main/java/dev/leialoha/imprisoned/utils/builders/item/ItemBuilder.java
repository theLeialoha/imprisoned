package dev.leialoha.imprisoned.utils.builders.item;

import java.util.UUID;

import dev.leialoha.imprisoned.item.Item;
import dev.leialoha.imprisoned.item.Rarity;
import dev.leialoha.imprisoned.registration.Registry;
import dev.leialoha.imprisoned.registration.RegistryKeys;

public class ItemBuilder<R> {

    protected static final Registry<Item> REGISTRY = RegistryKeys.ITEMS.getRegistry();

    protected ItemBuilder<R> withItem(Item item) { return this; }

    protected ItemBuilder<R> withOwner(UUID owner) { return this; };
    protected ItemBuilder<R> withUniqueIdentifier(UUID identifier) { return this; };
    protected <T> ItemBuilder<R> withEnchantments(T enchantments) { return this; };

    protected ItemBuilder<R> withName(String name) { return this; }
    protected ItemBuilder<R> withRarity(Rarity rarity) { return this; }

    public R build() { return null; }

}
