package dev.leialoha.imprisoned.changelogs;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.kyori.adventure.text.Component;

public class Changelog {

    private final static UUID GITHUB_UUID;
    private final static UUID SERVER_UUID;

    private final String hash;
    private final ChangelogEntry[] entries;
    private final Map<String, String> metadata;

    protected Changelog(String hash, ChangelogEntry[] entries) {
        this.hash = hash;
        this.entries = entries;

        this.metadata = genMetadata();
    }

    public String getHash() {
        return hash;
    }

    public ChangelogEntry[] getEntries() {
        return entries;
    }

    public boolean fromGithub() {
        return getAuthor().equals(GITHUB_UUID);
    }

    public UUID getAuthor() {
        String authoredStr = metadata.get("Authored-By");
        if (authoredStr == null) return SERVER_UUID;
        return parseUUID(authoredStr);
    }

    public Component asComponent() {
        Component ROOT = Component.empty();

        List<Component> components = Stream.of(entries)
            .map(ChangelogEntry::asComponent)
            .filter(Objects::nonNull)
            .map(Component::appendNewline)
            .toList();

        return ROOT.append(components);
    } 

    private UUID parseUUID(String uuidStr) {
        String uuidNull = "0".repeat(32);
        String uuidTrimmed = uuidStr.trim().replaceAll("\\-", "");
        String joinedUUID = uuidNull + uuidTrimmed;
        int length = joinedUUID.length();

        String paddedUUID = joinedUUID.substring(length-32, length);
        return UUID.fromString(paddedUUID);
    }

    private Map<String, String> genMetadata() {
        return Stream.of(this.entries)
            .filter(ChangelogEntry::isMetadata)
            .map(ChangelogEntry::content)
            .map(Changelog::asMetadata)
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    private static Map.Entry<String, String> asMetadata(String string) {
        if (string.isBlank()) return null;

        String[] entry = string.split("\\s*:\\s*", 2);
        String key = (entry.length > 0) ? entry[0] : null;
        String value = (entry.length > 1) ? entry[1] : null;

        if (key == null) return null;
        return Map.entry(key, value);
    }

    static {
        GITHUB_UUID = UUID.fromString("00000000-0000-0000-12f3-aa8e26f84960");
        SERVER_UUID = UUID.fromString("00000000-0000-0000-0cc9-293b49fe464e");
    }

}
