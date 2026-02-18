package dev.leialoha.imprisoned;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import dev.leialoha.imprisoned.events.PlayerEvents;
import dev.leialoha.imprisoned.mines.TickableHandler;
import dev.leialoha.imprisoned.networking.PacketManager;
import dev.leialoha.imprisoned.networking.packets.bothbound.ClickContainer;
import dev.leialoha.imprisoned.networking.packets.serverbound.PlayerAction;
import dev.leialoha.imprisoned.networking.packets.serverbound.UpdateCarriedItems;

public class ImprisonedPlugin extends JavaPlugin {

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        registerEvents();
        registerPackets();

        getLogger().info(Bukkit.getServer().getClass().getName());
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
        Bukkit.getPluginManager().registerEvents(new TickableHandler(), this);
    }

    private void registerPackets() {
        PacketManager.register(new PlayerAction());
        PacketManager.register(new UpdateCarriedItems());
        PacketManager.register(new ClickContainer());
    }

    @Override
    public void onDisable() {

    }
    
}
