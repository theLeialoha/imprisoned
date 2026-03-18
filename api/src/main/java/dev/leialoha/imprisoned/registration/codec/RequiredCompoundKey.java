package dev.leialoha.imprisoned.registration.codec;

import com.mojang.serialization.Codec;

import dev.leialoha.imprisoned.data.ResourceKey;

public class RequiredCompoundKey<T> extends CompoundKey<T> {

    public RequiredCompoundKey(ResourceKey key, Class<T> clazz, Codec<T> codec) {
        super(key, clazz, codec);
    }

    public RequiredCompoundKey(String serializationKey, ResourceKey key, Class<T> clazz, Codec<T> codec) {
        super(serializationKey, key, clazz, codec);
    }

}
