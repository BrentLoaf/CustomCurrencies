package org.brentloaf.customCurrencies.listeners;

import org.brentloaf.customCurrencies.currency.Currency;
import org.brentloaf.customCurrencies.currency.CurrencyRegistry;
import org.brentloaf.customCurrencies.events.ItemUseEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemUseListener implements Listener {

    @EventHandler
    public void onItemUse(ItemUseEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack == null) return;

        Currency currency = CurrencyRegistry.getFromItem(itemStack);
        if (currency == null) return;

        Class<? extends Event> classType = event.getClassType();

        Bukkit.getLogger().info("============Item Use============");
        Bukkit.getLogger().info("Class Trigger: " + classType);

        Player player = event.getPlayer();
        if (!(classType.isAssignableFrom(PlayerInteractEvent.class)) && player != null) player.sendMessage(ChatColor.YELLOW + "You can't do this with a currency.");
        event.setCancelled(true);
    }

    public static void init(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new ItemUseListener(), plugin);
    }
}
