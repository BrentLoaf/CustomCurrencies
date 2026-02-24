package org.brentloaf.customCurrencies.currency;

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
        List<Currency> currencies = loadedCurrencies.stream().filter(c -> c.getName().equals(name)).toList();
        if (currencies.isEmpty()) return null;
        return currencies.getFirst();
    }
}
