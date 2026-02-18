package dev.leialoha.imprisoned.networking.packets.bothbound;

import dev.leialoha.imprisoned.networking.PacketHandler;
import dev.leialoha.imprisoned.networking.annotations.HandlePacket;
import dev.leialoha.imprisoned.networking.packets.PacketListener;
import dev.leialoha.imprisoned.reflection.BukkitReflectionUtils;
import dev.leialoha.imprisoned.reflection.Reflection;

public class ClickContainer implements PacketListener {

    private static final Class<?> CONTAINER_CLICK_CLASS;
    
    @HandlePacket("ServerboundContainerClickPacket")
    public boolean onClientPacket(Object packet, PacketHandler handler) {
        int stateId = (int) Reflection.getField(packet, "stateId", CONTAINER_CLICK_CLASS);
        handler.setCurrentClick(stateId);
        return true;
    }

    static {
        CONTAINER_CLICK_CLASS = BukkitReflectionUtils.getPacketClass("ServerboundContainerClickPacket");
    }

}
