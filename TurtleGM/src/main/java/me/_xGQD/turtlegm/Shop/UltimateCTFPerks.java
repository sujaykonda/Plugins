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
                new String[]{"Gives you knockback enchantment on your sword"});
            kb.addEnchantment(Enchantment.KNOCKBACK, 1);
        ItemStack rush = ItemUtilities.createItem(Material.POTION, "Rush Perk",
                new String[]{"Gives you speed on kill"});
        ItemStack warp = ItemUtilities.createItem(Material.ENDER_PEARL, "Time Warp Perk",
                new String[]{"Gives you a time warp pearl"});

        costs.put("GOLD_INGOT", 0);
        costs.put("IRON_SWORD", 0);
        costs.put("POTION", 0);
        costs.put("ENDER_PEARL", 0);

        shop.setItem(0, gold);
        shop.setItem(1, kb);
        shop.setItem(2, rush);
        shop.setItem(3, warp);
}

    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        String mat = event.getCurrentItem().getType().name();
        if(plugin.manager.playerIn(player)){
            String mapName = plugin.manager.getMapName(player);
            Map map = plugin.manager.getMap(mapName);
            PlayerData data = map.getPlayerData(player);
            if(map instanceof CTFMap){
                UltimateCTFMap ctfMap = (UltimateCTFMap) map;
                UltimateCTFPlayerData ctfData = (UltimateCTFPlayerData) data;
                int new_gold = onBuy(player, mat, ctfData.gold);
                if(new_gold < 0){
                    player.sendMessage("Not enough gold to buy this item");
                    event.setCancelled(true);
                    player.closeInventory();
                    return;
                }
                ctfData.gold = new_gold;
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
                }
                ctfMap.readBuffs(player);
                ctfMap.setPlayerData(player, ctfData);
                plugin.manager.setMap(mapName, ctfMap);
            }
        }
    }
}
