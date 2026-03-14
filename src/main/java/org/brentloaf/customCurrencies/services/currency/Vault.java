package org.brentloaf.customCurrencies.services.currency;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Set;

public record Vault(String world, int x, int y, int z) {

    private static final Gson gson = new Gson();
    private static final Set<Material> valid = Set.of(
            Material.BARREL
    );

    public World worldObject() {
        return Bukkit.getWorld(world);
    }

    public Location toLocation() {
        return new Location(worldObject(), x, y, z);
    }

    public static Vault toVault(Location location) {
        return new Vault(location.getWorld().getName(), (int) location.getX(), (int) location.getY(), (int) location.getZ());
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static @Nullable Vault fromJson(String json) {
        if (json == null || json.isBlank()) return null;
        Type type = new TypeToken<Vault>(){}.getType();

        return gson.fromJson(json, type);
    }

    public @Nullable Inventory toInventory() {
        Block block = toLocation().getBlock();
        if (!valid.contains(block.getType())) return null;

        if (world == null) return null;

        BlockState state = block.getState();

        if (!(state instanceof Barrel barrel)) return null;

        return barrel.getInventory();
    }

    public @Nullable Integer count(Material backedMaterial) {
        Inventory inventory = toInventory();

        if (inventory == null) {
            Bukkit.getLogger().warning("The inventory when counting was returned null.");
            return null;
        }

        int count = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == backedMaterial) {
                count += item.getAmount();
            }
        }

        return count;
    }
}
