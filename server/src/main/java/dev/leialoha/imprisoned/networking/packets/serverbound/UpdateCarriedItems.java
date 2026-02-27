package dev.leialoha.imprisoned.networking.packets.serverbound;

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
import dev.leialoha.imprisoned.utils.MinecraftUtils;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;

public class UpdateCarriedItems implements PacketListener {

    private static final NamespacedKey INTERACT_RANGE_KEY;
    
    @HandlePacket(ServerboundSetCarriedItemPacket.class)
    public void onClientPacket(ServerboundSetCarriedItemPacket packet, final PacketHandler handler) {
        int miningState = handler.getMiningState();

        Bukkit.getScheduler().runTaskLater(PacketListener.getPlugin(), () -> {
            Player player = handler.getPlayer();
            if (miningState != handler.getMiningState()) return;
            if (player.getGameMode().equals(GameMode.CREATIVE)) return;

            if (handler.isMining()) {
                DestructionHandler.removeAction(player);

                final int slot = packet.getSlot();
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
            Player player = handler.getPlayer();
            int state = handler.getNextClick();

            net.minecraft.world.item.ItemStack nmsItem = MinecraftUtils.getNMSItem(item);
            ClientboundContainerSetSlotPacket containerSlotPacket = new ClientboundContainerSetSlotPacket(0, state, slot + 36, nmsItem);
            MinecraftUtils.sendPacket(containerSlotPacket, player);
    }

    private void preventBreak(Player player, boolean prevent) {
        AttributeInstance interactRange = player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE);
        if (prevent) interactRange.addModifier(new AttributeModifier(INTERACT_RANGE_KEY, -1, AttributeModifier.Operation.ADD_SCALAR));
        else interactRange.removeModifier(INTERACT_RANGE_KEY);
    }


    static {
        INTERACT_RANGE_KEY = NamespacedKey.fromString("mining_distance", PacketListener.getPlugin());
    }
}
