package dev.leialoha.imprisoned.registration.codec;

import java.lang.reflect.Array;
import java.util.Arrays;

import com.mojang.serialization.Codec;

import dev.leialoha.imprisoned.data.ResourceKey;
import dev.leialoha.imprisoned.item.ConsumableType;
import dev.leialoha.imprisoned.item.ItemType;
import dev.leialoha.imprisoned.item.Rarity;
import dev.leialoha.imprisoned.item.ToolType;
import dev.leialoha.imprisoned.text.StyledLore;

@SuppressWarnings({ "unchecked" })
public final class CompoundKeys {
    
    public static final CompoundKeyGroup GENERIC_ITEM;
    public static final CompoundKeyGroup CONSUMABLE_ITEM;
    public static final CompoundKeyGroup TOOL_ITEM;

    // Base Item Components
    public static final RequiredCompoundKey<ItemType> ITEM_TYPE = of("type", "item_type", ItemType.class, ItemType.CODEC).asRequired();

    // Item Display Components
    public static final RequiredCompoundKey<String> ITEM_NAME = ofString("name", "display/name").asRequired();
    public static final RequiredCompoundKey<Rarity> ITEM_RARITY = of("rarity", "display/rarity", Rarity.class, Rarity.CODEC).asRequired();
    public static final RequiredCompoundKey<StyledLore[]> ITEM_LORE = ofArray("lore", "display/lore", StyledLore.class, StyledLore.CODEC).asRequired();
    public static final RequiredCompoundKey<ResourceKey> ITEM_MODEL_ID = of("model_id", "display/model_id", ResourceKey.class, ResourceKey.CODEC).asRequired();
    public static final CompoundKey<String> ITEM_PLAYER_HEAD = ofString("head", "display/head");
    public static final CompoundKey<Integer> ITEM_CUSTOM_MODEL_DATA = ofInt("custom_model_data", "display/custom_model_data");

    // Item Price Components
    public static final DefaultCompoundKey<Integer> ITEM_BUY_PRICE = ofInt("buy", "price/buy").withDefault(0);
    public static final DefaultCompoundKey<Integer> ITEM_SELL_PRICE = ofInt("sell", "price/sell").withDefault(0);

    // Item Flag Components
    public static final DefaultCompoundKey<Boolean> ITEM_TRADABLE_FLAG = ofBool("tradable", "flag/tradable").withDefault(false);
    public static final DefaultCompoundKey<Boolean> ITEM_SOULBOUND_FLAG = ofBool("soulbound", "flag/soulbound").withDefault(false);

    // Item Consumable Components
    public static final RequiredCompoundKey<Boolean> COMSUMABLE_EATABLE = ofBool("eatable", "comsumable/eatable").asRequired();
    public static final DefaultCompoundKey<ConsumableType> COMSUMABLE_TYPE = of("consumable_type", "comsumable/consumable_type", ConsumableType.class, ConsumableType.CODEC).withDefault(ConsumableType.OTHER);

    public static final RequiredCompoundKey<Integer> COMSUMABLE_FOOD = ofInt("food", "comsumable/stats/food").asRequired();
    public static final RequiredCompoundKey<Float> COMSUMABLE_SATURATION = ofFloat("saturation", "comsumable/stats/saturation").asRequired();
    public static final RequiredCompoundKey<String[]> COMSUMABLE_EFFECTS = ofArray("effects", "comsumable/stats/effects", String.class, Codec.STRING).asRequired();

    // Item Tool Components
    public static final RequiredCompoundKey<Integer> TOOL_LEVEL = ofInt("level", "tool/level").asRequired();
    public static final RequiredCompoundKey<Integer> TOOL_DURABLITY = ofInt("durablity", "tool/durablity").asRequired();
    public static final RequiredCompoundKey<Boolean> TOOL_BREAKABLE = ofBool("breakable", "tool/breakable").asRequired();
    public static final DefaultCompoundKey<ToolType> TOOL_TOOL_TYPE = of("tool_type", "tool/tool_type", ToolType.class, ToolType.CODEC).withDefault(ToolType.NONE);






    private static CompoundKey<String> ofString(String key, String string) {
        return of(key, string, String.class, Codec.STRING);
    }

    private static CompoundKey<Integer> ofInt(String key, String string) {
        return of(key, string, Integer.class, Codec.INT);
    }

    private static CompoundKey<Boolean> ofBool(String key, String string) {
        return of(key, string, Boolean.class, Codec.BOOL);
    }

    private static CompoundKey<Float> ofFloat(String key, String string) {
        return of(key, string, Float.class, Codec.FLOAT);
    }

    private static <T> CompoundKey<T[]> ofArray(String key, String string, Class<T> clazz, Codec<T> codec) {
        return of(key, string, classArray(clazz), arrayCodec(codec, clazz));
    }

    private static <T> CompoundKey<T> of(String key, String string, Class<T> clazz, Codec<T> codec) {
        ResourceKey resourceKey = ResourceKey.fromNamespaceAndPath("core", string);
        return new CompoundKey<>(key, resourceKey, clazz, codec);
    }

    private static <T> Codec<T[]> arrayCodec(Codec<T> codec, Class<T> clazz) {
        return codec.listOf().xmap(
            list -> list.toArray((T[]) Array.newInstance(clazz, list.size())),
            Arrays::asList
        );
    }

    private static <T> Class<T[]> classArray(Class<T> clazz) {
        return (Class<T[]>) Array.newInstance(clazz, 0).getClass();
    }

    static {
        // Generic attributes
        final CompoundKeyGroup ITEM_DISPLAY = new CompoundKeyGroup("display")
            .register(ITEM_NAME, ITEM_RARITY, ITEM_LORE, ITEM_MODEL_ID, ITEM_PLAYER_HEAD, ITEM_CUSTOM_MODEL_DATA);
        final CompoundKeyGroup ITEM_PRICES = new CompoundKeyGroup("prices")
            .register(ITEM_BUY_PRICE, ITEM_SELL_PRICE);
        final CompoundKeyGroup ITEM_FLAGS = new CompoundKeyGroup("flags")
            .register(ITEM_TRADABLE_FLAG, ITEM_SOULBOUND_FLAG);

        // Comsumable attributes
        final CompoundKeyGroup COMSUMABLE_STATS = new CompoundKeyGroup("stats")
            .register(COMSUMABLE_FOOD, COMSUMABLE_SATURATION, COMSUMABLE_EFFECTS);
        final CompoundKeyGroup CONSUMABLE_ATTRIBUTES = new CompoundKeyGroup("consumable")
            .register(COMSUMABLE_EATABLE, COMSUMABLE_STATS, COMSUMABLE_TYPE);

        // Tool attributes
        final CompoundKeyGroup TOOL_ATTRIBUTES = new CompoundKeyGroup("tool")
            .register(TOOL_LEVEL, TOOL_DURABLITY, TOOL_BREAKABLE, TOOL_TOOL_TYPE);


        GENERIC_ITEM = new CompoundKeyGroup("item")
            .register(ITEM_TYPE, ITEM_DISPLAY, ITEM_PRICES, ITEM_FLAGS);

        CONSUMABLE_ITEM = new CompoundKeyGroup("consumable")
            .copy(GENERIC_ITEM).register(CONSUMABLE_ATTRIBUTES);
        
        TOOL_ITEM = new CompoundKeyGroup("tool")
            .copy(GENERIC_ITEM).register(TOOL_ATTRIBUTES);

    }

}
