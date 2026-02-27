package org.brentloaf.customCurrencies.currency;

import org.brentloaf.customCurrencies.CustomCurrencies;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CurrencyRegistry {

    private static List<Currency> loadedCurrencies = new ArrayList<>();

    public static List<Currency> getLoadedCurrencies() {
        return loadedCurrencies;
    }

    public static void addCurrency(Currency currency) {
        loadedCurrencies.add(currency);
    }

    public static @Nullable Currency getFromUuid(UUID uuid) {
        List<Currency> currencies = loadedCurrencies.stream().filter(c -> c.getUuid().equals(uuid)).toList();
        if (currencies.isEmpty()) return null;
        return currencies.getFirst();
    }

    public static @Nullable Currency getFromName(String name) {
        List<Currency> currencies = loadedCurrencies.stream().filter(c -> c.getRawName().equalsIgnoreCase(name)).toList();
        if (currencies.isEmpty()) return null;
        return currencies.getFirst();
    }

    public static @Nullable Currency getFromCraftKey(NamespacedKey key) {
        List<Currency> currencies = loadedCurrencies.stream().filter(c -> c.getCraftingKey().equals(key)).toList();
        if (currencies.isEmpty()) return null;
        return currencies.getFirst();
    }

    public static @Nullable Currency getFromItem(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(CustomCurrencies.plugin, "currency");

        if (!data.has(key)) return null;
        UUID uuid = UUID.fromString(data.get(key, PersistentDataType.STRING));

        return getFromUuid(uuid);
    }
}
