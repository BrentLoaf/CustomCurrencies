package org.brentloaf.customCurrencies.commands;

import org.brentloaf.customCurrencies.CustomCurrencies;
import org.brentloaf.customCurrencies.bank.Bank;
import org.brentloaf.customCurrencies.bank.BankRegistry;
import org.brentloaf.customCurrencies.bank.currency.Currency;
import org.brentloaf.customCurrencies.listeners.RegisterBankVault;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CreateCurrency implements CommandExecutor {

    private int MAXIMUM_ARGS = 2;
    private int MINIMUM_ARGS = 2;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "This command is only usable by players.");
            return false;
        }

        int argLength = strings.length;
        if (argLength < MINIMUM_ARGS || argLength > MAXIMUM_ARGS) {
            player.sendMessage(ChatColor.RED + "Wrong number of arguments used.");
            return false;
        }

        if (!BankRegistry.hasBank(player)) {
            player.sendMessage(ChatColor.RED + "You must create a bank before creating a currency.");
            return false;
        }

        String materialName = strings[1];
        Material backedMaterial;
        try {
            backedMaterial = Material.valueOf(materialName);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "This isn't a valid material.");
            return false;
        }

        String currencyName = strings[0];
        Bank bank = BankRegistry.getBank(player);
        Currency newCurrency = new Currency(bank, currencyName, backedMaterial);
        bank.addCurrency(newCurrency);
        RegisterBankVault.addCurrencyToListen(player, newCurrency);
        player.sendMessage(ChatColor.GREEN + "You have created the currency " + currencyName + ", please select a vault location by right-clicking a barrel.");
        return true;
    }

    public static void init(JavaPlugin plugin) {
        plugin.getCommand("create_currency").setExecutor(new CreateCurrency());
    }
}
