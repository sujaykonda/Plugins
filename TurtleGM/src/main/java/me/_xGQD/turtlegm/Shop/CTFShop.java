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

public class CTFShop extends Shop{
    public CTFShop(){
        super();
        costs = new HashMap<>();
        shop = Bukkit.createInventory(null, 9);

        ItemStack permchain = ItemUtilities.createItem(Material.GOLD_CHESTPLATE, "Gold Armor",
                new String[]{"Permanent Gold Chestplate, Leggings, Boots", "Cost 50 Gold"});
        ItemStack permsword = ItemUtilities.createItem(Material.GOLD_SWORD, "Sharp 2 Gold Sword",
                new String[]{"Permanent Sharpness 2 Golden Sword", "Cost 50 Gold"});
        permsword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
        ItemStack sword = ItemUtilities.createItem(Material.DIAMOND_SWORD, "Diamond Sword",
                new String[]{"Temporary Diamond Sword for until you die", "Cost 25 Gold"});
        ItemStack iron = ItemUtilities.createItem(Material.IRON_CHESTPLATE, "Iron Armor",
                new String[]{"Temporary Iron Armor for until you die", "Cost 50 Gold"});
        ItemStack haste = ItemUtilities.createItem(Material.GOLD_PICKAXE, "Haste 3",
                new String[]{"Permanent Haste 3 for faster break time", "Cost 50 Gold"});
        ItemStack rod = ItemUtilities.createItem(Material.FISHING_ROD, "Fishing Rod",
                new String[]{"Temporary Fishing Rod for until you die", "Cost 30 Gold"});
        ItemStack snow = ItemUtilities.createItem(Material.SNOW_BLOCK, "Snow Block",
                new String[]{"Temporary Snow Blocks for longer break time", "Cost 20 Gold (4 per)"});

        costs.put("GOLD_CHESTPLATE", 50);
        costs.put("GOLD_SWORD", 50);
        costs.put("DIAMOND_SWORD", 25);
        costs.put("IRON_CHESTPLATE", 50);
        costs.put("GOLD_PICKAXE", 50);
        costs.put("FISHING_ROD", 30);
        costs.put("SNOW_BLOCK", 20);

        shop.setItem(0, permchain);
        shop.setItem(1, permsword);
        shop.setItem(2, sword);
        shop.setItem(3, iron);
        shop.setItem(4, haste);
        shop.setItem(5, rod);
        shop.setItem(6, snow);
    }

    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        String mat = event.getCurrentItem().getType().name();
        if(plugin.manager.playerIn(player)){
            String mapName = plugin.manager.getMapName(player);
            Map map = plugin.manager.getMap(mapName);
            PlayerData data = map.getPlayerData(player);
            if(map instanceof CTFMap){
                CTFMap ctfMap = (CTFMap) map;
                CTFPlayerData ctfData = (CTFPlayerData) data;
                int new_gold = onBuy(player, mat, ctfData.gold);
                if(new_gold < 0){
                    player.sendMessage("Not enough gold to buy this item");
                    event.setCancelled(true);
                    player.closeInventory();
                    return;
                }
                ctfData.gold = new_gold;
                switch (mat){
                    case "GOLD_CHESTPLATE":
                        ctfData.buffs.add("permarmor");
                        event.setCancelled(true);
                        player.closeInventory();
                        ctfMap.readBuffs(player);
                        break;
                    case "GOLD_SWORD":
                        ctfData.buffs.add("permsword");
                        event.setCancelled(true);
                        player.closeInventory();
                        ctfMap.readBuffs(player);
                        break;
                    case "DIAMOND_SWORD":
                        player.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD));
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                    case "IRON_CHESTPLATE":
                        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                        player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                    case "GOLD_PICKAXE":
                        ctfData.buffs.add("haste");
                        event.setCancelled(true);
                        player.closeInventory();
                        ctfMap.readBuffs(player);
                        break;
                    case "FISHING_ROD":
                        player.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                    case "SNOW_BLOCK":
                        player.getInventory().addItem(new ItemStack(Material.SNOW_BLOCK, 4));
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                }
                ctfMap.setPlayerData(player, ctfData);
                plugin.manager.setMap(mapName, ctfMap);
            }
        }
    }
}
