package dev.leialoha.imprisoned;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import dev.leialoha.imprisoned.events.PlayerEvents;
import dev.leialoha.imprisoned.mines.TickableHandler;

public class ImprisonedPlugin extends JavaPlugin {

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        registerEvents();

        getLogger().info(Bukkit.getServer().getClass().getName());
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
        Bukkit.getPluginManager().registerEvents(new TickableHandler(), this);
    }

    @Override
    public void onDisable() {

    }
    
}
