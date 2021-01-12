package me._xGQD.turtlegm.Shop;

import me._xGQD.turtlegm.Maps.CTF.CTFMap;
import me._xGQD.turtlegm.Maps.CTF.CTFPlayerData;
import me._xGQD.turtlegm.Maps.Map;
import me._xGQD.turtlegm.Maps.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class CTFTeamSelector extends Shop{

    public CTFTeamSelector(){
        super();
        costs = new HashMap<>();
        shop = Bukkit.createInventory(null, 9);

        ItemStack redstone_block = ItemUtilities.createItem(Material.REDSTONE_BLOCK, "Red Team", new String[]{
                "Click me to join red team"});
        ItemStack lapis_block = ItemUtilities.createItem(Material.LAPIS_BLOCK, "Blue Team", new String[]{
                "Click me to join blue team"});

        costs.put("REDSTONE_BLOCK", 0);
        costs.put("LAPIS_BLOCK", 0);

        shop.setItem(1, redstone_block);
        shop.setItem(7, lapis_block);
    }

    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        String mat = event.getCurrentItem().getType().name();
        if(plugin.manager.playerIn(player)){
            String[] ids = plugin.manager.getMapIds(player);
            Map map = plugin.manager.getMap(ids[0], ids[1]);
            if(map instanceof CTFMap){
                CTFMap ctfMap = (CTFMap) map;
                CTFPlayerData ctfData;
                switch (mat){
                    case "REDSTONE_BLOCK":
                        ctfData = new CTFPlayerData(0);
                        ctfMap.playerData.put(player.getUniqueId(), ctfData);
                        ctfMap.spawn(player, 0);
                        ctfMap.team_count_1 += 1;
                        player.sendMessage("Sending you to red team");
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                    case "LAPIS_BLOCK":
                        ctfData = new CTFPlayerData(1);
                        ctfMap.playerData.put(player.getUniqueId(), ctfData);
                        ctfMap.spawn(player, 1);
                        ctfMap.team_count_2 += 1;
                        player.sendMessage("Sending you to blue team");
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                    default:
                        break;
                }
                plugin.manager.setMap(ids[0], ids[1], ctfMap);
            }
        }
    }
}
