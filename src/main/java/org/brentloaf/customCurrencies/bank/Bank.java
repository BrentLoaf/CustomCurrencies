package org.brentloaf.customCurrencies.bank;

import org.brentloaf.customCurrencies.currency.Currency;
import org.brentloaf.customCurrencies.currency.CurrencyRegistry;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bank {

    private final UUID ownerUUID;
    private String name;
    private List<UUID> ownedCurrencies = new ArrayList<>();

    public Bank(String name, Player owner) {
        this.name = name;
        this.ownerUUID = owner.getUniqueId();
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public String getName() {
        return name;
    }

    public List<Currency> getOwnedCurrencies() {
        return CurrencyRegistry.getLoadedCurrencies().stream().filter(c -> ownedCurrencies.contains(c.getUuid())).toList();
    }

    public void addCurrency(Currency currency) {
        ownedCurrencies.add(currency.getUuid());
    }
}
