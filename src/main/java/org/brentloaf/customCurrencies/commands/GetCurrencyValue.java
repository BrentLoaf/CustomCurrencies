package org.brentloaf.customCurrencies.commands;

import org.brentloaf.customCurrencies.bank.Bank;
import org.brentloaf.customCurrencies.bank.BankRegistry;
import org.brentloaf.customCurrencies.currency.Currency;
import org.brentloaf.customCurrencies.currency.CurrencyRegistry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GetCurrencyValue implements CommandExecutor, TabCompleter {

    private int MAXIMUM_ARGS = 1;
    private int MINIMUM_ARGS = 1;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        int argLength = strings.length;
        if (argLength < MINIMUM_ARGS || argLength > MAXIMUM_ARGS) {
            commandSender.sendMessage("Wrong number of arguments used.");
            return false;
        }

        String currencyName = strings[0];
        Currency currency = CurrencyRegistry.getFromName(currencyName);
        if (currency == null) {
            commandSender.sendMessage(ChatColor.RED + "The currency " + currencyName + " was not found.");
            return false;
        }

        double value = currency.getValue();
        if (value == -1) {
            commandSender.sendMessage(ChatColor.YELLOW + "No value of " + currencyName + " was found.");
            return false;
        }

        String materialName = currency.getBackedMaterial().name().toLowerCase().replace("_", " ") + "s";
        commandSender.sendMessage(ChatColor.GREEN + "The value of " + currency.getName() + " is " + value + " " + materialName + " per coin.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        int argLength = strings.length;

        return switch (argLength) {
            case 1 -> CurrencyRegistry.getLoadedCurrencies().stream().map(Currency::getRawName).toList();
            default -> List.of();
        };
    }

    public static void init(JavaPlugin plugin) {
        plugin.getCommand("get_currency_value").setExecutor(new GetCurrencyValue());
    }
}
