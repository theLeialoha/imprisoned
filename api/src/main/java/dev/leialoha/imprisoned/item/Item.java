package dev.leialoha.imprisoned.item;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import dev.leialoha.imprisoned.registration.codec.ComponentContainer;
import dev.leialoha.imprisoned.registration.codec.CompoundKeys;

public class Item extends ComponentContainer {
    
    public static final Codec<Item> CODEC;
    
    private Item(ComponentContainer container) {
        this.copyFrom(container);
    }


    public boolean isArtifact() {
        return get(CompoundKeys.ITEM_TYPE)
            .equals(ItemType.COLLECTABLE);
    }

    public int getMaxStackSize() {
        return 1;
        // return this.isArtifact() ? 1
        //     : get(CompoundKeys.);
    }


    

    private static MapCodec<ComponentContainer> codecFromType(String type) {
        ItemType iType = ItemType.valueOf(type.toUpperCase());
        return switch (iType) {
            case CONSUMABLE -> ComponentContainer.getMapCodec(CompoundKeys.CONSUMABLE_ITEM);
            case TOOL -> ComponentContainer.getMapCodec(CompoundKeys.TOOL_ITEM);
            default -> ComponentContainer.getMapCodec(CompoundKeys.GENERIC_ITEM);
        };
    }

    private static String getTypeStr(ComponentContainer container) {
        return container.get(CompoundKeys.ITEM_NAME).toString();
    }

    static {
        CODEC = Codec.STRING.dispatch(Item::getTypeStr, Item::codecFromType)
            .xmap(Item::new, Function.identity());
    }

}
