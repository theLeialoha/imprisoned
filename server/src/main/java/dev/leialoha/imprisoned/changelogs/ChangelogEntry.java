package dev.leialoha.imprisoned.changelogs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public record ChangelogEntry(Type type, String content) {

    public static ChangelogEntry create(String string) {
        Type type = Type.getType(string);
        String content = type.equals(Type.IGNORED) ? "" : string;
        content = content.replaceAll("^\\s*.\\s*", "");

        if (content.isEmpty())
            return new ChangelogEntry(type, null);
        return new ChangelogEntry(type, content);
    }

    protected boolean isMetadata() {
        return this.type.equals(Type.METADATA);
    }

    public Component asComponent() {
        return switch (type) {
            case METADATA -> null;
            case IGNORED -> Component.empty();
            case GROUP -> Component.text(content.toUpperCase())
                    .decorate(TextDecoration.BOLD)
                    .color(type.color);
            case NOTE -> Component.text(" " + content)
                    .decorate(TextDecoration.ITALIC)
                    .color(type.color);
            default -> Component.empty()
                    .append(
                        Component.text(type.display).color(type.color)
                            .decorate(TextDecoration.BOLD)
                    ).appendSpace().append(
                        Component.text(content)
                    );
        };
    }

    public enum Type {
        GROUP('#', 0xffcf22),
        ADD('+', NamedTextColor.GREEN),
        REMOVE('-', NamedTextColor.RED),
        CHANGE('~', '≈', NamedTextColor.GOLD),
        FIX('>', '»', NamedTextColor.BLUE),
        NOTE(':', NamedTextColor.GRAY),

        // Internal use
        METADATA('$' , NamedTextColor.DARK_GRAY),
        IGNORED(null, null, null);

        private final Character prefix;
        private final Character display;
        private final TextColor color;

        private Type(Character prefix, TextColor color) {
            this.prefix = prefix;
            this.display = prefix;
            this.color = color;
        }
    
        private Type(Character prefix, int color) {
            this.prefix = prefix;
            this.display = prefix;
            this.color = TextColor.color(color);
        }
    
        private Type(Character prefix, Character display, TextColor color) {
            this.prefix = prefix;
            this.display = display;
            this.color = color;
        }
    
        private Type(Character prefix, Character display, int color) {
            this.prefix = prefix;
            this.display = display;
            this.color = TextColor.color(color);
        }

        public boolean isPrefixed(String string) {
            if (string.length() == 0)
                return prefix == null;

            char strPrefix = string.charAt(0);
            return prefix != null && strPrefix == prefix.charValue();
        }

        public static Type getType(String string) {
            if (string != null) {
                String trimmed = string.trim();
    
                for (Type type : values())
                    if (type.isPrefixed(trimmed))
                        return type;
            }

            return IGNORED;
        }
    }

}
