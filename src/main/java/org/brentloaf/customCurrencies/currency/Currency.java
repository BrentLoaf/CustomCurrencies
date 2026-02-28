package org.brentloaf.customCurrencies.currency;

import org.brentloaf.customCurrencies.CustomCurrencies;
import org.brentloaf.customCurrencies.bank.Bank;
import org.bukkit.*;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class Currency {

    private final Bank ownedBank;

    private UUID uuid;
    private String name;
    private Material backedMaterial;
    private int inCirculation;
    private NamespacedKey craftingKey;
    private ItemStack coinItem;
    private HashSet<Location> vaultLocations = new HashSet<>();

    public Currency(Bank ownedBank, String name,  Material backedMaterial, Material coinMaterial, List<Material> materialIngredients) {
        this.ownedBank = ownedBank;
        this.uuid = UUID.randomUUID();
        this.name = name.replace("_", " ");
        this.backedMaterial = backedMaterial;
        this.coinItem = new ItemStack(coinMaterial);
        this.coinItem.editMeta(meta -> {
            meta.setDisplayName(ChatColor.YELLOW + this.name);
            PersistentDataContainer data = meta.getPersistentDataContainer();
            data.set(new NamespacedKey(CustomCurrencies.plugin, "currency"), PersistentDataType.STRING, uuid.toString());

            meta.setLore(List.of(ChatColor.YELLOW + "Value: N/A"));
        });

        this.craftingKey = new NamespacedKey(CustomCurrencies.plugin, "craft_" + getRawName());

        ShapelessRecipe recipe = new ShapelessRecipe(this.craftingKey, this.coinItem);
        for (Material material : materialIngredients) recipe.addIngredient(material);

        Bukkit.addRecipe(recipe);
    }

    public Bank getOwnedBank() {
        return ownedBank;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getRawName() {
        return name.toLowerCase().replace(" ", "_");
    }

    public Material getBackedMaterial() {
        return backedMaterial;
    }

    public int getInCirculation() {
        return inCirculation;
    }

    public ItemStack getCoinItem() {
        return coinItem;
    }

    public NamespacedKey getCraftingKey() {
        return craftingKey;
    }

    public HashSet<Location> getVaultLocations() {
        return vaultLocations;
    }

    public void setInCirculation(int inCirculation) {
        this.inCirculation = inCirculation;
    }

    public void addVault(Location vaultLocation) {
        vaultLocations.add(vaultLocation);
    }

    public boolean hasVault(Location location) {
        return vaultLocations.contains(location);
    }

    public boolean isOwner(Player player) {
        return ownedBank.isOwner(player);
    }

    public double getValue() {
        if (inCirculation <= 0) return -1;
        int totalMaterial = 0;

        for (Location vaultLocation : vaultLocations) {
            Inventory vaultInventory = getVaultInventory(vaultLocation);
            if (vaultInventory == null) {
                vaultLocations.remove(vaultInventory);
                Bukkit.getLogger().warning("The vault at: " + vaultLocation.toString() + " was removed due to a null return.");
                continue;
            }

            totalMaterial += countMaterialInInventory(vaultInventory);


        }

        double initValue = (double) totalMaterial / inCirculation;

        return Math.round(initValue * 10.0) / 10.0;
    }

    private Inventory getVaultInventory(Location location) {
        if (location == null) return null;

        World world = location.getWorld();
        if (world == null) return null;

        Block block = location.getBlock();

        if (block.getType() != Material.BARREL) return null;

        BlockState state = block.getState();

        if (!(state instanceof Barrel barrel)) return null;

        return barrel.getInventory();
    }

    private int countMaterialInInventory(Inventory inventory) {
        if (inventory == null) {
            Bukkit.getLogger().warning("The inventory when counting was returned null.");
            return 0;
        }

        int count = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == backedMaterial) {
                count += item.getAmount();
            }
        }

        return count;
    }

    public void updateItem(ItemStack item) {
        double value = getValue();
        String materialName = backedMaterial.name().toLowerCase().replace("_", " ");
        String valueString = value == -1 ?
                "Value: N/A" :
                "Value: " + value + " " + materialName + "(s) per coin ";

        item.editMeta(meta -> {
            meta.setLore(List.of(ChatColor.YELLOW + valueString));
        });
    }

    public boolean isCurrency(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(CustomCurrencies.plugin, "currency");
        if (!data.has(key)) return false;

        UUID currencyUuid = UUID.fromString(data.get(key, PersistentDataType.STRING));

        return currencyUuid.equals(uuid);
    }
}
