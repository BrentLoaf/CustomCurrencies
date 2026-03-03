package org.brentloaf.customCurrencies.commands;

import org.brentloaf.customCurrencies.account.Account;
import org.brentloaf.customCurrencies.account.AccountRegistry;
import org.brentloaf.customCurrencies.currency.Currency;
import org.brentloaf.customCurrencies.currency.CurrencyRegistry;
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

public class Deposit implements CommandExecutor, TabCompleter {

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
        Currency currency = CurrencyRegistry.getFromName(currencyName);
        if (currency == null) {
            player.sendMessage(ChatColor.RED + "The currency " + currencyName + " was not found.");
            return false;
        }

        String depoAmountString = strings[1];
        int depoAmount = 0;
        try {
            depoAmount = Integer.parseInt(depoAmountString);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "The amount " + depoAmountString + " is not valid.");
            return false;
        }

        int currentAmount = currency.getAmount(player.getInventory());
        if (currentAmount < depoAmount) {
            player.sendMessage(ChatColor.RED + "You only have " + currentAmount + " to deposit.");
            return false;
        }

        Account account = AccountRegistry.getAccount(player);
        account.deposit(player, currency, depoAmount);
        player.sendMessage(ChatColor.GREEN + "You have deposited " + depoAmount + " " + currency.getName() + "(s).");
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
        plugin.getCommand("deposit").setExecutor(new Deposit());
    }
}
