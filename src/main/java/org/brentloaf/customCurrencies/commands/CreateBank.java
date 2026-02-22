package org.brentloaf.customCurrencies.commands;

import org.brentloaf.customCurrencies.bank.Bank;
import org.brentloaf.customCurrencies.bank.BankRegistry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CreateBank implements CommandExecutor {

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

        if (BankRegistry.hasBank(player)) {
            player.sendMessage(ChatColor.RED + "You can only own a single bank at a time.");
            return false;
        }

        String bankName = strings[0];
        Bank bank = new Bank(bankName, player);
        BankRegistry.addBank(bank);
        player.sendMessage(ChatColor.GREEN + "You have created the bank " + bankName + ".");
        return true;
    }

    public static void init(JavaPlugin plugin) {
        plugin.getCommand("create_bank").setExecutor(new CreateBank());
    }
}
