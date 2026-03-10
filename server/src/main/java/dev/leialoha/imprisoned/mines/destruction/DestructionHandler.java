package dev.leialoha.imprisoned.mines.destruction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import dev.leialoha.imprisoned.block.BlockDrops;
import dev.leialoha.imprisoned.data.IntLocation;
import dev.leialoha.imprisoned.data.ResourceKey;
import dev.leialoha.imprisoned.drop.DropEntry;
import dev.leialoha.imprisoned.drop.DropTable;
import dev.leialoha.imprisoned.item.Enchantments;
import dev.leialoha.imprisoned.item.Item;
import dev.leialoha.imprisoned.item.ItemHolder;
import dev.leialoha.imprisoned.registration.Registry;
import dev.leialoha.imprisoned.registration.RegistryKeys;
import dev.leialoha.imprisoned.task.TaskCancellable;
import dev.leialoha.imprisoned.task.TaskHandler;
import dev.leialoha.imprisoned.task.impl.StartMiningBlockTask;
import dev.leialoha.imprisoned.task.impl.TickingTask;
import dev.leialoha.imprisoned.utils.BiComponent;
import dev.leialoha.imprisoned.utils.BukkitConversion;
import dev.leialoha.imprisoned.utils.ItemBuilder;
import dev.leialoha.imprisoned.utils.PlayerUtils;

public class DestructionHandler {
    
    private static final Map<IntLocation, DestructionState> DESTRUCTION_STATES = new HashMap<>();

    public static DestructionState getState(IntLocation pos) {
        return DESTRUCTION_STATES.get(pos);
    }

    public static void removeAction(Player player) {
        List.copyOf(DESTRUCTION_STATES.values()).stream()
            .filter(state -> state.hasAttackingPlayer(player))
            .forEach(state -> state.stopAttackBlock(player));
    }

    public static boolean startAction(IntLocation pos, Player player) {
        TaskCancellable task = new StartMiningBlockTask(pos, player);
        task.call();

        if (task.isCancelled())
            return false;

        // Make sure we stop them in their tracks
        if (inAction(player)) {
            removeAction(player);
            return false;
        }

        try {
            DESTRUCTION_STATES.computeIfAbsent(pos, DestructionState::new)
                .startAttackBlock(player);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean stopAction(IntLocation pos, Player player) {
        // Make sure we stop them in their tracks
        if (!inAction(player)) {
            removeAction(player);
            return false;
        }

        DestructionState state = DESTRUCTION_STATES.get(pos);
        if (state != null) state.stopAttackBlock(player);

        return true;
    }

    private static boolean inAction(Player player) {
        return List.copyOf(DESTRUCTION_STATES.values()).stream()
                .anyMatch(state -> state.hasAttackingPlayer(player));
    }

    public static void breakBlock(IntLocation pos, Collection<Player> attackers) {
        DestructionState state = DESTRUCTION_STATES.remove(pos);
        Location location = BukkitConversion.asLocation(pos);
        BlockDrops drops = state.getBlock().drops();

        List<BiComponent<Player, ItemHolder>> playerTools = attackers.stream()
            .map(p -> new BiComponent<>(p, p).mapSecond(DestructionHandler::getTool))
            .toList();

        playerTools.stream()
            .map(c -> c.mapSecond(generateDrop(drops)))
            .forEach(dropItem(pos));

        location.getBlock().breakNaturally(true);
    }

    private static ItemHolder getTool(Player player) {
        ItemStack stack = player.getInventory().getItemInMainHand();

        return ItemBuilder.ItemHolderBuilder.from(stack)
            .build();
    }

    private static Function<ItemHolder, BiComponent<ItemHolder, ItemHolder>> generateDrop(BlockDrops drops) {
        return (tool) -> {
            final Registry<DropTable> DROPS_REGISTRY = RegistryKeys.DROPS.getRegistry();
            final Registry<Item> ITEM_REGISTRY = RegistryKeys.ITEMS.getRegistry();

            ResourceKey tableKey = drops.dropTableId;
            DropTable table = DROPS_REGISTRY.get(tableKey);

            // We randomize the list to make equalally probable items randomly picked as well
            List<DropEntry> entries = new ArrayList<>(table.getEntries());
            Collections.shuffle(entries);

            ItemHolder drop = entries.stream()
                // Sort from smallest to biggest
                .sorted(Comparator.comparingDouble(DropEntry::getChance))
                // Roll for each item
                .filter(e -> Math.random() < e.chance)
                // Get first (smallest) roll
                .findFirst()
                .map(DropEntry::getItem)
                .map(ITEM_REGISTRY::get)
                .map(ItemHolder::new)
                .orElse(null);

            return new BiComponent<>(tool, drop);
        };
    }

    private static Consumer<BiComponent<Player, BiComponent<ItemHolder, ItemHolder>>> dropItem(IntLocation pos) {
        return (b) -> {
            Player player = b.getFirst();
            ItemHolder tool = b.getSecond().getFirst();
            ItemHolder drop = b.getSecond().getSecond();

            boolean hasTelekinesis = tool != null && tool.hasEnchantment(Enchantments.TELEKINESIS);

            if (hasTelekinesis) PlayerUtils.giveItem(player, drop, pos);
            else PlayerUtils.dropItem(player, drop, pos);
        };
    }

    @TaskHandler
    public void onGameTick(TickingTask task) {
        DESTRUCTION_STATES.values()
            .forEach(DestructionState::onTick);
    }
}
