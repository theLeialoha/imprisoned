package dev.leialoha.imprisoned.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StyledText {
    
    public static final Codec<StyledText> CODEC;

    private String content;
    private Style style;

    public StyledText(String content, Style style) {
        this.content = content;
        this.style = style;
    }

    public String getContent() {
        return content;
    }

    public Style getStyle() {
        return style;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> 
            instance.group(
                Codec.STRING.fieldOf("content").forGetter(StyledText::getContent),
                Style.CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(StyledText::getStyle)
            ).apply(instance, StyledText::new)
        );
    }

}
