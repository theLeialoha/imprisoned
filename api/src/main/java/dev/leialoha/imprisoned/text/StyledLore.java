package dev.leialoha.imprisoned.text;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class StyledLore extends StyledText {
    
    public static final Codec<StyledLore> CODEC;

    public Optional<StyledText> prefix;
    public Optional<StyledText> suffix;

    public StyledLore(String content, Style style, Optional<StyledText> prefix, Optional<StyledText> suffix) {
        super(content, style);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public Optional<StyledText> getPrefix() {
        return prefix;
    }

    public Optional<StyledText> getSuffix() {
        return suffix;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("content").forGetter(StyledText::getContent),
                Style.CODEC.optionalFieldOf("style", Style.EMPTY).forGetter(StyledText::getStyle),
                StyledText.CODEC.optionalFieldOf("prefix").forGetter(StyledLore::getPrefix),
                StyledText.CODEC.optionalFieldOf("suffix").forGetter(StyledLore::getSuffix)
            ).apply(instance, StyledLore::new)
        );
    }
    

}
