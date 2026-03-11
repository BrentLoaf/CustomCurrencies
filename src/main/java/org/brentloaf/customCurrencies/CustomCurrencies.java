package org.brentloaf.customCurrencies;

import org.brentloaf.customCurrencies.commands.*;
import org.brentloaf.customCurrencies.database.Database;
import org.brentloaf.customCurrencies.listeners.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class CustomCurrencies extends JavaPlugin {

    public static CustomCurrencies plugin;
    private static Database database;

    @Override
    public void onEnable() {
        plugin = this;

        try {
            database = new Database(plugin.getDataFolder().getAbsolutePath() + "/playerToons.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        CreateBank.init(plugin);
        CreateCurrency.init(plugin);
        GetCurrencyValue.init(plugin);
        GetCurrency.init(plugin);
        AddVault.init(plugin);
        Deposit.init(plugin);
        GetBalance.init(plugin);
        Withdraw.init(plugin);

        RegisterItemUse.init(plugin);
        ItemUseListener.init(plugin);
        RegisterUpdateCurrencyItem.init(plugin);
        RegisterBankVault.init(plugin);
        CraftCurrency.init(plugin);
        RegisterItemDelete.init(plugin);
        ItemDeleteListener.init(plugin);
    }

    @Override
    public void onDisable() {
        try {
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
