package dev.leialoha.imprisoned.registration;

import dev.leialoha.imprisoned.data.ResourceKey;

public final class RegistryEntry<T> {
    
    private final ResourceKey key;
    private final T value;

    protected RegistryEntry(ResourceKey key, T value) {
        this.key = key;
        this.value = value;
    }

    public ResourceKey getKey() {
        return key;
    }

    public T get() {
        return value;
    }
}
