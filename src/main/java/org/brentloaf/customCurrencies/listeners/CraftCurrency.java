package org.brentloaf.customCurrencies.listeners;

import org.brentloaf.customCurrencies.CustomCurrencies;
import org.brentloaf.customCurrencies.currency.Currency;
import org.brentloaf.customCurrencies.currency.CurrencyRegistry;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftCurrency implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCraftItem(CraftItemEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getRecipe() instanceof Keyed keyed)) return;
        NamespacedKey craftingKey = keyed.getKey();

        Currency currency = CurrencyRegistry.getFromCraftKey(craftingKey);
        if (currency == null) return;

        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!currency.isOwner(player)) {
            player.sendMessage(ChatColor.YELLOW + "You can only craft currencies you own.");
            event.setCancelled(true);
            return;
        }

        ClickType clickType = event.getClick();
        if (clickType.isCreativeAction()) {
            event.setCancelled(true);
            return;
        }

        if (clickType.isShiftClick()) {
            ItemStack[] oldContents = player.getInventory().getContents();
            int oldAmount = countItemsInInventory(oldContents, currency);

            Bukkit.getScheduler().runTask(CustomCurrencies.plugin, task -> {
                ItemStack[] newContents = player.getInventory().getContents();
                int newAmount = countItemsInInventory(newContents, currency);

                int totalGained = newAmount - oldAmount;
                currency.setInCirculation(currency.getInCirculation() + totalGained);
            });
        } else currency.setInCirculation(currency.getInCirculation() + 1);
    }

    public int countItemsInInventory(ItemStack[] contents, Currency currency) {
        int amount = 0;

        for (ItemStack inSlot : contents) {
            if (inSlot == null || inSlot.getType() == Material.AIR) continue;
            if (!currency.isCurrency(inSlot)) continue;
            amount += inSlot.getAmount();
        }

        return amount;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCraftItem(CrafterCraftEvent event) {
        Keyed keyed = event.getRecipe();
        NamespacedKey craftingKey = keyed.getKey();

        Currency currency = CurrencyRegistry.getFromCraftKey(craftingKey);
        if (currency != null) event.setCancelled(true);
    }

    public static void init(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new CraftCurrency(), plugin);
    }
}
