package dev.leialoha.imprisoned.utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Entity;

public class EntityUtils {
    
    private static final Map<Entity, Map<String, Object>> ENTITY_METADATAS = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T getMetadata(Entity entity, String metaKey) {
        Map<String, Object> metadata = ENTITY_METADATAS.get(entity);
        if (metadata == null) return null;
        return (T) metadata.get(metaKey);
    }

    public static <T> void setMetadata(Entity entity, String metaKey, T metaValue) {
        ENTITY_METADATAS.computeIfAbsent(entity, e -> new HashMap<>())
            .put(metaKey, metaValue);
    }

    public static void clearMetadata(Entity entity) {
        ENTITY_METADATAS.remove(entity);
    }

}
