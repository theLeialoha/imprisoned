package dev.leialoha.imprisoned.utils.builders.item.stack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Multimaps;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;

import dev.leialoha.imprisoned.data.ResourceKey;
import dev.leialoha.imprisoned.item.Item;
import dev.leialoha.imprisoned.item.ItemHolder;
import dev.leialoha.imprisoned.item.Rarity;
import dev.leialoha.imprisoned.registration.codec.CompoundKeys;
import dev.leialoha.imprisoned.text.StyledLore;
import dev.leialoha.imprisoned.utils.BukkitConversion;
import dev.leialoha.imprisoned.utils.MinecraftUtils;
import dev.leialoha.imprisoned.utils.builders.item.ItemBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;

public class ItemStackBuilder extends ItemBuilder<ItemStack> {
    private static final ItemBuilder<ItemStack> EMPTY = new EmptyItemBuilder<>();

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
        ResourceKey key = REGISTRY.getKey(item);

        data.putString("item", key.toString());
        data.putString("type", item.get(CompoundKeys.ITEM_TYPE).toString());

        return this
            .withName(item.get(CompoundKeys.ITEM_NAME))
            .withRarity(item.get(CompoundKeys.ITEM_RARITY))
            .withLore(item.get(CompoundKeys.ITEM_LORE))
            .withModel(item.get(CompoundKeys.ITEM_MODEL_ID))
            .withHeadProfile(item.get(CompoundKeys.ITEM_PLAYER_HEAD))
            .withCustomModelData(item.get(CompoundKeys.ITEM_CUSTOM_MODEL_DATA));
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

        if (!tag.isEmpty())
            data.put("enchantment", tag);

        return this;
    }

    protected ItemStackBuilder withLore(StyledLore[] loreArr) {
        final List<Component> lore = Stream.of(loreArr)
            .map(MinecraftUtils.splitLore(250))
            .flatMap(Collection::stream)
            .map(BukkitConversion::asComponent)
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
        if (holder.item == null) return EMPTY;

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
