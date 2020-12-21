package me._xGQD.ultragm.listeners;

import me._xGQD.ultragm.Main;
import me._xGQD.ultragm.gamemodes.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HitListener implements Listener {
    Main plugin;
    public HitListener(Main plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onHit(EntityDamageEvent event){
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(plugin.players.containsKey(player.getUniqueId())){
                int map_i = plugin.players.get(player.getUniqueId());
                Map map = plugin.maps.get(plugin.players.get(player.getUniqueId()));
                if(map.isOpen()){
                    event.setCancelled(true);
                }
                if(map.isStart()){
                    map.onHit(event);
                }
                plugin.maps.set(map_i, map);
            }
        }
    }
}
