package dev.leialoha.imprisoned;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;

import dev.leialoha.imprisoned.block.Blocks;
import dev.leialoha.imprisoned.changelog.ChangelogHandler;
import dev.leialoha.imprisoned.commands.ChangelogCommand;
import dev.leialoha.imprisoned.compat.Compatabilities;
import dev.leialoha.imprisoned.events.PlayerEvents;
import dev.leialoha.imprisoned.mines.destruction.DestructionHandler;
import dev.leialoha.imprisoned.networking.utils.PacketManager;
import dev.leialoha.imprisoned.task.Task;
import dev.leialoha.imprisoned.task.TaskManager;
import dev.leialoha.imprisoned.task.impl.TickingTask;
import dev.leialoha.imprisoned.networking.packets.bothbound.ClickContainer;
import dev.leialoha.imprisoned.networking.packets.serverbound.PlayerAction;
import dev.leialoha.imprisoned.networking.packets.serverbound.UpdateCarriedItems;

public class ImprisonedPlugin extends JavaPlugin implements Listener {

    public static File CHANGELOG_FOLDER;

    @Override
    public void onLoad() {
        Blocks.init();
        registerExtras();

        CHANGELOG_FOLDER = new File(getDataFolder(), "changelogs");
    }

    @Override
    public void onEnable() {
        registerEvents();
        registerPackets();
        registerCommands();

        ChangelogHandler.loadChangelogs(CHANGELOG_FOLDER);
    }

    @Override
    public void onDisable() {

    }

    private void registerEvents() {
        PlayerEvents playerEvents = new PlayerEvents();

        Bukkit.getPluginManager().registerEvents(playerEvents, this);
        Bukkit.getPluginManager().registerEvents(this, this);
        
        final TaskManager<Task> manager = TaskManager.INSTANCE;

        manager.register(playerEvents);
        manager.register(new DestructionHandler());
    }

    private void registerPackets() {
        PacketManager.INSTANCE.register(new PlayerAction());
        PacketManager.INSTANCE.register(new UpdateCarriedItems());
        PacketManager.INSTANCE.register(new ClickContainer());
    }

    private void registerCommands() {
        getCommand("changelog").setExecutor(new ChangelogCommand());
    }

    private void registerExtras() {
        Server server = getServer();

        Compatabilities.WORLD_GUARD.register(server);
    }

    @EventHandler
    public void onTick(ServerTickStartEvent event) {
        TickingTask task = new TickingTask();
        task.call();
    }

}
