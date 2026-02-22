package org.brentloaf.customCurrencies.bank;

import org.brentloaf.customCurrencies.bank.currency.Currency;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bank {

    private final UUID ownerUUID;
    private String name;
    private List<Currency> ownedCurrencies = new ArrayList<>();

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
        return ownedCurrencies;
    }

    public void addCurrency(Currency currency) {
        ownedCurrencies.add(currency);
    }
}
