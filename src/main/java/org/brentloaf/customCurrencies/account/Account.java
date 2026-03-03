package org.brentloaf.customCurrencies.account;

import org.brentloaf.customCurrencies.currency.Currency;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Account {

    private final UUID ownerUuid;
    private HashMap<UUID, Integer> balances = new HashMap<>();

    public Account(Player owner) {
        this.ownerUuid = owner.getUniqueId();
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public HashMap<UUID, Integer> getBalances() {
        return balances;
    }

    public boolean isOwner(Player player) {
        return player.getUniqueId().equals(ownerUuid);
    }

    public int get(Currency currency) {
        return balances.getOrDefault(currency.getUuid(), 0);
    }

    public void deposit(Player player, Currency currency, int amount) {
        int currentAmount = balances.getOrDefault(currency, 0);
        balances.put(currency.getUuid(), currentAmount + amount);
        currency.removeAmount(player, amount);
    }
}
