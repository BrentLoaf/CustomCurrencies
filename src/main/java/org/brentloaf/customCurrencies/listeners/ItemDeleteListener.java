package org.brentloaf.customCurrencies.listeners;

import org.brentloaf.customCurrencies.currency.Currency;
import org.brentloaf.customCurrencies.currency.CurrencyRegistry;
import org.brentloaf.customCurrencies.events.ItemDeleteEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemDeleteListener implements Listener {

    @EventHandler
    public void onItemDelete(ItemDeleteEvent event) {
        ItemStack itemDeleted = event.getItem();

        Currency currency = CurrencyRegistry.getFromItem(itemDeleted);
        if (currency == null) return;

        int amountDeleted = event.getAmount();
        currency.setInCirculation(currency.getInCirculation() - amountDeleted);
    }

    public static void init(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new ItemDeleteListener(), plugin);
    }
}
