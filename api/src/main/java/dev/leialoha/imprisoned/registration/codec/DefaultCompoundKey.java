package dev.leialoha.imprisoned.registration.codec;

import java.util.Objects;

import com.mojang.serialization.Codec;

import dev.leialoha.imprisoned.data.ResourceKey;

public class DefaultCompoundKey<T> extends CompoundKey<T> {

    protected final T defaultValue;

    public DefaultCompoundKey(ResourceKey key, Class<T> clazz, Codec<T> codec, T defaultValue) {
        super(key, clazz, codec);
        this.defaultValue = defaultValue;
    }

    public DefaultCompoundKey(String serializationKey, ResourceKey key, Class<T> clazz, Codec<T> codec, T defaultValue) {
        super(serializationKey, key, clazz, codec);
        this.defaultValue = defaultValue;
    }

    public boolean matchesValue(T value) {
        return Objects.equals(defaultValue, value);
    }

    public CompoundKey<T> ignoresDefault() {
        return new CompoundKey<>(key, clazz, codec);
    }

    public T getDefaultValue() {
        return defaultValue;
    }
}
