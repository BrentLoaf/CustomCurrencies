package org.brentloaf.customCurrencies.services.account;

import org.brentloaf.customCurrencies.database.Database;
import org.brentloaf.customCurrencies.services.currency.Currency;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public record Account(UUID ownerId, HashMap<UUID, Integer> balances) {

    public boolean isOwner(Player player) {
        return player.getUniqueId().equals(ownerId);
    }

    public int get(Currency currency) {
        return balances.getOrDefault(currency.getUuid(), 0);
    }

    public void deposit(Player player, Currency currency, int amount) {
        currency.removeAmount(player, amount);

        HashMap<UUID, Integer> balances = Database.AccountQuery.getBalance(player.getUniqueId());

        int currentAmount = balances.getOrDefault(currency.getUuid(), 0);
        balances.put(currency.getUuid(), currentAmount + amount);
        Database.AccountQuery.setBalance(player.getUniqueId(), balances);
    }

    public boolean withdraw(Player player, Currency currency, int amount) {
        boolean wasAdded = currency.giveCoins(player, amount);

        if (wasAdded) {
            HashMap<UUID, Integer> balances = Database.AccountQuery.getBalance(player.getUniqueId());
            balances.computeIfPresent(currency.getUuid(), (k, current) -> current - amount);
            Database.AccountQuery.setBalance(player.getUniqueId(), balances);
            return true;
        } else {
            return false;
        }
    }
}
