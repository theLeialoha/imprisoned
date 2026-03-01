package dev.leialoha.imprisoned.utils;

import java.util.stream.Stream;

public enum EnvironmentPlatform {

    // Client
    FORGE("net.minecraftforge.fml.common.Mod"),
    NEOFORGE("net.neoforged.fml.common.Mod"),
    FABRIC("net.fabricmc.api.ModInitializer"),
    QUILT("org.quiltmc.qsl.base.api.entrypoint.ModInitializer"),

    // Server
    GLOWSTONE("net.glowstone.GlowServer"),
    PAPER("co.aikar.timings.Timings"),
    SPIGOT("org.spigotmc.SpigotConfig"),
    CRAFTBUKKIT("org.bukkit.craftbukkit.CraftServer", "org.bukkit.craftbukkit.Main"),

    // I don't know how we are loaded
    UNKNOWN;
    
    private final boolean exists;

    EnvironmentPlatform(String ...classes) {
        this.exists = Stream.of(classes)
            .anyMatch(EnvironmentPlatform::classExists);
    }

    public static EnvironmentPlatform get() {
        // Forks need to be checked first

        if (NEOFORGE.exists) return NEOFORGE;
        if (FORGE.exists) return FORGE;
        if (QUILT.exists) return QUILT;
        if (FABRIC.exists) return FABRIC;

        if (GLOWSTONE.exists) return GLOWSTONE;
        if (PAPER.exists) return PAPER;
        if (SPIGOT.exists) return SPIGOT;
        if (CRAFTBUKKIT.exists) return CRAFTBUKKIT;

        return UNKNOWN;
    }

    static boolean classExists(String className) {
        try {
            return Class.forName(className) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
