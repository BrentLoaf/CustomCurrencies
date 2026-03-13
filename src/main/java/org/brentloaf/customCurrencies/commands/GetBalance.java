package org.brentloaf.customCurrencies.commands;

import org.brentloaf.customCurrencies.services.account.Account;
import org.brentloaf.customCurrencies.services.account.AccountService;
import org.brentloaf.customCurrencies.services.currency.Currency;
import org.brentloaf.customCurrencies.services.currency.CurrencyRegistry;
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

public class GetBalance implements CommandExecutor, TabCompleter {

    private int MAXIMUM_ARGS = 1;
    private int MINIMUM_ARGS = 1;

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

        Account account = AccountService.getAccount(player);
        int amount = account.get(currency);
        player.sendMessage(ChatColor.GREEN + "Your balance for " + currency.getName() + " is " + amount + " coins.");
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
        plugin.getCommand("get_balance").setExecutor(new GetBalance());
    }
}