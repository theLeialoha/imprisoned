package dev.leialoha.imprisoned.item.consumable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ConsumableAttributes {
    
    private boolean eatable;
    private FoodAttributes foodAttributes;
    private ConsumableType consumableType;

    public static final Codec<ConsumableAttributes> CODEC;
    public static final ConsumableAttributes EMPTY = new ConsumableAttributes(false, FoodAttributes.EMPTY, ConsumableType.OTHER);

    private ConsumableAttributes (
        boolean eatable,
        FoodAttributes foodAttributes,
        ConsumableType consumableType
    ) {
        this.eatable = eatable;
        this.foodAttributes = foodAttributes;
        this.consumableType = consumableType;
    }

    public boolean isEatable() {
        return eatable;
    }

    public FoodAttributes getFoodAttributes() {
        return foodAttributes;
    }

    public ConsumableType getConsumableType() {
        return consumableType;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.BOOL.optionalFieldOf("eatable", false).forGetter(ConsumableAttributes::isEatable),
                FoodAttributes.CODEC.optionalFieldOf("stats", FoodAttributes.EMPTY).forGetter(ConsumableAttributes::getFoodAttributes),
                ConsumableType.CODEC.optionalFieldOf("type", ConsumableType.OTHER).forGetter(ConsumableAttributes::getConsumableType)
            ).apply(instance, ConsumableAttributes::new)
        );
    }

}

