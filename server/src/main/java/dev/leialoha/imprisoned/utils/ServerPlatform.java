package dev.leialoha.imprisoned.utils;

public enum ServerPlatform {

    GLOWSTONE,
    PAPER,
    SPIGOT,
    CRAFTBUKKIT,
    UNKNOWN;
    
    public static ServerPlatform get() {
        if (classExists("net.glowstone.GlowServer"))
            return ServerPlatform.GLOWSTONE;
        else if (classExists("co.aikar.timings.Timings"))
            return ServerPlatform.PAPER;
        else if (classExists("org.spigotmc.SpigotConfig"))
            return ServerPlatform.SPIGOT;
        else if (classExists("org.bukkit.craftbukkit.CraftServer") || classExists("org.bukkit.craftbukkit.Main"))
            return ServerPlatform.CRAFTBUKKIT;
        else
            return ServerPlatform.UNKNOWN;
    }

    static boolean classExists(String className) {
        try {
            return Class.forName(className) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
