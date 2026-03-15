package dev.leialoha.imprisoned.utils.builders.item.holder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import dev.leialoha.imprisoned.data.ResourceKey;
import dev.leialoha.imprisoned.item.Item;
import dev.leialoha.imprisoned.item.ItemHolder;
import dev.leialoha.imprisoned.item.Rarity;
import dev.leialoha.imprisoned.utils.builders.item.ItemBuilder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;

public class ItemHolderBuilder extends ItemBuilder<ItemHolder> {
    private static final ItemBuilder<ItemHolder> EMPTY = new ItemBuilder<ItemHolder>();

    private ItemHolder holder;

    private ItemHolderBuilder() {
        holder = new ItemHolder(null);
    }

    @Override
    protected ItemHolderBuilder withItem(Item item) {
        if (item != null) holder.item = item;
        return this;
    }

    @Override
    protected ItemHolderBuilder withOwner(UUID owner) {
        if (owner != null) holder.owner = owner;
        return this;
    }

    @Override
    protected ItemHolderBuilder withUniqueIdentifier(UUID identifier) {
        if (identifier != null) holder.identifier = identifier;
        return this;
    }

    @Override
    protected <T> ItemHolderBuilder withEnchantments(T enchantments) {
        Map<ResourceKey, Integer> map = new HashMap<>();

        if (enchantments instanceof CompoundTag tag) {
            tag.forEach((key, value) -> {
                ResourceKey enchantment = ResourceKey.tryParse(key);
                Integer level = value.asInt().orElse(0);
                if (level > 0) map.put(enchantment, level);
            });
        }

        holder.enchantments = map;

        return this;
    }

    @Override
    protected ItemHolderBuilder withRarity(Rarity rarity) {
        if (rarity != null) holder.rarity = rarity;
        return this;
    }

    @Override
    protected ItemHolderBuilder withName(String name) {
        if (name != null) holder.name = name;
        return this;
    }

    public static ItemBuilder<ItemHolder> from(ItemStack stack) {
        if (stack == null) return EMPTY;
        if (((CraftItemStack) stack).handle == null) return EMPTY;
        
        final DataComponentMap components = ((CraftItemStack) stack).handle.getComponents();
        CustomData data = components.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = data.copyTag();

        Item item = tag.getString("item")
            .map(ResourceKey::tryParse)
            .map(REGISTRY::get)
            .orElse(null);

        UUID owner = tag.getString("owner")
            .map(UUID::fromString)
            .orElse(null);

        UUID identifier = tag.getString("identifier")
            .map(UUID::fromString)
            .orElse(null);

        Tag enchantments = tag.get("enchantments");

        String name = tag.getString("name").orElse(null);

        Rarity rarity = tag.getString("rarity")
            .map(String::toUpperCase)
            .map(Rarity::valueOf)
            .orElse(null);

        return new ItemHolderBuilder()
            .withItem(item)
            .withRarity(rarity)
            .withOwner(owner)
            .withUniqueIdentifier(identifier)
            .withEnchantments(enchantments)
            .withName(name);
    }

    @Override
    public ItemHolder build() {
        return this.holder;
    }

}
