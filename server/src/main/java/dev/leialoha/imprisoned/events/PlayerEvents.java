package dev.leialoha.imprisoned.events;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import dev.leialoha.imprisoned.networking.PacketInjector;
import dev.leialoha.imprisoned.task.TaskHandler;
import dev.leialoha.imprisoned.task.impl.TickingTask;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PacketInjector.injectPlayer(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        PacketInjector.uninjectPlayer(event.getPlayer());
    }

    @TaskHandler
    public void onGameTick(TickingTask task) {
        List.copyOf(Bukkit.getOnlinePlayers())
            .forEach(this::tickPlayer);
    }

    private void tickPlayer(Player player) {
        player.getAttribute(Attribute.BLOCK_BREAK_SPEED).setBaseValue(0.0d);
    }

}
