package org.brentloaf.customCurrencies;

import org.brentloaf.customCurrencies.commands.CreateBank;
import org.brentloaf.customCurrencies.commands.CreateCurrency;
import org.brentloaf.customCurrencies.commands.GetCurrency;
import org.brentloaf.customCurrencies.commands.GetCurrencyValue;
import org.brentloaf.customCurrencies.listeners.RegisterBankVault;
import org.bukkit.plugin.java.JavaPlugin;

public final class CustomCurrencies extends JavaPlugin {

    public static CustomCurrencies plugin;

    @Override
    public void onEnable() {
        plugin = this;

        CreateBank.init(plugin);
        CreateCurrency.init(plugin);
        GetCurrencyValue.init(plugin);
        GetCurrency.init(plugin);

        RegisterBankVault.init(plugin);
    }
}
