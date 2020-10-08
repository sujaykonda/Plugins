package me._xGQD.ultragm.listeners;

import me._xGQD.ultragm.Main;
import me._xGQD.ultragm.gamemodes.Map;
import me._xGQD.ultragm.shops.CTFShop;
import me._xGQD.ultragm.shops.Shop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {
    Main plugin;
    public InventoryClickListener(Main plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        for(int i = 0; i < plugin.shops.size(); i++){
            Shop shop = plugin.shops.get(i);
            shop.onInventoryClick(event);
            plugin.shops.set(i, shop);
        }
    }
}