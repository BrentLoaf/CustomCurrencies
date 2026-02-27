package org.brentloaf.customCurrencies.listeners;

import org.brentloaf.customCurrencies.CustomCurrencies;
import org.brentloaf.customCurrencies.currency.Currency;
import org.brentloaf.customCurrencies.currency.CurrencyRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterUpdateCurrencyItem implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Bukkit.getScheduler().runTaskLater(CustomCurrencies.plugin, task -> {
            ItemStack[] items = event.getView().getBottomInventory().getContents();

            for (ItemStack item : items) {
                if (item == null || item.getType() == Material.AIR) continue;

                Currency currency = CurrencyRegistry.getFromItem(item);
                if (currency == null) continue;

                currency.updateItem(item);
            }
        }, 1L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        ItemStack[] items = event.getInventory().getContents();

        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR) continue;

            Currency currency = CurrencyRegistry.getFromItem(item);
            if (currency == null) continue;

            currency.updateItem(item);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();

        Currency currency = CurrencyRegistry.getFromItem(item);
        if (currency == null) return;

        currency.updateItem(item);
    }

    public static void init(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new RegisterUpdateCurrencyItem(), plugin);
    }
}
