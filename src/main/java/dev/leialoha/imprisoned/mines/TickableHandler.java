package dev.leialoha.imprisoned.mines;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;

public class TickableHandler implements Listener {

    private static final Set<Tickable> TICKABLES = new HashSet<>();

    protected static boolean add(Tickable tickable) {
        return TICKABLES.add(tickable);
    }

    protected static boolean remove(Tickable tickable) {
        return TICKABLES.remove(tickable);
    }

    protected static boolean has(Tickable tickable) {
        return TICKABLES.contains(tickable);
    }

    protected static List<Tickable> removeIf(Predicate<? super Tickable> filter) {
        List<Tickable> removed = List.copyOf(TICKABLES).stream()
            .filter(filter).filter(TickableHandler::notPlayerTickable).toList();

        TICKABLES.removeAll(removed);

        return removed;
    }

    protected static List<Tickable> clear() {
        return removeIf(TickableHandler::notPlayerTickable);
    }

    private static boolean notPlayerTickable(Tickable tickable) {
        return !(tickable instanceof PlayerTickable);
    }

    @EventHandler
    public void onTick(ServerTickStartEvent event) {
        List.copyOf(TICKABLES)
            .forEach(Tickable::onTick);
    }

    static {
        PlayerTickable.INSTANCE.startTicking();
    }

}
