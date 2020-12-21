package me._xGQD.commandnpc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class InteractListener implements Listener {
    Main plugin;
    public InteractListener(Main plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event){
        String name = event.getRightClicked().getCustomName();
        if(plugin.npcs.containsKey(name)){
            event.getPlayer().performCommand(plugin.npcs.get(name));
        }
    }
}
