package org.brentloaf.customCurrencies.services.account;

import org.brentloaf.customCurrencies.database.Database;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AccountService {

    public static @NotNull Account getAccount(Player player) {
       return Database.AccountQuery.get(player.getUniqueId());
    }

    private static void addAccount(Player player) {
        Database.AccountQuery.add(player.getUniqueId());
    }
}
