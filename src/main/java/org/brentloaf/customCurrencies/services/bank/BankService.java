package org.brentloaf.customCurrencies.services.bank;

import org.brentloaf.customCurrencies.database.Database;
import org.bukkit.entity.Player;

public class BankService {

    public static void addBank(String name, Player owner) {
        Database.BankQuery.add(owner.getUniqueId(), name);
    }

    public static boolean hasBank(Player player) {
        return Database.BankQuery.getFromPlayer(player) != null;
    }

    public static Bank getBank(String bankName) {
        return Database.BankQuery.getFromName(bankName);
    }

    public static Bank getBank(Player player) {
        return Database.BankQuery.getFromPlayer(player);
    }

    public static boolean nameTaken(String bankName) {
        return Database.BankQuery.getFromName(bankName) != null;
    }
}
