package dev.leialoha.imprisoned.job;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;

import dev.leialoha.imprisoned.mines.destruction.DestructionHandler;
import dev.leialoha.imprisoned.registration.RegistrationProvider;
import dev.leialoha.imprisoned.registration.Registry;
import dev.leialoha.imprisoned.registration.RegistryEntry;
import dev.leialoha.imprisoned.registration.RegistryKeys;

public class Tickables implements Listener {
    
    final Registry<Tickable> TICKABLES = RegistryKeys.TICKABLES.getRegistry();

    public static void init() {
        RegistrationProvider<Tickable> provider = RegistrationProvider.of(RegistryKeys.TICKABLES, "imprisoned");

        provider.register("tickable_players", PlayerTickable.INSTANCE);
        provider.register("destruction_handler", new DestructionHandler());
    }


    @EventHandler
    public void onTick(ServerTickStartEvent event) {
        TICKABLES.getEntries().stream()
            .map(RegistryEntry::get)
            .forEach(Tickable::onTick);
    }
}
