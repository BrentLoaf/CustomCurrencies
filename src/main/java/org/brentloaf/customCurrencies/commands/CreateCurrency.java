package org.brentloaf.customCurrencies.commands;

import org.brentloaf.customCurrencies.bank.Bank;
import org.brentloaf.customCurrencies.bank.BankRegistry;
import org.brentloaf.customCurrencies.currency.Currency;
import org.brentloaf.customCurrencies.currency.CurrencyRegistry;
import org.brentloaf.customCurrencies.listeners.RegisterBankVault;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CreateCurrency implements CommandExecutor, TabCompleter {

    private int MAXIMUM_ARGS = 11;
    private int MINIMUM_ARGS = 3;

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

        String backedMaterialName = strings[1];
        Material backedMaterial;
        try {
            backedMaterial = Material.valueOf(backedMaterialName);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "This isn't a valid backed material.");
            return false;
        }

        List<Material> materialIngredients = new ArrayList<>();
        for (int i = 3; i < strings.length; i++) {
            String ingredientName = strings[i];
            Material ingredientMaterial;

            try {
                ingredientMaterial = Material.valueOf(ingredientName);
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + ingredientName + "  isn't a valid backed material.");
                return false;
            }

            materialIngredients.add(ingredientMaterial);
        }

        String itemMaterialName = strings[2];
        Material itemMaterial;
        try {
            itemMaterial = Material.valueOf(itemMaterialName);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "This isn't a valid coin material.");
            return false;
        }

        String currencyName = strings[0];
        Bank bank = BankRegistry.getBank(player);
        Currency newCurrency = new Currency(bank, currencyName, backedMaterial, itemMaterial, materialIngredients);
        bank.addCurrency(newCurrency);
        CurrencyRegistry.addCurrency(newCurrency);
        RegisterBankVault.addCurrencyToListen(player, newCurrency);
        player.sendMessage(ChatColor.GREEN + "You have created the currency " + currencyName + ", please select a vault location by right-clicking a barrel.");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        int argLength = strings.length;

        return switch (argLength) {
            case 1 -> List.of();
            default -> List.of(Material.values()).stream().filter(m -> m != Material.AIR).map(Material::name).toList();
        };
    }

    public static void init(JavaPlugin plugin) {
        plugin.getCommand("create_currency").setExecutor(new CreateCurrency());
    }
}
