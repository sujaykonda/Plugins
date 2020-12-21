package me._xGQD.ultragm.shops;

import me._xGQD.ultragm.Main;
import me._xGQD.ultragm.Utilities.Utilities;
import me._xGQD.ultragm.gamemodes.Map;
import me._xGQD.ultragm.gamemodes.TeamDeathMatchMap;
import me._xGQD.ultragm.gamemodes.UltCapTheFlag;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TDMShop extends Shop{
    public TDMShop(Main plugin, String mapType) {
        super(plugin, mapType);
        ItemStack diamondsword = Utilities.createItem(Material.DIAMOND_SWORD, "Diamond Sword",
                new String[]{"Temporary Diamond Sword for until you die", "Cost 25 Gold"});
        ItemStack diamondhelmet = Utilities.createItem(Material.DIAMOND_HELMET, "Diamond Helmet",
                new String[]{"Temporary Diamond Helmet for until you die", "Cost 25 Gold"});
        ItemStack diamondboots = Utilities.createItem(Material.DIAMOND_BOOTS, "Diamond Boots",
                new String[]{"Temporary Diamond Boots for until you die", "Cost 25 Gold"});
        ItemStack protall = Utilities.createItem(Material.IRON_CHESTPLATE, "Protection",
                new String[]{"Temporary Protection On All Armor for until you die", "Cost 50 Gold"});
        protall.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ItemStack rod = Utilities.createItem(Material.FISHING_ROD, "Fishing Rod",
                new String[]{"Temporary Fishing Rod for until you die", "Cost 30 Gold"});
        ItemStack glass = Utilities.createItem(Material.STAINED_GLASS, "Snow Block",
                new String[]{"Temporary Glass Blocks for building", "Cost 10 Gold (10 per)"});;
        shop.setItem(0, diamondsword);
        shop.setItem(1, diamondhelmet);
        shop.setItem(2, diamondboots);
        shop.setItem(3, protall);
        shop.setItem(4, rod);
        shop.setItem(5, glass);
    }
    @Override
    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(plugin.players.containsKey(player.getUniqueId())){
            int map_i = plugin.players.get(player.getUniqueId());
            Map map = plugin.maps.get(map_i);
            if(map.getType().equals("TDM") && map instanceof TeamDeathMatchMap){
                TeamDeathMatchMap tdmmap = (TeamDeathMatchMap) map;
                boolean close = tdmmap.buy(player, event.getCurrentItem());
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
