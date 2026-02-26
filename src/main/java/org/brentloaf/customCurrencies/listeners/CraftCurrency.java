package org.brentloaf.customCurrencies.listeners;

import org.brentloaf.customCurrencies.CustomCurrencies;
import org.brentloaf.customCurrencies.currency.Currency;
import org.brentloaf.customCurrencies.currency.CurrencyRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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

        ItemStack result = event.getCurrentItem();
        if (result == null) return;

        ClickType clickType = event.getClick();

        if (clickType.isShiftClick()) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            ItemStack itemToCount = currency.getCoinItem();

            ItemStack[] oldContents = player.getInventory().getContents();
            int oldAmount = countItemsInInventory(oldContents, itemToCount);

            Bukkit.getScheduler().runTask(CustomCurrencies.plugin, task -> {
                ItemStack[] newContents = player.getInventory().getContents();
                int newAmount = countItemsInInventory(newContents, itemToCount);

                int totalGained = newAmount - oldAmount;
                currency.setInCirculation(currency.getInCirculation() + totalGained);
            });
        } else currency.setInCirculation(currency.getInCirculation() + 1);
    }

    public int countItemsInInventory(ItemStack[] contents, ItemStack item) {
        int amount = 0;

        for (ItemStack inSlot : contents) {
            if (inSlot == null || inSlot.getType() == Material.AIR) continue;
            if (!inSlot.isSimilar(item)) continue;
            amount += inSlot.getAmount();
        }

        return amount;
    }

    public static void init(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new CraftCurrency(), plugin);
    }
}
