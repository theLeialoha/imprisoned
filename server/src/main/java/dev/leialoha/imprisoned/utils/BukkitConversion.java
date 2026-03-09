package dev.leialoha.imprisoned.utils;

import dev.leialoha.imprisoned.data.IntLocation;
import dev.leialoha.imprisoned.data.ResourceKey;
import dev.leialoha.imprisoned.item.Item;
import dev.leialoha.imprisoned.item.ItemAttributes;
import dev.leialoha.imprisoned.item.ItemHolder;
import dev.leialoha.imprisoned.registration.Registry;
import dev.leialoha.imprisoned.registration.RegistryKeys;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Multimaps;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;

public final class BukkitConversion {

    private BukkitConversion() {
        throw new IllegalAccessError("Class doesn't need to be initalized");
    }

    public static ResourceKey asResourceKey(NamespacedKey key) {
        return ResourceKey.fromNamespaceAndPath(key.namespace(), key.value());
    }

    public static NamespacedKey asNamepacedKey(ResourceKey key) {
        return new NamespacedKey(key.getNamespace(), key.getPath());
    }

    public static Identifier asIdentifier(ResourceKey key) {
        return Identifier.fromNamespaceAndPath(key.getNamespace(), key.getPath());
    }

    public static IntLocation asIntLocation(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        World world = location.getWorld();
        ResourceKey key = asResourceKey(world.getKey());

        return new IntLocation(x, y, z, key);
    }

    public static Location asLocation(IntLocation location) {
        int x = location.x();
        int y = location.y();
        int z = location.z();

        ResourceKey key = location.world();
        NamespacedKey namespacedKey = asNamepacedKey(key);
        World world = Bukkit.getWorld(namespacedKey);

        return new Location(world, x, y, z);
    }

    public static ItemHolder asItemHolder(ItemStack stack) {
        if (stack == null) return null;

        final Registry<Item> ITEM_REGISTRY = RegistryKeys.ITEMS.getRegistry();

        net.minecraft.world.item.ItemStack nmsStack = ((CraftItemStack) stack).handle;
        if (nmsStack == null) return null;

        DataComponentMap components = nmsStack.getComponents();
        CustomData data = components.get(DataComponents.CUSTOM_DATA);

        CompoundTag tag = data.copyTag();

        Item item = tag.getString("item")
            .map(ResourceKey::tryParse)
            .map(ITEM_REGISTRY::get)
            .orElse(null);

        UUID owner = tag.getString("owner")
            .map(UUID::fromString)
            .orElse(null);

        UUID identifier = tag.getString("identifier")
            .map(UUID::fromString)
            .orElse(null);

        Map<ResourceKey, Integer> enchantments = tag.getCompoundOrEmpty("enchantments")
            .entrySet().stream()
            .map(entry -> Pair.of(
                ResourceKey.tryParse(entry.getKey()),
                entry.getValue().asInt().orElse(0)
            )).filter(p -> p.getFirst() != null)
            .collect(Pair.toMap());


        if (item == null) return null;
        return new ItemHolder(item, owner, identifier, enchantments);
    }

    public static ItemStack asItemStack(ItemHolder holder) {
        if (holder == null) return null;
        
        final Item ITEM = holder.item;
        final ItemAttributes ATTRIBUTES = ITEM.attributes;

        final Identifier ITEM_MODEL = asIdentifier(ATTRIBUTES.id());
        final ResolvableProfile PROFILE = makeProfile(ATTRIBUTES.head());
        final CustomModelData CUSTOM_MODEL_DATA = makeCustomModelData(ATTRIBUTES.customModelData());
        final Component CUSTOM_NAME = Component.literal(ITEM.name).withColor(ITEM.rarity.getColor());

        final Registry<Item> ITEM_REGISTRY = RegistryKeys.ITEMS.getRegistry();
        ResourceKey key = ITEM_REGISTRY.getKey(ITEM);
        if (key == null) return null;
        
        CompoundTag tag = new CompoundTag();

        tag.putString("item", key.toString());
        if (holder.owner != null)
            tag.putString("owner", holder.owner.toString());
        if (holder.identifier != null)
            tag.putString("identifier", holder.identifier.toString());
        if (holder.enchantments != null)
            tag.put("enchantments", asTag(holder.enchantments));

        CustomData data = CustomData.of(tag);
        
        DataComponentPatch components = DataComponentPatch.builder()
            .set(DataComponents.CUSTOM_DATA, data)
            .set(DataComponents.ITEM_MODEL, ITEM_MODEL)
            .set(DataComponents.MAX_STACK_SIZE, 1)
            .set(DataComponents.PROFILE, PROFILE)
            .set(DataComponents.CUSTOM_MODEL_DATA, CUSTOM_MODEL_DATA)
            .set(DataComponents.CUSTOM_NAME, CUSTOM_NAME)
            .build();

        Holder<net.minecraft.world.item.Item> itemHolder = Holder.direct(Items.GHAST_TEAR);
        net.minecraft.world.item.ItemStack nmsStack = new net.minecraft.world.item.ItemStack(itemHolder, 1, components);

        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    private static CustomModelData makeCustomModelData(Optional<Integer> optional) {
        return optional.map(value -> {
            return new CustomModelData(List.of(), List.of(), List.of(), List.of(value));
        }).orElse(CustomModelData.EMPTY);
    }

    private static ResolvableProfile makeProfile(Optional<String> optional) {
        return optional.map(value -> {
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
    }

    private static Tag asTag(Map<ResourceKey, Integer> map) {
        CompoundTag tag = new CompoundTag();
        map.forEach((key, value) -> tag.putInt(key.toString(), value));
        return tag;
    }

}
