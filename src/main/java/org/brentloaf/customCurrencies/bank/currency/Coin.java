package org.brentloaf.customCurrencies.bank.currency;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public record Coin(NamespacedKey key, ItemStack item) {}
