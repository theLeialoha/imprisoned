package dev.leialoha.imprisoned.networking.utils;

import java.lang.reflect.Method;

import dev.leialoha.imprisoned.task.TaskManager;
import dev.leialoha.imprisoned.task.TaskPriority;
import net.minecraft.network.protocol.Packet;

public class PacketManager extends TaskManager<Packet> {

    public static final PacketManager INSTANCE = new PacketManager();

    private PacketManager() {
        super(Packet.class);
    }
    
    @Override
    protected boolean methodHasRequiredParams(Method method) {
        if (method.getParameterTypes().length != 2) return false;
        if (!Packet.class.isAssignableFrom(method.getParameterTypes()[0])) return false;
        if (!method.getParameterTypes()[1].equals(PacketHandler.class)) return false;
        return method.isAnnotationPresent(HandlePacket.class);
    }

    @Override
    protected Class<? extends Packet<?>> methodGetTask(Method method) {
        return (methodHasRequiredParams(method))
            ? method.getAnnotation(HandlePacket.class).value()
            : null;
    }

    public void register(PacketListener listener) {
        super.register(listener);
    }

    public void register(PacketListener listener, Method method, TaskPriority priority) {
        super.register(listener, method, priority);
    }

    public void unregister(PacketListener listener) {
        super.unregister(listener);
    }

    public void unregister(PacketListener listener, Class<? extends Packet<?>> task) {
        super.unregister(listener, task);
    } 

    @Deprecated
    public void register(Object listener) {
        super.register(assertPacketListener(listener));
    }

    @Override
    protected void register(Object listener, Method method) {
        super.register(assertPacketListener(listener), method, TaskPriority.NORMAL);
    }

    @Deprecated
    public void register(Object listener, Method method, TaskPriority priority) {
        super.register(assertPacketListener(listener), method, priority);
    }

    @Deprecated
    public void unregister(Object listener) {
        super.unregister(assertPacketListener(listener));
    }

    @Deprecated
    public void unregister(Object listener, Class<? extends Packet> task) {
        super.unregister(assertPacketListener(listener), task);
    }

    private PacketListener assertPacketListener(Object listener) {
        if (!(listener instanceof PacketListener packetListener))
            throw new IllegalArgumentException("Listener must be a implement " + listener.getClass());
        return packetListener;
    }

}
