package org.brentloaf.customCurrencies.listeners;

import org.brentloaf.customCurrencies.bank.currency.Currency;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class RegisterBankVault implements Listener {

    private static Map<Player, Currency> currenciesToListen = new HashMap<>();

    @EventHandler
    public void onBlockInteractListener(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!currenciesToListen.containsKey(player)) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && player.isSneaking()) return;

        if (event.getHand() != EquipmentSlot.HAND) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock.getType() != Material.BARREL) {
            player.sendMessage(ChatColor.YELLOW + "The block you select as a vault must be a barrel.");
            return;
        }

        Currency currency = currenciesToListen.get(player);
        Location selectedLocation = clickedBlock.getLocation();
        currency.addVault(selectedLocation);
        currenciesToListen.remove(player);

        int x = (int) selectedLocation.x();
        int y = (int) selectedLocation.y();
        int z = (int) selectedLocation.z();
        player.sendMessage(ChatColor.GREEN + "The barrel at " + x + ", " + y + ", " + z + " is now a vault location for " + currency.getName());

        currenciesToListen.remove(player);
    }

    public static void addCurrencyToListen(Player player, Currency currency) {
        currenciesToListen.put(player, currency);
    }

    public static void init(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new RegisterBankVault(), plugin);
    }
}
