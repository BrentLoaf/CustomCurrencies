package org.brentloaf.customCurrencies.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemDeleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final ItemStack item;
    private final int amount;

    public ItemDeleteEvent(ItemStack item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
