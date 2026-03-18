package dev.leialoha.imprisoned.registration.codec;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;

@SuppressWarnings({"unchecked", "rawtype"})
public class ComponentContainer {
    
    private final Map<CompoundKey<?>, Object> data = new HashMap<>();

    public <T> void set(CompoundKey<T> key, T value) {
        data.put(key, value);
    }

    protected final void copyFrom(ComponentContainer container) {
        data.putAll(container.data);
    }

    @Deprecated
    protected void unsafeSet(CompoundKey<?> key, Object value) {
        data.put(key, value);
    }

    public <T> Optional<T> get(CompoundKey<T> key) {
        return Optional.ofNullable((T) data.get(key));
    }

    public <T> T get(RequiredCompoundKey<T> key) {
        T value = (T) data.get(key);
        if (value == null) throw new IllegalArgumentException(key + " doesnt't exist in this ComponentContainer");
        return value;
    }

    public <T> T get(DefaultCompoundKey<T> key) {
        return (T) data.getOrDefault(key, key.defaultValue);
    }

    private static Function<Map<String, Dynamic<?>>, ComponentContainer> fromDynamicMap(CompoundKeyGroup group) {
        return (map) -> fromDynamicMap(group, map);
    }

    private static Function<ComponentContainer, Map<String, Dynamic<?>>> toDynamicMap(CompoundKeyGroup group) {
        return (container) -> toDynamicMap(group, container);
    }

    private static ComponentContainer fromDynamicMap(CompoundKeyGroup group, Map<String, Dynamic<?>> map) {
        ComponentContainer container = new ComponentContainer();

        map.forEach((key, value) -> {
            CompoundKey<?> compoundKey = group.get(key);
            if (compoundKey == null) return;

            if (compoundKey instanceof CompoundKeyGroup group2) {
                getCodec(group2).parse(JsonOps.INSTANCE, value.cast(JsonOps.INSTANCE))
                    .result().ifPresent(container::copyFrom);
            } else {
                compoundKey.decode(value.cast(JsonOps.INSTANCE))
                    .ifPresent(v -> container.unsafeSet(compoundKey, v));
            }

        });

        return container;
    }

    private static Map<String, Dynamic<?>> toDynamicMap(CompoundKeyGroup group, ComponentContainer container) {
        Map<String, Dynamic<?>> map = new HashMap<>();

        String key;
        Dynamic<?> dynamic;
        Optional<?> optional;

        for (CompoundKey<?> compoundKey : group.getKeys()) {
            key = compoundKey.getSerializationKey();
            if (compoundKey instanceof RequiredCompoundKey<?> requiredCompoundKey)
                optional = compoundKey.encode(container.get(requiredCompoundKey));
            else if (compoundKey instanceof DefaultCompoundKey<?> defaultCompoundKey)
                optional = compoundKey.encode(container.get(defaultCompoundKey));
            else if (compoundKey instanceof CompoundKeyGroup group2) 
                optional = getCodec(group2).encodeStart(JsonOps.INSTANCE, container).result();
            else
                optional = container.get(compoundKey).map(compoundKey::encode);

            if (optional.isPresent()) {
                dynamic = new Dynamic(JsonOps.INSTANCE, (Object) optional.get());
                map.put(key, dynamic);

            }
        }
    
        return map;
    }

    public static Codec<ComponentContainer> getCodec(CompoundKeyGroup group) {
        return Codec.unboundedMap(Codec.STRING, Codec.PASSTHROUGH)
            .xmap(fromDynamicMap(group), toDynamicMap(group));
    }

    public static MapCodec<ComponentContainer> getMapCodec(CompoundKeyGroup group) {
        return MapCodec.assumeMapUnsafe(getCodec(group));
    }

}
