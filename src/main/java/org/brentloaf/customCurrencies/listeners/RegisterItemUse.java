package org.brentloaf.customCurrencies.listeners;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import org.brentloaf.customCurrencies.events.ItemUseEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class RegisterItemUse implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        ItemUseEvent useEvent = new ItemUseEvent(player, itemStack, event);
        Bukkit.getPluginManager().callEvent(useEvent);
        event.setCancelled(useEvent.isCancelled());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlaceBlock(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItemInHand();
        ItemUseEvent useEvent = new ItemUseEvent(player, itemStack, event);
        Bukkit.getPluginManager().callEvent(useEvent);
        event.setCancelled(useEvent.isCancelled());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onConsumeItem(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        ItemUseEvent useEvent = new ItemUseEvent(player, itemStack, event);
        Bukkit.getPluginManager().callEvent(useEvent);
        event.setCancelled(useEvent.isCancelled());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItem(event.getHand());
        ItemUseEvent useEvent = new ItemUseEvent(player, itemStack, event);
        Bukkit.getPluginManager().callEvent(useEvent);
        event.setCancelled(useEvent.isCancelled());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItem(event.getHand());
        ItemUseEvent useEvent = new ItemUseEvent(player, itemStack, event);
        Bukkit.getPluginManager().callEvent(useEvent);
        event.setCancelled(useEvent.isCancelled());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLaunchProjectile(PlayerLaunchProjectileEvent event) {
        if (event.isCancelled()) return;

        Entity projectile = event.getProjectile();
        ItemStack itemStack = null;

        if (projectile instanceof ThrowableProjectile throwable) itemStack = throwable.getItem();
        if (itemStack == null) return;

        Player player = event.getPlayer();
        ItemUseEvent useEvent = new ItemUseEvent(player, itemStack, event);
        Bukkit.getPluginManager().callEvent(useEvent);
        event.setCancelled(useEvent.isCancelled());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onShootArrow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.isCancelled()) return;

        Entity projectile = event.getProjectile();
        ItemStack itemStack = null;

        if (projectile instanceof Arrow arrow) itemStack = arrow.getItemStack();
        if (itemStack == null) return;


        ItemUseEvent useEvent = new ItemUseEvent(player, itemStack, event);
        Bukkit.getPluginManager().callEvent(useEvent);

        boolean shouldCancel = useEvent.isCancelled();
        event.setCancelled(shouldCancel);

        if (shouldCancel) {
            player.getInventory().addItem(itemStack);
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCraftItem(CraftItemEvent event) {
        if (event.isCancelled()) return;
        Player player = (Player) event.getWhoClicked();

        CraftingInventory inventory = event.getInventory();

        ItemStack[] itemsUsed = inventory.getMatrix();

        for (ItemStack itemStack : itemsUsed) {
            ItemUseEvent useEvent = new ItemUseEvent(player, itemStack, event);
            Bukkit.getPluginManager().callEvent(useEvent);
            if (useEvent.isCancelled()) {
                event.setCancelled(useEvent.isCancelled());
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBurnItem(FurnaceBurnEvent event) {
        if (event.isCancelled()) return;

        List<ItemStack> items = new ArrayList<>();

        items.add(event.getFuel());
        if (event.getBlock().getState() instanceof Furnace furnace) items.add(furnace.getInventory().getSmelting());

        for (ItemStack itemStack : items) {
            ItemUseEvent useEvent = new ItemUseEvent(null, itemStack, event);
            Bukkit.getPluginManager().callEvent(useEvent);
            if (useEvent.isCancelled()) {
                event.setCancelled(useEvent.isCancelled());
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSmeltItem(FurnaceSmeltEvent event) {
        if (event.isCancelled()) return;

        ItemStack itemStack = event.getSource();
        ItemUseEvent useEvent = new ItemUseEvent(null, itemStack, event);
        Bukkit.getPluginManager().callEvent(useEvent);
        event.setCancelled(useEvent.isCancelled());
    }

    private static final List<Class<? extends Inventory>> ILLEGAL_INVENTORIES = List.of(
            FurnaceInventory.class,
            BrewerInventory.class,
            GrindstoneInventory.class,
            StonecutterInventory.class,
            SmithingInventory.class,
            AnvilInventory.class,
            EnchantingInventory.class,
            CraftingInventory.class
    );

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled()) return;

        Inventory clicked = event.getClickedInventory();
        if (clicked == null) return;

        boolean isIllegal = ILLEGAL_INVENTORIES.stream().anyMatch(cls -> cls.isInstance(clicked));
        if (!isIllegal) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCursor();
        ItemUseEvent useEvent = new ItemUseEvent(player, itemStack, event);
        Bukkit.getPluginManager().callEvent(useEvent);
        event.setCancelled(useEvent.isCancelled());
    }

    public static void init(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new RegisterItemUse(), plugin);
    }
}
