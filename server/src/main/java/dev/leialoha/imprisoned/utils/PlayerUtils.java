package dev.leialoha.imprisoned.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.leialoha.imprisoned.data.IntLocation;
import dev.leialoha.imprisoned.item.ItemHolder;
import dev.leialoha.imprisoned.utils.builders.item.stack.ItemStackBuilder;

public class PlayerUtils {

    private PlayerUtils() {
        throw new IllegalAccessError("Class doesn't need to be initalized");
    }

    
    public static void giveItem(Player player, ItemHolder holder) {
        ItemStack stack = ItemStackBuilder.from(holder)
            .build();

        if (stack != null) giveItem(player, stack, player.getLocation());
    }
    
    public static void giveItem(Player player, ItemHolder holder, IntLocation pos) {
        Location loc = BukkitConversion.asLocation(pos);
        ItemStack stack = ItemStackBuilder.from(holder)
            .build();

        if (stack != null) giveItem(player, stack, loc);
    }

    private static void giveItem(Player player, ItemStack stack, Location location) {
        // TODO: Backpacks

        if (!isInventoryFull(player)) player.give(stack);
        else dropItem(player, stack, location);
    }

    public static void dropItem(Player player, ItemHolder holder) {
        ItemStack stack = ItemStackBuilder.from(holder)
            .build();

        if (stack != null) dropItem(player, stack, player.getLocation());
    }

    public static void dropItem(Player player, ItemHolder holder, IntLocation pos) {
        Location loc = BukkitConversion.asLocation(pos);
        ItemStack stack = ItemStackBuilder.from(holder)
            .build();

        if (stack != null) dropItem(player, stack, loc);
    }

    private static void dropItem(Player player, ItemStack stack, Location location) {
        World world = location.getWorld();
        Item item = world.dropItem(location, stack);

        // 1 min delay
        item.setPickupDelay(20 * 60);

        // TODO: prevent pickups
        EntityUtils.setMetadata(item, "owner", player.getUniqueId());
        EntityUtils.setMetadata(item, "pickup_delay", 20 * 60);
    }

    public static boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() < 0;
    }

}
