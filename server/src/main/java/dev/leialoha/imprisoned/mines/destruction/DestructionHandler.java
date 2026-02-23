package dev.leialoha.imprisoned.mines.destruction;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import dev.leialoha.imprisoned.mines.world.BlockDrops;
import dev.leialoha.imprisoned.mines.world.BlockLocation;

public class DestructionHandler {
    
    private static final Map<BlockLocation, DestructionState> DESTRUCTION_STATES = new HashMap<>();

    public static DestructionState getState(BlockLocation pos) {
        return DESTRUCTION_STATES.get(pos);
    }

    public static void removeAction(Player player) {
        List.copyOf(DESTRUCTION_STATES.values()).stream()
            .filter(state -> state.hasAttackingPlayer(player))
            .forEach(state -> state.stopAttackBlock(player));
    }

    public static boolean startAction(BlockLocation pos, Player player) {
        // Make sure we stop them in their tracks
        if (inAction(player)) {
            removeAction(player);
            return false;
        }

        try {
            DESTRUCTION_STATES.computeIfAbsent(pos, DestructionState::new)
                .startAttackBlock(player);
        } catch (Exception e) {}

        return true;
    }

    public static boolean stopAction(BlockLocation pos, Player player) {
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

    public static void breakBlock(BlockLocation pos, Collection<Player> attackers) {
        DestructionState state = DESTRUCTION_STATES.remove(pos);
        BlockDrops drops = state.getBlockData().getDrops();

        attackers.forEach(drops::awardToPlayer);
        pos.getBlock().breakNaturally(true, true);
    }
}
