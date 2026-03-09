package dev.leialoha.imprisoned.registration;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.leialoha.imprisoned.data.ResourceKey;

public class Registry<T> {

    private final Set<RegistryEntry<T>> entries = new HashSet<>();

    protected Registry() {
    }

    public boolean contains(Object object) {
        if (entries.contains(object)) return true;
        return entries.stream().map(RegistryEntry::get)
            .anyMatch(e -> e.equals(object));
    }

    public boolean isEmpty() {
        return getEntries().isEmpty();
    }

    public int size() {
        return getEntries().size();
    }

    public Collection<RegistryEntry<T>> getEntries() {
        return List.copyOf(entries);
    }

    public T get(ResourceKey resourceKey) {
        return entries.stream().filter(e -> e.getKey().equals(resourceKey))
            .map(RegistryEntry::get).findFirst().orElse(null);
    }

    public ResourceKey getKey(T value) {
        return entries.stream().filter(e -> e.get().equals(value))
            .findFirst().map(RegistryEntry::getKey).orElse(null);
    }

    public RegistryEntry<T> register(ResourceKey resourceKey, T entryValue) {
        boolean exists = entries.stream()
            .map(RegistryEntry::getKey)
            .anyMatch(e -> e.equals(resourceKey));

        if (exists) {
            throw new IllegalArgumentException("An object named \"" + resourceKey + "\" already exists");
        }

        RegistryEntry<T> entry = new RegistryEntry<>(resourceKey, entryValue);
        entries.add(entry);

        return entry;
    }

}
