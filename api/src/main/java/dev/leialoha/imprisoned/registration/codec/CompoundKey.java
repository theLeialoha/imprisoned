package dev.leialoha.imprisoned.registration.codec;

import java.util.Objects;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import dev.leialoha.imprisoned.data.ResourceKey;

public class CompoundKey<T> {

    protected final String serializationKey;
    protected final ResourceKey key;
    protected final Class<T> clazz;
    protected final Codec<T> codec;
    
    public CompoundKey(ResourceKey key, Class<T> clazz, Codec<T> codec) {
        this(key.getPath(), key, clazz, codec);
    }

    public CompoundKey(String serializationKey, ResourceKey key, Class<T> clazz, Codec<T> codec) {
        this.serializationKey = serializationKey;
        this.key = key;
        this.clazz = clazz;
        this.codec = codec;
    }

    public DefaultCompoundKey withDefault(T defaultValue) {
        return new DefaultCompoundKey<>(serializationKey, key, clazz, codec, defaultValue);
    }

    public RequiredCompoundKey asRequired() {
        return new RequiredCompoundKey<>(serializationKey, key, clazz, codec);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompoundKey<?> other)) return false;
        return key.equals(other.key) && clazz.equals(other.clazz);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(key, clazz);
    }

    public String getSerializationKey() {
        return serializationKey;
    }

    public ResourceKey getKey() {
        return key;
    }

    public Class<T> getType() {
        return clazz;
    }

    public Codec<T> getCodec() {
        return codec;
    }

    protected Optional<T> decode(JsonElement input) {
        return codec.parse(JsonOps.INSTANCE, input).result();
    }

    @SuppressWarnings("unchecked")
    protected Optional<JsonElement> encode(Object input) {
        return codec.encodeStart(JsonOps.INSTANCE, (T) input).result();
    }

}
