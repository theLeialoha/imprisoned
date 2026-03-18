package dev.leialoha.imprisoned.registration.codec;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CompoundKeyGroup extends CompoundKey<ComponentContainer> {
    
    private final Map<String, CompoundKey<?>> KEYS = new HashMap<>();

    public CompoundKeyGroup(String groupKey) {
        super(groupKey, null, ComponentContainer.class, null);
    }

    public CompoundKeyGroup copy(CompoundKeyGroup other) {
        for (CompoundKey<?> key : other.getKeys())
            KEYS.put(key.getSerializationKey(), key);
        return this;
    }

    public CompoundKeyGroup register(CompoundKey<?> ...keys) {
        for (CompoundKey<?> key : keys)
            KEYS.put(key.getSerializationKey(), key);
        return this;
    }

    public CompoundKey<?> get(String id) {
        return KEYS.get(id);
    }

    public Collection<CompoundKey<?>> getKeys() {
        return KEYS.values();
    }

}
