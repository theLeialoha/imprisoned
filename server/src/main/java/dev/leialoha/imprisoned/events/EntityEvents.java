package dev.leialoha.imprisoned.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;

import dev.leialoha.imprisoned.utils.EntityUtils;

public class EntityEvents implements Listener {
    
    @EventHandler
    public void onEntityRemove(EntityRemoveFromWorldEvent event) {
        EntityUtils.clearMetadata(event.getEntity());
    }

}
