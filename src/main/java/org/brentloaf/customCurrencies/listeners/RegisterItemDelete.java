package org.brentloaf.customCurrencies.listeners;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.brentloaf.customCurrencies.events.ItemDeleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class RegisterItemDelete implements Listener {

    @EventHandler
    public void onPlayerBreakItem(PlayerItemBreakEvent event) {
        ItemStack itemStack = event.getBrokenItem();
        int amount = itemStack.getAmount();
        Bukkit.getPluginManager().callEvent(new ItemDeleteEvent(itemStack, amount));
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        if (event.isCancelled()) return;
        ItemStack itemStack = event.getEntity().getItemStack();
        int amount = itemStack.getAmount();
        Bukkit.getPluginManager().callEvent(new ItemDeleteEvent(itemStack, amount));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Item item)) return;

        ItemStack itemStack = item.getItemStack();
        int amount = itemStack.getAmount();
        Bukkit.getPluginManager().callEvent(new ItemDeleteEvent(itemStack, amount));
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Item item)) return;

        ItemStack itemStack = item.getItemStack();
        if (!itemStack.getType().isBurnable()) return;

        int amount = itemStack.getAmount();
        Bukkit.getPluginManager().callEvent(new ItemDeleteEvent(itemStack, amount));
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Item item)) return;

        ItemStack itemStack = item.getItemStack();
        int amount = itemStack.getAmount();
        Bukkit.getPluginManager().callEvent(new ItemDeleteEvent(itemStack, amount));
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveEvent event) {
        if (!(event.getEntity() instanceof Item item)) return;

        ItemStack itemStack = item.getItemStack();
        int amount = itemStack.getAmount();
        Bukkit.getPluginManager().callEvent(new ItemDeleteEvent(itemStack, amount));
    }

    @EventHandler
    public void onEntityRemoveFromWorld(EntityRemoveFromWorldEvent event) {
        if (!(event.getEntity() instanceof Item item)) return;

        ItemStack itemStack = item.getItemStack();
        int amount = itemStack.getAmount();
        Bukkit.getPluginManager().callEvent(new ItemDeleteEvent(itemStack, amount));
    }

    @EventHandler
    public void itemConsumeEvent(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;
        ItemStack itemStack = event.getItem();
        Bukkit.getPluginManager().callEvent(new ItemDeleteEvent(itemStack, 1));
    }

    @EventHandler
    public void blockPlaceEvent(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        ItemStack itemStack = event.getItemInHand();
        Bukkit.getPluginManager().callEvent(new ItemDeleteEvent(itemStack, 1));
    }

    @EventHandler
    public void bucketEmptyEvent(PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) return;
        ItemStack itemStack = event.getItemStack();
        Bukkit.getPluginManager().callEvent(new ItemDeleteEvent(itemStack, 1));
    }

    @EventHandler
    public void bucketFillEvent(PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) return;
        ItemStack itemStack = event.getItemStack();
        Bukkit.getPluginManager().callEvent(new ItemDeleteEvent(itemStack, 1));
    }

    @EventHandler
    public void craftItemEvent(CraftItemEvent event) {
        // event.get
    }

    public static void init(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new RegisterItemDelete(), plugin);
    }
}
