package org.brentloaf.customCurrencies.listeners;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.brentloaf.customCurrencies.currency.Currency;
import org.brentloaf.customCurrencies.currency.CurrencyRegistry;
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

        if (!player.isSneaking()) {
            player.sendMessage(ChatColor.GREEN + "You have stopped selecting vaults.");
            currenciesToListen.remove(player);
            event.setCancelled(true);
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getHand() != EquipmentSlot.HAND) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() != Material.BARREL) {
            player.sendMessage(ChatColor.YELLOW + "The block you select as a vault must be a barrel.");
            return;
        }

        Location selectedLocation = clickedBlock.getLocation();
        if (CurrencyRegistry.hasVault(selectedLocation)) {
            player.sendMessage(ChatColor.YELLOW + "Vault locations can only be selected once.");
            return;
        }

        Currency currency = currenciesToListen.get(player);
        currency.addVault(selectedLocation);
        player.sendActionBar(Component.text(ChatColor.GREEN + "If you wish to stop selecting vaults, you can right-click a barrel without shifting."));

        int x = (int) selectedLocation.x();
        int y = (int) selectedLocation.y();
        int z = (int) selectedLocation.z();
        player.sendMessage(ChatColor.GREEN + "The barrel at " + x + ", " + y + ", " + z + " is now a vault location for " + currency.getName());

        event.setCancelled(true);
    }

    public static void addCurrencyToListen(Player player, Currency currency) {
        currenciesToListen.put(player, currency);
    }

    public static void init(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new RegisterBankVault(), plugin);
    }
}
