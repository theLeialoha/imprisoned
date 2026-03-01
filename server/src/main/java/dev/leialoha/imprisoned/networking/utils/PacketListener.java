package dev.leialoha.imprisoned.networking.utils;

import org.bukkit.plugin.Plugin;

import dev.leialoha.imprisoned.ImprisonedPlugin;

public interface PacketListener {
    
    static Plugin getPlugin() {
        return ImprisonedPlugin.getPlugin(ImprisonedPlugin.class);
    }

}
