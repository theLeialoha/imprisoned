package dev.leialoha.imprisoned.compat;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

public enum Compatabilities {
    
    WORLD_GUARD("WorldGuard", "WorldGuardCompat");

    private final Compatability instance;

    Compatabilities(String pluginName, String compatClass) {
        final PluginManager manager = Bukkit.getPluginManager();
        final boolean exists = manager.getPlugin(pluginName) != null;

        this.instance = (!exists)
            ? new Compatability() {}
            : getInstance(compatClass);
    }

    private Compatability getInstance(String compatClass) {
        try {
            String packageName = Compatabilities.class.getPackageName();
            Class<?> clazz = Class.forName(packageName + "." + compatClass);

            if (Compatability.class.isAssignableFrom(clazz)) {
                Constructor<?> constructor = clazz.getConstructor();
                return (Compatability) constructor.newInstance();
            }
        } catch (Exception ex) {}

        return new Compatability() {};
    }

    public void register(Server server) {
        this.instance.register(server);
    }

}
