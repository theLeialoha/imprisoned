package dev.leialoha.imprisoned.item.consumable;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class FoodAttributes {
    
    private int food;
    private float saturation;
    private List<String> effects;

    public static final Codec<FoodAttributes> CODEC;
    public static final FoodAttributes EMPTY = new FoodAttributes(0, 0, List.of());

    private FoodAttributes (
        int food,
        float saturation,
        List<String> effects
    ) {
        this.food = food;
        this.saturation = saturation;
        this.effects = effects;
    }

    public int getFood() {
        return food;
    }

    public float getSaturation() {
        return saturation;
    }

    public List<String> getEffects() {
        return effects;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.optionalFieldOf("food", 0).forGetter(FoodAttributes::getFood),
                Codec.FLOAT.optionalFieldOf("saturation", 0f).forGetter(FoodAttributes::getSaturation),
                Codec.STRING.listOf().optionalFieldOf("effects", List.of()).forGetter(FoodAttributes::getEffects)
            ).apply(instance, FoodAttributes::new)
        );
    }

}

