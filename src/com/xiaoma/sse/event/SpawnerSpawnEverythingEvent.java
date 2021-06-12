package com.xiaoma.sse.event;

import com.xiaoma.sse.copy.ItemStack;
import com.xiaoma.sse.Spawner;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class SpawnerSpawnEverythingEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private final Spawner spawner;

    private final List<ItemStack> items;

    public SpawnerSpawnEverythingEvent(Spawner spawner, List<ItemStack> items) {
        this.spawner = spawner;
        this.items = items;
    }

    private boolean cancel;

    public Spawner getSpawner() {
        return spawner;
    }

    public List<ItemStack> getItems() {
        return items;
    }


    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }
}
