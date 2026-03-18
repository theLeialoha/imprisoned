package dev.leialoha.imprisoned.utils.builders.item;

import java.util.UUID;

import dev.leialoha.imprisoned.item.Item;
import dev.leialoha.imprisoned.item.Rarity;
import dev.leialoha.imprisoned.registration.Registry;
import dev.leialoha.imprisoned.registration.RegistryKeys;

public abstract class ItemBuilder<R> {

    protected static final Registry<Item> REGISTRY = RegistryKeys.ITEMS.getRegistry();

    protected abstract ItemBuilder<R> withItem(Item item);

    protected abstract ItemBuilder<R> withOwner(UUID owner);
    protected abstract ItemBuilder<R> withUniqueIdentifier(UUID identifier);
    protected abstract <T> ItemBuilder<R> withEnchantments(T enchantments);

    protected abstract ItemBuilder<R> withName(String name);
    protected abstract ItemBuilder<R> withRarity(Rarity rarity);

    public abstract R build();

    protected static final class EmptyItemBuilder<R> extends ItemBuilder<R> {
        public EmptyItemBuilder() {}

        @Override protected ItemBuilder<R> withItem(Item item) { return this; }
        @Override protected ItemBuilder<R> withOwner(UUID owner) { return this; }
        @Override protected ItemBuilder<R> withUniqueIdentifier(UUID identifier) { return this; }
        @Override protected <T> ItemBuilder<R> withEnchantments(T enchantments) { return this; }
        @Override protected ItemBuilder<R> withName(String name) { return this; }
        @Override protected ItemBuilder<R> withRarity(Rarity rarity) { return this; }
        @Override public R build() { return null; }
    }


}
