package org.brentloaf.customCurrencies.bank.currency;

import org.brentloaf.customCurrencies.bank.Bank;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class Currency {

    private final Bank ownedBank;

    private String name;
    private Material backedMaterial;
    private int inCirculation;
    private Coin coin;
    private List<Location> vaultLocations = new ArrayList<>();

    public Currency(Bank ownedBank, String name,  Material backedMaterial) {
        this.ownedBank = ownedBank;
        this.name = name;
        this.backedMaterial = backedMaterial;
    }

    public Bank getOwnedBank() {
        return ownedBank;
    }

    public String getName() {
        return name;
    }

    public Material getBackedMaterial() {
        return backedMaterial;
    }

    public int getInCirculation() {
        return inCirculation;
    }

    public Coin getCoin() {
        return coin;
    }

    public List<Location> getVaultLocations() {
        return vaultLocations;
    }

    public void addVault(Location vaultLocation) {
        vaultLocations.add(vaultLocation);
    }
}
