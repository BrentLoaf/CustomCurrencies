package org.brentloaf.customCurrencies.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ItemDeleteEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final ItemStack item;
    private final int amount;
    private final Class<? extends Event> classType;

    public ItemDeleteEvent(ItemStack item, int amount, Event event) {
        this.item = item;
        this.amount = amount;
        this.classType = event.getClass();
    }

    public ItemStack getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public Class<? extends Event> getClassType() {
        return classType;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
