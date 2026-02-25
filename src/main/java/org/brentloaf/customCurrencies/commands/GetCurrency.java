package org.brentloaf.customCurrencies.commands;

import org.brentloaf.customCurrencies.currency.Currency;
import org.brentloaf.customCurrencies.currency.CurrencyRegistry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GetCurrency implements CommandExecutor, TabCompleter {

    private final int MAXIMUM_ARGS = 1;
    private final int MINIMUM_ARGS = 1;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return false;
        }

        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return false;
        }

        int argLength = strings.length;
        if (argLength < MINIMUM_ARGS || argLength > MAXIMUM_ARGS) {
            player.sendMessage(ChatColor.RED + "Wrong number of arguments used.");
            return false;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "You do not have enough inventory space.");
            return false;
        }

        String currencyName = strings[0];
        Currency currency = CurrencyRegistry.getFromName(currencyName);
        if (currency == null) {
            player.sendMessage(ChatColor.RED + "The currency " + currencyName + " was not found.");
            return false;
        }

        ItemStack coin = currency.getCoinItem();
        currency.setInCirculation(currency.getInCirculation() + 1);
        player.getInventory().addItem(coin);
        player.sendMessage(ChatColor.GREEN + "You have been given 1 " + currency.getName() + " coin.");
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
        plugin.getCommand("get_currency").setExecutor(new GetCurrency());
    }
}
