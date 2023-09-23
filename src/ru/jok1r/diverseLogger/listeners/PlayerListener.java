package ru.jok1r.diverseLogger.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import ru.jok1r.diverseLogger.Main;
import ru.jok1r.diverseLogger.diverse.DroppedData;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final Main plugin;
    private final Map<UUID, DroppedData> dataMap = new HashMap<UUID, DroppedData>();
    private final Map<UUID, DroppedData> killingData = new HashMap<UUID, DroppedData>();

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        if(!event.getDrops().isEmpty()) {
            String player = event.getEntity().getName();
            (new BukkitRunnable() {
                public void run() {
                    long time = System.currentTimeMillis();
                    if(plugin.getServer().getPlayer(player) != null) {
                        List<Entity> data = plugin.getServer().getPlayer(player).getNearbyEntities(5.0D, 5.0D, 5.0D);
                        for (Entity entity : data) {
                            if (entity.getType() == EntityType.DROPPED_ITEM) {
                                Item item = (Item) entity;
                                if (item.getTicksLived() < 10L) {
                                    killingData.put(item.getUniqueId(), new DroppedData(item.getUniqueId(), time, player));
                                }
                            }
                        }
                    }
                }
            }).runTaskLater(this.plugin, 8);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTakeItem(PlayerPickupItemEvent event) {
        if(!event.isCancelled()) {
            ItemStack stack = event.getItem().getItemStack();
            String message;
            UUID uuid = event.getItem().getUniqueId();
            if(this.dataMap.containsKey(uuid)) {
                message = this.plugin.takingItemFromPlayer.replaceAll("%p1", this.dataMap.get(event.getItem().getUniqueId()).getPlayer());
                double time = (System.currentTimeMillis() - this.dataMap.get(event.getItem().getUniqueId()).getDroppedTime()) / 1000.0D;
                message = message.replaceAll("%t", time + "");
                this.dataMap.remove(uuid);
            } else if(this.killingData.containsKey(uuid)){
                message = this.plugin.killingItem.replaceAll("%p1", this.killingData.get(uuid).getPlayer());
                this.killingData.remove(uuid);
            } else {
                message = this.plugin.takingItem;
            }

            if(stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
                message = message.replaceAll("%n", stack.getItemMeta().getDisplayName());
            } else {
                message = message.replaceAll("%n", stack.getTypeId() + "");
            }

            message = message.replaceAll("%u", uuid.toString()).replaceAll("%i", stack.getAmount() + "");
            message = message.replaceAll("%p", event.getPlayer().getName());
            message = message.replaceAll("%d", this.plugin.getTime());
            message = message.replaceAll("%x", String.valueOf((int) event.getPlayer().getLocation().getX()));
            message = message.replaceAll("%y", String.valueOf((int) event.getPlayer().getLocation().getY()));
            message = message.replaceAll("%z", String.valueOf((int) event.getPlayer().getLocation().getZ()));
            try {
                this.plugin.logging(message);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDropItem(PlayerDropItemEvent event) {
        if(!event.isCancelled()) {
            this.dataMap.put(event.getItemDrop().getUniqueId(), new DroppedData(event.getItemDrop().getUniqueId(), System.currentTimeMillis(), event.getPlayer().getName()));
            String message = this.plugin.droppingItem.replaceAll("%p", event.getPlayer().getName());
            ItemStack stack = event.getItemDrop().getItemStack();
            if(stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
                message = message.replaceAll("%n", stack.getItemMeta().getDisplayName());
            } else {
                message = message.replaceAll("%n", stack.getTypeId() + "");
            }
            message = message.replaceAll("%u", event.getItemDrop().getUniqueId() + "").replaceAll("%i", stack.getAmount() + "");;
            message = message.replaceAll("%d", this.plugin.getTime());
            message = message.replaceAll("%x", String.valueOf((int) event.getPlayer().getLocation().getX()));
            message = message.replaceAll("%y", String.valueOf((int) event.getPlayer().getLocation().getY()));
            message = message.replaceAll("%z", String.valueOf((int) event.getPlayer().getLocation().getZ()));
            try {
                this.plugin.logging(message);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
