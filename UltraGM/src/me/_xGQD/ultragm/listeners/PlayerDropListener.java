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
        if(plugin.players.containsKey(player.getUniqueId())){
            int map_i = plugin.players.get(player.getUniqueId());
            Map map = plugin.maps.get(plugin.players.get(player.getUniqueId()));
            map.onPlayerDrop(event);
            plugin.maps.set(map_i, map);
        }
    }
}
