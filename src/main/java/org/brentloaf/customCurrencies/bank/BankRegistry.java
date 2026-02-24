package org.brentloaf.customCurrencies.bank;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BankRegistry {

    private static List<Bank> loadedBanks = new ArrayList<>();

    public static List<Bank> getLoadedBanks() {
        return loadedBanks;
    }

    public static void addBank(Bank bank) {
        loadedBanks.add(bank);
    }

    public static boolean hasBank(Player player) {
        return loadedBanks.stream().anyMatch(b -> b.getOwnerUUID().equals(player.getUniqueId()));
    }

    public static Bank getBank(String bankName) {
        return loadedBanks.stream().filter(b -> b.getName().equals(bankName)).toList().getFirst();
    }

    public static Bank getBank(Player player) {
        return loadedBanks.stream().filter(b -> b.getOwnerUUID().equals(player.getUniqueId())).toList().getFirst();
    }
}
