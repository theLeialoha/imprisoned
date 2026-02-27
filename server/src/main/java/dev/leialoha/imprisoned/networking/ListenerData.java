package dev.leialoha.imprisoned.networking;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import dev.leialoha.imprisoned.networking.packets.PacketListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.Packet;

public record ListenerData(PacketListener source, Method target) {

    public boolean sendPacket(Packet<?> packet, PacketHandler handler) {
        try {
            Class<?> returnType = target.getReturnType();
            Object shouldCancel = target.invoke(source, packet, handler);
    
            if (returnType.equals(boolean.class)) {
                return (boolean) shouldCancel;
            }
    
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            
            Player player = handler.getPlayer();
            player.sendMessage(
                Component.empty()
                    .append(Component.empty()
                        .append(Component.text("An error occured with packet "))
                        .append(Component.text(packet.getClass().getSimpleName()))
                        .append(Component.text(" when executing "))
                        .append(Component.text(target.getDeclaringClass().getSimpleName()))
                        .append(Component.text("."))
                        .append(Component.text(target.getName()))
                        .color(NamedTextColor.RED)
                    ).append(
                        Component.text("\n")
                    ).append(
                        Component.text("Check the console for more info...")
                            .color(NamedTextColor.GRAY)
                    ).asComponent()
            );

            return true;
        }
    }

}
