package com.xiaoma.sse.event;

import com.xiaoma.sse.Spawner;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class SpawnerDestroyEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private final Spawner spawner;

    private final ItemStack drop;

    private final Player player;

    public SpawnerDestroyEvent(Spawner spawner, ItemStack drop, Player player) {
        this.spawner = spawner;
        this.drop = drop;
        this.player = player;
    }

    private boolean cancel;

    public Spawner getSpawner() {
        return spawner;
    }

    public ItemStack getDrop() {
        return drop;
    }

    public Player getPlayer() {
        return player;
    }


    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }
}
