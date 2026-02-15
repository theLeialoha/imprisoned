package dev.leialoha.imprisoned.events;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;

public class ServerEvents implements Listener {

    @EventHandler
    public void onTicked(ServerTickStartEvent event) {
        for (Player player : Bukkit.getOnlinePlayers())
            player.getAttribute(Attribute.BLOCK_BREAK_SPEED).setBaseValue(0.0d);
    }

}
