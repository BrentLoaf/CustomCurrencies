package org.brentloaf.customCurrencies.account;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class AccountRegistry {

    private static HashSet<Account> loadedAccounts = new HashSet<>();

    public static @NotNull Account getAccount(Player player) {
        Account account = loadedAccounts.stream().filter(a -> a.isOwner(player)).toList().getFirst();
        if (account != null) {
            return account;
        } else {
            Account newAccount = new Account(player);
            addAccount(newAccount);
            return newAccount;
        }
    }

    private static void addAccount(Account account) {
        loadedAccounts.add(account);
    }
}
