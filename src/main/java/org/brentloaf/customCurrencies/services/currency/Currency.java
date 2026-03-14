package org.brentloaf.customCurrencies.services.currency;

import org.brentloaf.customCurrencies.CustomCurrencies;
import org.brentloaf.customCurrencies.database.Database;
import org.brentloaf.customCurrencies.services.bank.Bank;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public record Currency(UUID ownedBank, UUID id, String name, Material backedMaterial, Material coinMaterial, List<Material> materialIngredients, int inCirculation, HashSet<Vault> vaultLocations) {

    public ItemStack coinItem() {
        ItemStack coinItem = new ItemStack(coinMaterial);

        coinItem.editMeta(meta -> {
            meta.setDisplayName(ChatColor.YELLOW + this.name);
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(CustomCurrencies.plugin, "currency"), PersistentDataType.STRING, id.toString());

            double value = value();
            String materialName = backedMaterial.name().toLowerCase().replace("_", " ");
            String valueString = value <= 0 ? "N/A" : value + " " + materialName + "(s) per coin ";

            meta.setLore(List.of(ChatColor.YELLOW + "Value: " + valueString));
        });

        return coinItem;
    }

    public ShapelessRecipe recipe() {
        ShapelessRecipe recipe = new ShapelessRecipe(craftKey(), coinItem());
        for (Material material : materialIngredients) recipe.addIngredient(material);

        return recipe;
    }

    public String rawName() {
        return name.toLowerCase().replace(" ", "_");
    }

    public NamespacedKey craftKey() {
        return new NamespacedKey(CustomCurrencies.plugin, "craft_" + rawName());
    }

    public boolean hasVault(Location location) {
        return vaultLocations.stream().anyMatch(v -> v.toLocation().equals(location));
    }

    public boolean isOwner(Player player) {
        Bank bank = Database.BankQuery.getFromPlayer(player);
        if (bank == null) return false;
        return bank.isOwner(player);
    }

    public double value() {
        if (inCirculation <= 0) return -1;
        int totalMaterial = 0;

        for (Vault vault : vaultLocations) {
            Integer amount = vault.count(backedMaterial);
            if (amount == null) {
                // TODO: Remove vault from list.
                continue;
            }

            totalMaterial += amount;
        }

        double initValue = (double) totalMaterial / inCirculation;

        return Math.round(initValue * 10.0) / 10.0;
    }

    public void updateItem(ItemStack item) {
        double value = value();
        String materialName = backedMaterial.name().toLowerCase().replace("_", " ");
        String valueString = value == -1 ?
                "Value: N/A" :
                "Value: " + value + " " + materialName + "(s) per coin ";

        item.editMeta(meta -> {
            meta.setLore(List.of(ChatColor.YELLOW + valueString));
        });
    }

    public boolean isCurrency(ItemStack itemStack) {
        if (itemStack == null) return false;

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(CustomCurrencies.plugin, "currency");
        if (!data.has(key)) return false;

        UUID currencyUuid = UUID.fromString(data.get(key, PersistentDataType.STRING));

        return currencyUuid.equals(id);
    }

    public int getAmount(Inventory inventory) {
        ItemStack[] items = inventory.getContents();
        int current = 0;

        for (ItemStack item : items) {
            if (!isCurrency(item)) continue;
            current +=  item.getAmount();
        }

        return current;
    }

    public void removeAmount(Player player, int amount) {
        int remaining = amount;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (!isCurrency(item)) continue;

            int itemAmount = item.getAmount();

            if (itemAmount <= remaining) {
                remaining -= itemAmount;
                item.setAmount(0);
            } else {
                item.setAmount(itemAmount - remaining);
                remaining = 0;
            }

            if (remaining <= 0) break;
        }

        player.updateInventory();
    }

    public boolean giveCoins(Player player, int amount) {
        Inventory inventory = player.getInventory();

        Inventory temp = Bukkit.createInventory(null, 36);
        temp.setStorageContents(inventory.getStorageContents());

        ItemStack coins = coinItem().asQuantity(amount);
        HashMap<Integer, ItemStack> leftOver = temp.addItem(coins);

        if (leftOver.isEmpty()) {
            inventory.addItem(coins);
            return true;
        } else return false;
    }
}
