package dev.leialoha.imprisoned.networking.packets.bothbound;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import dev.leialoha.imprisoned.networking.PacketHandler;
import dev.leialoha.imprisoned.networking.annotations.HandlePacket;
import dev.leialoha.imprisoned.networking.packets.PacketListener;
import dev.leialoha.imprisoned.reflection.BukkitReflectionUtils;
import dev.leialoha.imprisoned.reflection.Reflection;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.Sound.Source;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;

public class ClickContainer implements PacketListener {

    private static final Class<?> CONTAINER_CLICK_CLASS;
    
    @HandlePacket("ServerboundContainerClickPacket")
    public boolean onClientPacket(Object packet, PacketHandler handler) {
        int stateId = (int) Reflection.getField(packet, "stateId", CONTAINER_CLICK_CLASS);
        handler.setCurrentClick(stateId);
        return true;
    }

    @HandlePacket("ClientboundSetPlayerInventoryPacket")
    public void onInventoryChange(Object packet, PacketHandler handler) {
        checkInventoryUpdate(packet, handler);
    }

    @HandlePacket("ClientboundContainerSetSlotPacket")
    public void onContainerSlotChange(Object packet, PacketHandler handler) {
        checkInventoryUpdate(packet, handler);
    }
    
    private void checkInventoryUpdate(Object packet, PacketHandler handler) {
        Player player = handler.getPlayer();
        PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() < 0) {
            player.showTitle(Title.title(
                Component.text("Full Inventory").color(NamedTextColor.RED),
                Component.text("Sell using your backpack").color(NamedTextColor.GRAY), 
                0, 
                20 * 3, 
                10
            ));

            player.playSound(
                Sound.sound().source(Source.UI)
                    .type(() -> Key.key("item.bundle.drop_contents")
                ).build()
            );
        }
    }
    


    static {
        CONTAINER_CLICK_CLASS = BukkitReflectionUtils.getPacketClass("ServerboundContainerClickPacket");
    }

}
