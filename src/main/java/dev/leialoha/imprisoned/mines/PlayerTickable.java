package dev.leialoha.imprisoned.mines;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

class PlayerTickable implements Tickable {

    protected static final PlayerTickable INSTANCE;

    private PlayerTickable() {}

    @Override
    public void onTick() {
        List.copyOf(Bukkit.getOnlinePlayers())
            .forEach(this::tickPlayer);
    }

    private void tickPlayer(Player player) {
        player.getAttribute(Attribute.BLOCK_BREAK_SPEED).setBaseValue(0.0d);
    }


    static {
        INSTANCE = new PlayerTickable();
    }

}
