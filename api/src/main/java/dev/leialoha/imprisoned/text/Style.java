package dev.leialoha.imprisoned.text;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.leialoha.imprisoned.text.TextColor.NamedTextColor;

public record Style (
    Optional<TextColor> textColor,
    Optional<Boolean> bold,
    Optional<Boolean> italic,
    Optional<Boolean> underlined,
    Optional<Boolean> strikethrough,
    Optional<Boolean> obfuscated
) {
    public static final Style EMPTY = new Style(null, null, null, null, null, null);

    public static final Codec<Style> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            TextColor.CODEC.optionalFieldOf("color").forGetter(Style::textColor),
            Codec.BOOL.optionalFieldOf("bold").forGetter(Style::bold),
            Codec.BOOL.optionalFieldOf("italic").forGetter(Style::italic),
            Codec.BOOL.optionalFieldOf("underlined").forGetter(Style::underlined),
            Codec.BOOL.optionalFieldOf("strikethrough").forGetter(Style::strikethrough),
            Codec.BOOL.optionalFieldOf("obfuscate").forGetter(Style::obfuscated)
        ).apply(instance, Style::new)
    );

    public int color() {
        return textColor.orElse(
            NamedTextColor.WHITE.color
        ).color;
    }
}
