package ru.jok1r.diverseLogger.listeners;

import noppes.npcs.side.server.bukkit.listeners.NPCDropEvent;
import noppes.npcs.side.server.bukkit.listeners.NPCKillingEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.jok1r.diverseLogger.Main;

import java.io.IOException;

public class NPCListener implements Listener {

    private final Main plugin;

    public NPCListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNpc(NPCDropEvent.Post event) {
        if(!event.getDrop().hasItemMeta()) {
            return;
        }

        String message = this.plugin.npcDrop.replaceAll("%d", this.plugin.getTime());
        message = message.replaceAll("%p", event.getPlayer());
        message = message.replaceAll("%i", event.getDrop().getItemMeta().getDisplayName());
        try {
            this.plugin.logging(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @EventHandler
    public void onNpcKill(NPCKillingEvent event) {
        String message = this.plugin.npcKilling.replaceAll("%d",  this.plugin.getTime());
        message = message.replaceAll("%p", event.getPlayer().getName());
        message = message.replaceAll("%npc", event.getNpc());
        try {
            this.plugin.logging(message);
            this.plugin.saveKilledData(event.getNpc(), event.getPlayer().getName());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
