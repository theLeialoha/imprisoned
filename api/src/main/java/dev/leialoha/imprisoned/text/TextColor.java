package dev.leialoha.imprisoned.text;

import com.mojang.serialization.Codec;
 
public class TextColor {

    final String name;
    final int color;

    private TextColor(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public static final Codec<TextColor> CODEC;

    @Override
    public String toString() {
        return this.name;
    }

    private static TextColor create(String color) {
        if (!color.matches("#[\\d0-9]{6}"))
            return NamedTextColor.WHITE.color;

        int hex = Integer.parseInt(color.substring(1), 16);
        return new TextColor(color.toUpperCase(), hex);
    }

    private static TextColor fromString(String string) {
        TextColor color = NamedTextColor.colorOf(string);
        return (color == null) ? create(string) : color;
    }

    static {
        CODEC = Codec.STRING.xmap(TextColor::fromString, TextColor::toString);
    }

    enum NamedTextColor {
        BLACK(0x000000),
        DARK_BLUE(0x0000aa),
        DARK_GREEN(0x00aa00),
        DARK_AQUA(0x00aaaa),
        DARK_RED(0xaa0000),
        DARK_PURPLE(0xaa00aa),
        GOLD(0xffaa00),
        GRAY(0xaaaaaa),
        DARK_GRAY(0x555555),
        BLUE(0x5555ff),
        GREEN(0x55ff55),
        AQUA(0x55ffff),
        RED(0xff5555),
        LIGHT_PURPLE(0xff55ff),
        YELLOW(0xffff55),
        WHITE(0xffffff);

        TextColor color;

        NamedTextColor(int color) {
            this.color = new TextColor(name(), color);
        }

        public TextColor getColor() {
            return color;
        }

        public static TextColor colorOf(String string) {
            NamedTextColor named = valueOf(string.toUpperCase());
            return (named == null) ? null : named.color;
        }
    }
}
