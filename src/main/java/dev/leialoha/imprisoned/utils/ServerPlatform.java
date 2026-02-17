package dev.leialoha.imprisoned.utils;

import dev.leialoha.imprisoned.reflection.Reflection;

public enum ServerPlatform {

    GLOWSTONE,
    PAPER,
    SPIGOT,
    CRAFTBUKKIT,
    UNKNOWN;
    
    public static ServerPlatform get() {
        if (Reflection.classExists("net.glowstone.GlowServer"))
            return ServerPlatform.GLOWSTONE;
        else if (Reflection.classExists("co.aikar.timings.Timings"))
            return ServerPlatform.PAPER;
        else if (Reflection.classExists("org.spigotmc.SpigotConfig"))
            return ServerPlatform.SPIGOT;
        else if (Reflection.classAnyExists("org.bukkit.craftbukkit.CraftServer", "org.bukkit.craftbukkit.Main"))
            return ServerPlatform.CRAFTBUKKIT;
        else
            return ServerPlatform.UNKNOWN;
    }

}
