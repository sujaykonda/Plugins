package me._xGQD.turtlegm.Shop;

import me._xGQD.turtlegm.Maps.CTF.CTFMap;
import me._xGQD.turtlegm.Maps.CTF.CTFPlayerData;
import me._xGQD.turtlegm.Maps.Map;
import me._xGQD.turtlegm.Maps.PlayerData;
import me._xGQD.turtlegm.Maps.UltimateCTF.UltimateCTFMap;
import me._xGQD.turtlegm.Maps.UltimateCTF.UltimateCTFPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class UltimateCTFPerks extends Shop{
    public UltimateCTFPerks(){
        super();
        costs = new HashMap<>();
        shop = Bukkit.createInventory(null, 9);

        ItemStack gold = ItemUtilities.createItem(Material.GOLD_INGOT, "Gold Perk",
                new String[]{"Gives you 15 gold instead of 10 gold on kill"});
        ItemStack kb = ItemUtilities.createItem(Material.IRON_SWORD, "Knockback Perk",
                new String[]{"Gives you the capability to deal more knockback with your sword"});
            kb.addEnchantment(Enchantment.KNOCKBACK, 1);
        ItemStack rush = ItemUtilities.createItem(Material.POTION, "Rush Perk",
                new String[]{"Gives you speed on kill"});
        ItemStack warp = ItemUtilities.createItem(Material.ENDER_PEARL, "Time Warp Perk",
                new String[]{"Gives you a time warp pearl"});

        shop.setItem(0, gold);
        shop.setItem(1, kb);
        shop.setItem(2, rush);
        shop.setItem(3, warp);
}
    @Override
    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        String mat = event.getCurrentItem().getType().name();
        if(plugin.manager.playerIn(player)){
            String[] ids = plugin.manager.getMapIds(player);
            Map map = plugin.manager.getMap(ids[0], ids[1]);
            PlayerData data = map.playerData.get(player.getUniqueId());
            if(map instanceof UltimateCTFMap){
                UltimateCTFMap ctfMap = (UltimateCTFMap) map;
                UltimateCTFPlayerData ctfData = (UltimateCTFPlayerData) data;
                System.out.println(mat);
                switch (mat){
                    case "GOLD_INGOT":
                        ctfData.perk = 1;
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                    case "IRON_SWORD":
                        ctfData.perk = 2;
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                    case "POTION":
                        ctfData.perk = 3;
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                    case "ENDER_PEARL":
                        ctfData.perk = 4;
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                    default:
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                }
                ctfMap.readBuffs(player);
                ctfMap.playerData.put(player.getUniqueId(), ctfData);
                plugin.manager.setMap(ids[0], ids[1], ctfMap);
            }
        }
    }
}
