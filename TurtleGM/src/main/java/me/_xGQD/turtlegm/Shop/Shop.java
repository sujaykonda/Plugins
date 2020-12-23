package me._xGQD.turtlegm.Shop;

import me._xGQD.turtlegm.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Shop {
    protected Main plugin;
    protected HashMap<String, Integer> costs;
    protected Inventory shop;
    public Shop(){
        plugin = JavaPlugin.getPlugin(Main.class);
    }

    public boolean isShop(Inventory inv){
        return inv.equals(shop);
    }
    public int onBuy(Player player, String material, int gold){
        if(costs.get(material) <= gold){
            gold -= costs.get(material);
            return gold;
        }
        return -1;
    }

    public void onInventoryClick(InventoryClickEvent event){ }

    public void open(Player player){
        player.openInventory(shop);
    }
}
