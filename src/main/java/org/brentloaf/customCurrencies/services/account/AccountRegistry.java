package org.brentloaf.customCurrencies.services.account;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class AccountRegistry {

    private static HashSet<Account> loadedAccounts = new HashSet<>();

    public static @NotNull Account getAccount(Player player) {
       return loadedAccounts.stream()
                .filter(a -> a.isOwner(player))
                .findFirst()
                .orElseGet(() -> {
                    Account newAccount = new Account(player);
                    addAccount(newAccount);
                    return newAccount;
                });
    }

    private static void addAccount(Account account) {
        loadedAccounts.add(account);
    }
}
