package dev.leialoha.imprisoned.destruction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import dev.leialoha.imprisoned.world.BlockDrops;
import net.minecraft.core.BlockPos;

public class DestructionHandler {
    
    private static final Map<BlockPos, DestructionState> DESTRUCTION_STATES = new HashMap<>();

    public static DestructionState getState(BlockPos pos) {
        return DESTRUCTION_STATES.get(pos);
    }

    public static boolean startDestruction(BlockPos pos, Player player) {
        boolean notExists = !DESTRUCTION_STATES.containsKey(pos);

        DESTRUCTION_STATES.computeIfAbsent(pos, DestructionState::new)
            .startAttackBlock(player);

        return notExists;
    }

    public static void stopDestruction(BlockPos pos, Player player) {
        DestructionState state = DESTRUCTION_STATES.get(pos);
        if (state != null) state.stopAttackBlock(player);
    }

    public static void breakBlock(BlockPos pos, Collection<Player> attackers) {
        BlockDrops drops = DESTRUCTION_STATES.remove(pos)
            .getBlockData().getDrops();

        attackers.forEach(drops::awardToPlayer);
    }


}
