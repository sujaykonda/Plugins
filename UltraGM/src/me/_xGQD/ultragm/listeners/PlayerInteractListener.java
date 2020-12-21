package me._xGQD.ultragm.listeners;

import me._xGQD.ultragm.Main;
import me._xGQD.ultragm.gamemodes.Map;
import me._xGQD.ultragm.shops.Shop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteractListener implements Listener {
    Main plugin;
    public PlayerInteractListener(Main plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(player.getItemInHand().getType().equals(plugin.wand.getType())){
            plugin.wand.onPlayerInteract(event);
        }
        if(plugin.players.containsKey(player.getUniqueId())){
            int map_i = plugin.players.get(player.getUniqueId());
            Map map = plugin.maps.get(plugin.players.get(player.getUniqueId()));
            map.onPlayerInteract(event);
            plugin.maps.set(map_i, map);
        }
        for(String key : plugin.shops.keySet()){
            Shop shop = plugin.shops.get(key);
            shop.onPlayerInteract(event);
            plugin.shops.put(key, shop);
        }
    }
}
