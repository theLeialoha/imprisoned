package dev.leialoha.imprisoned.destruction;

import java.io.InvalidObjectException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import dev.leialoha.imprisoned.world.BlockDrops;
import dev.leialoha.imprisoned.world.BlockLocation;

public class DestructionHandler {
    
    private static final Map<BlockLocation, DestructionState> DESTRUCTION_STATES = new HashMap<>();

    public static DestructionState getState(BlockLocation pos) {
        return DESTRUCTION_STATES.get(pos);
    }

    public static boolean startDestruction(BlockLocation pos, Player player) {
        try {
            DESTRUCTION_STATES.computeIfAbsent(pos, DestructionState::new)
                .startAttackBlock(player);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void stopDestruction(BlockLocation pos, Player player) {
        DestructionState state = DESTRUCTION_STATES.get(pos);
        if (state != null) state.stopAttackBlock(player);
    }

    public static void breakBlock(BlockLocation pos, Collection<Player> attackers) {
        BlockDrops drops = DESTRUCTION_STATES.remove(pos)
            .getBlockData().getDrops();

        attackers.forEach(drops::awardToPlayer);
        pos.getBlock().breakNaturally(true, true);
    }

    public static void onTick() {
        List.copyOf(DESTRUCTION_STATES.values())
            .forEach(DestructionState::onTick);
    }


}
