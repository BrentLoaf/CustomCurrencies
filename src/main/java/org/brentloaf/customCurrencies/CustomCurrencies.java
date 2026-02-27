package org.brentloaf.customCurrencies;

import org.brentloaf.customCurrencies.commands.*;
import org.brentloaf.customCurrencies.listeners.*;
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
        AddVault.init(plugin);

        RegisterItemUse.init(plugin);
        ItemUseListener.init(plugin);
        RegisterUpdateCurrencyItem.init(plugin);
        RegisterBankVault.init(plugin);
        CraftCurrency.init(plugin);
        RegisterItemDelete.init(plugin);
        ItemDeleteListener.init(plugin);
    }
}
