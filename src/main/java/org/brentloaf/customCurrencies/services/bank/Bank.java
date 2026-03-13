package org.brentloaf.customCurrencies.services.bank;

import org.brentloaf.customCurrencies.database.Database;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public record Bank(String name, UUID ownerId, UUID id, List<UUID> currencies) {

    public boolean isOwner(Player player) {
        return player.getUniqueId().equals(ownerId);
    }

    public String rawName() {
        return name.toLowerCase().replace(" ", "_");
    }

    public void addCurrency(UUID currencyId) {
        Database.BankQuery.addCurrency(id, currencyId);
    }
}
