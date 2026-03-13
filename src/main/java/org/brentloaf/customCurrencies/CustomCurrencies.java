package org.brentloaf.customCurrencies;

import jdk.jfr.StackTrace;
import org.brentloaf.customCurrencies.commands.*;
import org.brentloaf.customCurrencies.database.Database;
import org.brentloaf.customCurrencies.listeners.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public final class CustomCurrencies extends JavaPlugin {

    public static CustomCurrencies plugin;
    private static Database database;

    @Override
    public void onEnable() {
        plugin = this;

        try {
            File folder = plugin.getDataFolder();
            if (!folder.exists()) folder.mkdirs();

            database = new Database(folder.getAbsolutePath() + "/playerToons.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
