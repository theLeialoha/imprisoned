package dev.leialoha.imprisoned.networking.packets.serverbound;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.leialoha.imprisoned.mines.destruction.DestructionHandler;
import dev.leialoha.imprisoned.networking.PacketHandler;
import dev.leialoha.imprisoned.networking.annotations.HandlePacket;
import dev.leialoha.imprisoned.networking.packets.PacketListener;
import dev.leialoha.imprisoned.reflection.BukkitReflectionUtils;
import dev.leialoha.imprisoned.reflection.Reflection;

public class UpdateCarriedItems implements PacketListener {

    private static final Class<?> SET_CARRIED_ITEM_PACKET_CLASS;
    private static final Class<?> SET_SLOT_PACKET_CLASS;
    private static final Class<?> NMS_ITEMSTACK_CLASS;
    private static final NamespacedKey INTERACT_RANGE_KEY;
    
    @HandlePacket("ServerboundSetCarriedItemPacket")
    public void onClientPacket(Object packet, final PacketHandler handler) {
        int miningState = handler.getMiningState();

        Bukkit.getScheduler().runTaskLater(PacketListener.getPlugin(), () -> {
            Player player = handler.getPlayer();
            if (miningState != handler.getMiningState()) return;
            if (player.getGameMode().equals(GameMode.CREATIVE)) return;

            if (handler.isMining()) {
                DestructionHandler.removeAction(player);
                
                final int slot = (int) Reflection.getField(packet, "slot", SET_CARRIED_ITEM_PACKET_CLASS);
                setItem(slot, handler, ItemStack.empty());
                preventBreak(player, true);

                Bukkit.getScheduler().runTaskLater(PacketListener.getPlugin(), () -> {
                    setItem(slot, handler);
                    preventBreak(player, false);
                }, 5l);
            }
        }, 2l);
    }

    private void setItem(int slot, PacketHandler handler) {
        Player player = handler.getPlayer();
        ItemStack heldItem = player.getInventory().getItem(slot);
        setItem(slot, handler, heldItem);
    }

    private void setItem(int slot, PacketHandler handler, ItemStack item) {
        try {
            Player player = handler.getPlayer();
            int state = handler.getNextClick();

            Object nmsItem = BukkitReflectionUtils.getNMSItem(item);
            Constructor<?> constructor = SET_SLOT_PACKET_CLASS.getConstructor(int.class, int.class, int.class, NMS_ITEMSTACK_CLASS);
            Object containerSlotPacket = Reflection.createInstance(constructor, 0, state, slot + 36, nmsItem);

            BukkitReflectionUtils.sendPacket(containerSlotPacket, player);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void preventBreak(Player player, boolean prevent) {
        AttributeInstance interactRange = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);
        if (prevent) interactRange.addModifier(new AttributeModifier(INTERACT_RANGE_KEY, -1, AttributeModifier.Operation.ADD_SCALAR));
        else interactRange.removeModifier(INTERACT_RANGE_KEY);
    }


    static {
        INTERACT_RANGE_KEY = NamespacedKey.fromString("mining_distance", PacketListener.getPlugin());
        SET_CARRIED_ITEM_PACKET_CLASS = BukkitReflectionUtils.getNMSClass("ServerboundSetCarriedItemPacket", "net.minecraft.network.protocol.game");
        SET_SLOT_PACKET_CLASS = BukkitReflectionUtils.getPacketClass("ClientboundContainerSetSlotPacket");
        NMS_ITEMSTACK_CLASS = BukkitReflectionUtils.getNMSClass("ItemStack", "net.minecraft.world.item");
    }
}
