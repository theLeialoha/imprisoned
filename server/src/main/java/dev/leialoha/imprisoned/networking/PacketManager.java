package dev.leialoha.imprisoned.networking;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.leialoha.imprisoned.networking.annotations.HandlePacket;
import dev.leialoha.imprisoned.networking.packets.PacketListener;
import dev.leialoha.imprisoned.reflection.BukkitReflectionUtils;

public final class PacketManager {

    private PacketManager() {}

    private static final Map<Class<?>, List<ListenerData>> REGISTRY_MAP = new HashMap<>();

    private static boolean methodHasRequiredParams(Method method) {
        if (method.getParameterTypes().length != 2) return false;
        if (!method.getParameterTypes()[0].equals(Object.class)) return false;
        return method.getParameterTypes()[1].equals(PacketHandler.class);
    }

    private static boolean methodHasPacketClass(Method method) {
        if (!method.isAnnotationPresent(HandlePacket.class)) return false;
        return methodGetPacket(method) != null;
    }

    private static Class<?> methodGetPacket(Method method) {
        HandlePacket annotation = method.getAnnotation(HandlePacket.class);
        return BukkitReflectionUtils.getPacketClass(annotation.value(), annotation.state());
    }

    public static List<ListenerData> get(Class<?> packet) {
        return REGISTRY_MAP.get(packet);
    }

    private static void cleanMap(boolean removeOnlyEmpty) {
        Map.copyOf(REGISTRY_MAP).forEach((k, v) -> {
            if (!removeOnlyEmpty || v.isEmpty())
                REGISTRY_MAP.remove(k);
        });
    }

    public static void register(PacketListener listener) {
        Class<? extends PacketListener> clazz = listener.getClass();
        List.of(clazz.getMethods()).forEach(m -> register(listener, m));
    }

    private static void register(PacketListener listener, Method method) {
        if (!methodHasPacketClass(method)) return;
        if (!methodHasRequiredParams(method)) return;
        
        Class<?> packet = methodGetPacket(method);
        ListenerData data = new ListenerData(listener, method);

        REGISTRY_MAP.computeIfAbsent(packet, p -> new ArrayList<>())
            .add(data);
    }

    public static void unregister(PacketListener listener) {
        Map.copyOf(REGISTRY_MAP).forEach((key, list) -> {
            list.removeIf(v -> v.source().equals(listener));
        });

        cleanMap(true);
    }

    public static void unregister(PacketListener listener, Class<?> packet) {
        if (!REGISTRY_MAP.containsKey(packet)) return;

        REGISTRY_MAP.get(packet)
            .removeIf(v -> v.source().equals(listener));

        cleanMap(true);
    }

}
