package dev.leialoha.imprisoned.changelog;

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

    public enum Type {
        GROUP('#', "#ffcf22"),
        ADD('+', "GREEN"),
        REMOVE('-', "RED"),
        CHANGE('~', '≈', "GOLD"),
        FIX('>', '»', "BLUE"),
        NOTE(':', "GRAY"),

        // Internal use
        METADATA('$' , "DARK_GRAY"),
        IGNORED(null, null, null);

        private final Character prefix;
        private final Character display;
        private final String color;

        private Type(Character prefix, String color) {
            this.prefix = prefix;
            this.display = prefix;
            this.color = color;
        }
    
        private Type(Character prefix, Character display, String color) {
            this.prefix = prefix;
            this.display = display;
            this.color = color;
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

        public Character getDisplayChar() {
            return display;
        }

        public String getColor() {
            return color;
        }
    }

}
