package org.brentloaf.customCurrencies.commands;

import org.brentloaf.customCurrencies.currency.Currency;
import org.brentloaf.customCurrencies.currency.CurrencyRegistry;
import org.brentloaf.customCurrencies.listeners.RegisterBankVault;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AddVault implements CommandExecutor, TabCompleter {

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
            player.sendMessage("Wrong number of arguments used.");
            return false;
        }

        String currencyName = strings[0];
        Currency currency = CurrencyRegistry.getFromName(currencyName);
        if (currency == null) {
            player.sendMessage(ChatColor.RED + "The currency " + currencyName + " was not found.");
            return false;
        }

        player.sendMessage(ChatColor.GREEN + "Right-click shift a barrel to make it a new vault for " + currency.getName() + ".");
        RegisterBankVault.addCurrencyToListen(player, currency);
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
        plugin.getCommand("add_vault").setExecutor(new AddVault());
    }
}