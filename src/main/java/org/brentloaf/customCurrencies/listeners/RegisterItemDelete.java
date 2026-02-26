package org.brentloaf.customCurrencies.listeners;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.brentloaf.customCurrencies.events.ItemDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterItemDelete implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        if (!(event.getEntity() instanceof Item item)) return;

        ItemStack itemStack = item.getItemStack();
        int amount = itemStack.getAmount();
        Bukkit.getPluginManager().callEvent(new ItemDeleteEvent(itemStack, amount, event));
    }

    public static void init(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new RegisterItemDelete(), plugin);
    }
}
