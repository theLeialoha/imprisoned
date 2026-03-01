package dev.leialoha.imprisoned.task.impl;

import org.bukkit.entity.Player;

import dev.leialoha.imprisoned.data.IntLocation;
import dev.leialoha.imprisoned.task.TaskCancellable;

public class StartMiningBlockTask extends TaskCancellable {
    
    public final IntLocation pos;
    public final Player player;

    public StartMiningBlockTask(IntLocation pos, Player player) {
        this.pos = pos;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public IntLocation getPos() {
        return pos;
    }

}
