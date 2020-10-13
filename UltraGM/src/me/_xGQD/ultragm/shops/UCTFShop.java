package me._xGQD.ultragm.shops;

import me._xGQD.ultragm.Main;
import me._xGQD.ultragm.Utilities.Utilities;
import me._xGQD.ultragm.gamemodes.Map;
import me._xGQD.ultragm.gamemodes.UltCapTheFlag;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UCTFShop extends Shop{
    
    public UCTFShop(Main plugin, String mapType){
        super(plugin, mapType);
        this.plugin = plugin;
        this.mapType = mapType;

        ItemStack gold = Utilities.createItem(Material.GOLD_INGOT, "Gold Perk",
                new String[]{"Start off with 30 gold", "Gets 15 gold on kill instead of 10", "Ultimate Perk"});
        ItemStack rush = Utilities.createItem(Material.POTION, "Rush Perk",
                new String[]{"Gets speed on kill", "Ultimate Perk"});
        ItemStack timewarp = Utilities.createItem(Material.ENDER_PEARL, "Time Warp Perk",
                new String[]{"Allows you to time warp pearl around", "10 second cooldown", "Ultimate Perk"});
        ItemStack kb = Utilities.createItem(Material.IRON_SWORD, "Knockback Perk",
                new String[]{"Knockback 1 on all swords", "Ultimate Perk"});
        kb.addEnchantment(Enchantment.KNOCKBACK, 1);
        ItemStack bow = Utilities.createItem(Material.BOW, "Bow Perk",
                new String[]{"A bow + one arrow every death", "Ultimate Perk"});
        shop.setItem(0, gold);
        shop.setItem(2, rush);
        shop.setItem(4, timewarp);
        shop.setItem(6, kb);
        shop.setItem(8, bow);
    }
    @Override
    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        int map_i = plugin.getMap(player);
        if(map_i != -1){
            Map map = plugin.maps.get(map_i);
            if(map.getType().equals("UCTF") && map instanceof UltCapTheFlag){
            	UltCapTheFlag umap = (UltCapTheFlag) map;
                boolean close = umap.setPerk(player, event.getCurrentItem());
                if(close){
                    event.setCancelled(true);
                    player.closeInventory();
                }
            }
        }
    }
    @Override
    public void onPlayerInteract(PlayerInteractEvent event){ }

}
