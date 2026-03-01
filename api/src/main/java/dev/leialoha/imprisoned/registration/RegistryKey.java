package dev.leialoha.imprisoned.registration;

import java.util.HashMap;
import java.util.Map;

import dev.leialoha.imprisoned.data.ResourceKey;

public final class RegistryKey<T> {

    private final static Map<ResourceKey, RegistryKey<?>> REGISTRY_KEYS = new HashMap<>();
    private final Registry<T> registry;

    protected RegistryKey(String name) {
        ResourceKey key = ResourceKey.tryBuild("core", name);
        if (REGISTRY_KEYS.containsKey(key))
            throw new IllegalArgumentException("Regitry already exists with key: " + key.toString());
        
        REGISTRY_KEYS.put(key, this);
        this.registry = new Registry<>();
    }

    protected RegistryKey(ResourceKey key) {
        if (key.getNamespace().equalsIgnoreCase("core"))
            throw new IllegalArgumentException("Disallowed namespace for key: " + key.toString());
        if (REGISTRY_KEYS.containsKey(key))
            throw new IllegalArgumentException("Regitry already exists with key: " + key.toString());
        
        REGISTRY_KEYS.put(key, this);
        this.registry = new Registry<>();
    }

    public Registry<T> getRegistry() {
        return registry;
    }

}
