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
        int map_i = plugin.getMap(player);
        if(map_i != -1){
            Map map = plugin.maps.get(map_i);
            map.onPlayerInteract(event);
            plugin.maps.set(map_i, map);
        }
        for(int i = 0; i < plugin.shops.size(); i++){
            Shop shop = plugin.shops.get(i);
            shop.onPlayerInteract(event);
            plugin.shops.set(i, shop);
        }
    }
}
