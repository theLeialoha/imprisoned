package dev.leialoha.imprisoned.utils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import dev.leialoha.imprisoned.changelog.Changelog;
import dev.leialoha.imprisoned.changelog.ChangelogEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ChangelogUtils {
    
    public static Component asComponent(Changelog changelog) {
        Component ROOT = Component.empty();

        List<Component> components = Stream.of(changelog.getEntries())
                .map(ChangelogUtils::asComponent)
                .filter(Objects::nonNull)
                .map(Component::appendNewline)
                .toList();

        return ROOT.append(components);
    }

    public static Component asComponent(ChangelogEntry entry) {
        String content = entry.content();
        ChangelogEntry.Type type = entry.type();
        TextColor color = getColor(type.getColor());
        String display = type.getDisplayChar().toString();

        return switch (entry.type()) {
            case METADATA -> null;
            case IGNORED -> Component.empty();
            case GROUP -> Component.text(content.toUpperCase())
                    .decorate(TextDecoration.BOLD)
                    .color(color);
            case NOTE -> Component.text(" " + content)
                    .decorate(TextDecoration.ITALIC)
                    .color(color);
            default -> Component.empty()
                    .append(
                        Component.text(display).color(color)
                            .decorate(TextDecoration.BOLD)
                    ).appendSpace().append(
                        Component.text(content)
                    );
        };
    }

    private static TextColor getColor(final String colorStr) {
        final String lowerColorStr = colorStr.toLowerCase();

        if (lowerColorStr != null) {
            if (lowerColorStr.startsWith("#")) {
                String colorHexStr = lowerColorStr.substring(1);
                if (lowerColorStr.length() == 3) {
                    colorHexStr = colorHexStr.substring(0, 1).repeat(2)
                        + colorHexStr.substring(1, 2).repeat(2)
                        + colorHexStr.substring(2, 3).repeat(2);
                }

                if (colorHexStr.matches("[a-f\\d]{6}")) {
                    int colorInt = Integer.parseInt(colorHexStr, 16);
                    return TextColor.color(colorInt);
                }
            } else if (lowerColorStr.matches("^(?:(?:dark_)?(?:red|green|aqua|blue|gray)|(?:dark|light)_purple|white|black|gold|yellow)$")) {
                return NamedTextColor.NAMES.value(lowerColorStr.toUpperCase());
            }
        }

        return NamedTextColor.WHITE;
    }

}
