package dev.leialoha.imprisoned.item.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ToolAttributes {
    
    private int level;
    private int durablity;
    private boolean breakable;
    private ToolType toolType;

    public static final Codec<ToolAttributes> CODEC;
    public static final ToolAttributes EMPTY = new ToolAttributes(1, 0, false, ToolType.NONE);

    private ToolAttributes (
        int level,
        int durablity,
        boolean breakable,
        ToolType toolType
    ) {
        this.level = level;
        this.durablity = durablity;
        this.breakable = breakable;
        this.toolType = toolType;
    }

    public int getLevel() {
        return level;
    }

    public int getDurablity() {
        return durablity;
    }

    public boolean isBreakable() {
        return breakable;
    }

    public ToolType getToolType() {
        return toolType;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.INT.fieldOf("level").forGetter(ToolAttributes::getLevel),
                Codec.INT.optionalFieldOf("durablity", 0).forGetter(ToolAttributes::getDurablity),
                Codec.BOOL.optionalFieldOf("breakable", false).forGetter(ToolAttributes::isBreakable),
                ToolType.CODEC.optionalFieldOf("tool_type", ToolType.NONE).forGetter(ToolAttributes::getToolType)
            ).apply(instance, ToolAttributes::new)
        );
    }

}

