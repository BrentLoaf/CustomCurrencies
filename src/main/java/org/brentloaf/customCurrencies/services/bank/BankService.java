package org.brentloaf.customCurrencies.services.bank;

import org.brentloaf.customCurrencies.database.Database;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BankService {

    // private static List<OldBank> loadedBanks = new ArrayList<>();

    public static List<OldBank> getLoadedBanks() {
        return loadedBanks;
    }

    public static void addBank(String name, Player owner) {
        Database.Bank.add(owner.getUniqueId(), name);
    }

    public static boolean hasBank(Player player) {
        return loadedBanks.stream().anyMatch(b -> b.getOwnerUUID().equals(player.getUniqueId()));
    }

    public static OldBank getBank(String bankName) {
        return loadedBanks.stream().filter(b -> b.getName().equals(bankName)).toList().getFirst();
    }

    public static OldBank getBank(Player player) {
        return loadedBanks.stream().filter(b -> b.getOwnerUUID().equals(player.getUniqueId())).toList().getFirst();
    }

    public static boolean nameTaken(String string) {
        return loadedBanks.stream().anyMatch(b -> b.getRawName().equalsIgnoreCase(string.replace(" ", "_")));
    }
}
