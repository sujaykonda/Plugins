package me._xGQD.ultragm.shops;

import me._xGQD.ultragm.Main;
import me._xGQD.ultragm.gamemodes.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Shop {
    Main plugin;
    Inventory shop;
    String mapType;

    public Shop(Main plugin, String mapType){
        this.plugin = plugin;
        this.mapType = mapType;
    }
    public boolean useShop(Map map){
        return map.getType().equals(mapType);
    }
    public boolean isShopName(String name){
        return(shop.getName() == name);
    }
    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        int map_i = plugin.getMap(player);
        if(map_i != -1){
            Map map = plugin.maps.get(map_i);
            boolean close = map.buy(player, event.getCurrentItem());
            if(close){
                event.setCancelled(true);
            }
        }
        player.closeInventory();
    }
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        int map_i = plugin.getMap(player);
        if(map_i != -1){
            if(player.getItemInHand().getType().equals(Material.NETHER_STAR)){
                player.openInventory(shop);
            }
        }
    }

}
