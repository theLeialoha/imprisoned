package dev.leialoha.imprisoned.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Multimaps;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;

import dev.leialoha.imprisoned.data.ResourceKey;
import dev.leialoha.imprisoned.item.Item;
import dev.leialoha.imprisoned.item.ItemDisplay;
import dev.leialoha.imprisoned.item.ItemHolder;
import dev.leialoha.imprisoned.item.Rarity;
import dev.leialoha.imprisoned.registration.Registry;
import dev.leialoha.imprisoned.registration.RegistryKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;

public class ItemBuilder<R> {

    static final Registry<Item> REGISTRY = RegistryKeys.ITEMS.getRegistry();

    protected ItemBuilder<R> withItem(Item item) { return this; }

    protected ItemBuilder<R> withOwner(UUID owner) { return this; };
    protected ItemBuilder<R> withUniqueIdentifier(UUID identifier) { return this; };
    protected <T> ItemBuilder<R> withEnchantments(T enchantments) { return this; };

    protected ItemBuilder<R> withName(String name) { return this; }
    protected ItemBuilder<R> withRarity(Rarity rarity) { return this; }

    public R build() { return null; }

    public static class ItemHolderBuilder extends ItemBuilder<ItemHolder> {
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

    public static class ItemStackBuilder extends ItemBuilder<ItemStack> {
        private static final ItemBuilder<ItemStack> EMPTY = new ItemBuilder<ItemStack>();

        private CraftItemStack stack;
        private CompoundTag data;

        private String displayName;
        private Rarity rarity;

        private ItemStackBuilder() {
            stack = CraftItemStack.asCraftCopy(
                new ItemStack(Material.GHAST_TEAR)
            );
            data = new CompoundTag();
        }

        @Override
        protected ItemStackBuilder withItem(Item item) {
            ItemDisplay display = item.getDisplay();
            ResourceKey key = REGISTRY.getKey(item);

            data.putString("item", key.toString());

            return this
                .withName(item.name)
                .withRarity(item.rarity)
                .withLore(item.lore)
                .withModel(display.id())
                .withHeadProfile(display.head())
                .withCustomModelData(display.customModelData());
        }

        @Override
        protected ItemStackBuilder withName(String name) {
            if (name != null) {
                this.displayName = name;
                data.putString("name", name);
            }

            return this;
        }

        @Override
        protected ItemStackBuilder withOwner(UUID owner) {
            if (owner != null) data.putString("owner", owner.toString());
            return this;
        }

        @Override
        protected ItemStackBuilder withUniqueIdentifier(UUID identifier) {
            if (identifier != null) data.putString("identifier", identifier.toString());
            return this;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected <T> ItemStackBuilder withEnchantments(T enchantments) {
            CompoundTag tag = new CompoundTag();
            
            if (enchantments instanceof Map map) {
                map.forEach((key, value) -> {
                    if (!(key instanceof ResourceKey enchantment)) return;
                    if (!(value instanceof Integer level)) return;
                    tag.putInt(enchantment.toString(), level);
                });
            }

            data.put("enchantment", tag);
    
            return this;
        }

        protected ItemStackBuilder withLore(List<String> loreArr) {
            final MutableComponent PREFIX = Component.empty()
                .withStyle(Style.EMPTY.withItalic(false))
                .withColor(ChatFormatting.GRAY.getColor())
                .append(Component.literal(" › ")
                    .withColor(ChatFormatting.DARK_GRAY.getColor())
                );

            final List<Component> lore = loreArr.stream()
                .map(MinecraftUtils.splitString(200))
                .flatMap(Collection::stream)
                .map(BukkitConversion::asComponent)
                .map(c -> c == null ? Component.empty() : PREFIX.copy().append(c))
                .collect(Collectors.toCollection(ArrayList::new));

            lore.addAll(List.of(
                // empty line
                Component.empty(),
                // line with rarity
                Component.literal(rarity.name().toUpperCase())
                    .append(" ITEM")
                    .withStyle(Style.EMPTY
                    .withBold(true)
                    .withItalic(false)
                    .withColor(rarity.getColor())
                )
            ));

            this.stack.handle.set(DataComponents.LORE, new ItemLore(lore));

            return this;
        }

        @Override
        protected ItemStackBuilder withRarity(Rarity rarity) {
            if (rarity != null) {
                this.rarity = rarity;
                data.putString("rarity", rarity.toString());
            }

            return this;
        }

        protected ItemStackBuilder withModel(ResourceKey key) {
            Identifier identifier = BukkitConversion.asIdentifier(key);
            this.stack.handle.set(DataComponents.ITEM_MODEL, identifier);
            return this;
        }

        protected ItemStackBuilder withHeadProfile(Optional<String> profile) {
            ResolvableProfile resolvedProfile = profile.map(value -> {
                return new ResolvableProfile.Static(
                    Either.right(
                        new ResolvableProfile.Partial(
                            Optional.empty(), Optional.empty(),
                            new PropertyMap(
                                Multimaps.forMap(
                                    Map.of("textures", new Property("textures", value))
                                )
                            )
                        )
                    ), PlayerSkin.Patch.EMPTY
                );
            }).orElse(ResolvableProfile.Static.EMPTY);

            this.stack.handle.set(DataComponents.PROFILE, resolvedProfile);
            return this;
        }

        protected ItemStackBuilder withCustomModelData(Optional<Integer> modelData) {
            CustomModelData customModelData = modelData.map(value -> {
                return new CustomModelData(List.of(), List.of(), List.of(), List.of(value));
            }).orElse(CustomModelData.EMPTY);

            this.stack.handle.set(DataComponents.CUSTOM_MODEL_DATA, customModelData);
            return this;
        }

        public static ItemBuilder<ItemStack> from(ItemHolder holder) {
            if (holder == null) return EMPTY;

            return new ItemStackBuilder()
                .withItem(holder.item)
                .withOwner(holder.owner)
                .withUniqueIdentifier(holder.identifier)
                .withEnchantments(holder.enchantments);
        }

        @Override
        public ItemStack build() {
            this.stack.handle.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
            this.stack.handle.set(DataComponents.MAX_STACK_SIZE, 1);

            final Component CUSTOM_NAME = Component.empty()
                .withStyle(Style.EMPTY.withItalic(false))
                .append(Component.literal(displayName).withColor(rarity.getColor()));
            
            this.stack.handle.set(DataComponents.CUSTOM_NAME, CUSTOM_NAME);

            return this.stack;
        }

    }

}
