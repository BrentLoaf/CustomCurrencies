package org.brentloaf.customCurrencies.commands;

import org.brentloaf.customCurrencies.services.account.Account;
import org.brentloaf.customCurrencies.services.account.AccountService;
import org.brentloaf.customCurrencies.services.currency.Currency;
import org.brentloaf.customCurrencies.services.currency.CurrencyService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Withdraw implements CommandExecutor, TabCompleter {

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

        String currencyName = strings[0];
        Currency currency = CurrencyService.getFromName(currencyName);
        if (currency == null) {
            player.sendMessage(ChatColor.RED + "The currency " + currencyName + " was not found.");
            return false;
        }

        String withAmountString = strings[1];
        int withAmount = 0;
        try {
            withAmount = Integer.parseInt(withAmountString);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "The amount " + withAmountString + " is not valid.");
            return false;
        }

        Account account = AccountService.getAccount(player);
        int currentAmount = account.get(currency);
        if (currentAmount < withAmount) {
            player.sendMessage(ChatColor.RED + "You only have " + currentAmount + " to withdraw.");
            return false;
        }

        boolean wasAdded = account.withdraw(player, currency, withAmount);
        if (wasAdded) {
            player.sendMessage(ChatColor.GREEN  + "You have withdrawn " + withAmount + " " + currency.getName() + "(s)");
            return true;
        } else {
            player.sendMessage(ChatColor.RED  + "You don't have enough inventory space to withdraw " + withAmount + " " + currency.getName() + "(s)");
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        int argLength = strings.length;

        return switch (argLength) {
            case 1 -> CurrencyService.getLoadedCurrencies().stream().map(Currency::getRawName).toList();
            default -> List.of();
        };
    }

    public static void init(JavaPlugin plugin) {
        plugin.getCommand("withdraw").setExecutor(new Withdraw());
    }
}
