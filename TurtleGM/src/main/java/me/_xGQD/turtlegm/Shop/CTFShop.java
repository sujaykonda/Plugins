package me._xGQD.turtlegm.Shop;

import me._xGQD.turtlegm.ItemUtilities;
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

        ItemStack permgold = ItemUtilities.createItem(Material.GOLD_CHESTPLATE, "Gold Armor",
                new String[]{"Permanent Gold Chestplate, Leggings, Boots", "Cost 50 Gold"});
        ItemStack permsword = ItemUtilities.createItem(Material.GOLD_SWORD, "Sharp 2 Gold Sword",
                new String[]{"Permanent Sharpness 2 Golden Sword", "Cost 50 Gold"});
        permsword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
        ItemStack sword = ItemUtilities.createItem(Material.DIAMOND_SWORD, "Diamond Sword",
                new String[]{"Temporary Diamond Sword for until you die", "Cost 25 Gold"});
        ItemStack iron = ItemUtilities.createItem(Material.IRON_CHESTPLATE, "Iron Armor",
                new String[]{"Temporary Iron Armor for until you die", "Cost 50 Gold"});
        ItemStack shears = ItemUtilities.createItem(Material.SHEARS, "Shears",
                new String[]{"Permanent Shears for faster break time", "Cost 50 Gold"});
        ItemStack rod = ItemUtilities.createItem(Material.FISHING_ROD, "Fishing Rod",
                new String[]{"Temporary Fishing Rod for until you die", "Cost 50 Gold"});
        ItemStack snow = ItemUtilities.createItem(Material.SNOW_BLOCK, "Snow Block",
                new String[]{"Temporary Snow Blocks for longer break time", "Cost 20 Gold (4 per)"});

        costs.put(permgold.getType(), 50);
        costs.put(permsword.getType(), 50);
        costs.put(sword.getType(), 25);
        costs.put(iron.getType(), 50);
        costs.put(shears.getType(), 50);
        costs.put(rod.getType(), 50);
        costs.put(snow.getType(), 20);

        shop.setItem(0, permgold);
        shop.setItem(1, permsword);
        shop.setItem(2, sword);
        shop.setItem(3, iron);
        shop.setItem(4, shears);
        shop.setItem(5, rod);
        shop.setItem(6, snow);
    }

    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        Material mat = event.getCurrentItem().getType();
        if(plugin.manager.playerIn(player)){
            String[] ids = plugin.manager.getMapIds(player);
            Map map = plugin.manager.getMap(ids[0], ids[1]);
            if(map instanceof CTFMap){
                int new_gold = onBuy(player, mat, ((CTFMap) map).getPlayerData(player.getUniqueId()).gold);
                if(new_gold < 0){
                    player.sendMessage("Not enough gold to buy this item");
                    event.setCancelled(true);
                    player.closeInventory();
                    return;
                }
                ((CTFMap) map).getPlayerData(player.getUniqueId()).gold = new_gold;
                switch (mat){
                    case GOLD_CHESTPLATE:
                        ((CTFMap) map).getPlayerData(player.getUniqueId()).buffs.add("permarmor");
                        event.setCancelled(true);
                        player.closeInventory();
                        ((CTFMap) map).readBuffs(player);
                        break;
                    case GOLD_SWORD:
                        ((CTFMap) map).getPlayerData(player.getUniqueId()).buffs.add("permsword");
                        event.setCancelled(true);
                        player.closeInventory();
                        ((CTFMap) map).readBuffs(player);
                        break;
                    case DIAMOND_SWORD:
                        player.getInventory().setItem(((CTFMap) map).kits.get(player.getUniqueId()).sword, new ItemStack(Material.DIAMOND_SWORD));
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                    case IRON_CHESTPLATE:
                        player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                        player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                    case SHEARS:
                        ((CTFMap) map).getPlayerData(player.getUniqueId()).buffs.add("shears");
                        event.setCancelled(true);
                        player.closeInventory();
                        ((CTFMap) map).readBuffs(player);
                        break;
                    case FISHING_ROD:
                        player.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                    case SNOW_BLOCK:
                        player.getInventory().addItem(new ItemStack(Material.SNOW_BLOCK, 4));
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                    default:
                        event.setCancelled(true);
                        player.closeInventory();
                        break;
                }
            }
        }
    }
}
