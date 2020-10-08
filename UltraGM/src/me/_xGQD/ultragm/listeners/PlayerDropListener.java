package me._xGQD.ultragm.listeners;

import me._xGQD.ultragm.Main;
import me._xGQD.ultragm.gamemodes.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;


public class PlayerDropListener implements Listener {
    Main plugin;
    public PlayerDropListener(Main plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        int map_i = plugin.getMap(player);
        if(map_i != -1){
            Map map = plugin.maps.get(map_i);
            map.onPlayerDrop(event);
            plugin.maps.set(map_i, map);
        }
    }
}
