package me._xGQD.turtlegm.Shop;

import me._xGQD.turtlegm.Maps.CTF.CTFMap;
import me._xGQD.turtlegm.Maps.CTF.CTFPlayerData;
import me._xGQD.turtlegm.Maps.Map;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class SkyUHCKitSelector extends Shop{
    public SkyUHCKitSelector(){
        costs = new HashMap<>();
        shop = Bukkit.createInventory(null, 9);

        ItemStack default_kit = ItemUtilities.createItem(Material.STONE_PICKAXE, "Default", new String[]{
                "Gives you chainmail armor, a stone tools with the exception of a iron axe"});
        ItemStack gold_kit = ItemUtilities.createItem(Material.GOLD_CHESTPLATE, "Gold", new String[]{
                "Gives you gold armor and gold tools"});
        ItemStack rush_kit = ItemUtilities.createItem(Material.STONE, "Rush", new String[]{
                "Gives you stone blocks and "});
        ItemStack snowman_kit = ItemUtilities.createItem(Material.SNOW_BALL, "Snowman", new String[]{
                "Gives you diamond helmet and leather chestplate, pants, boots",
                "Also a diamond shovel, stone axe, stone pickaxe and 16 snowballs"});
        ItemStack fishermen_kit = ItemUtilities.createItem(Material.FISHING_ROD, "Fishermen", new String[]{
                "Gives you leather helmet, chestplate, pants and diamond boots with depth strider",
                "Also a fishing rod with max rod enchants, stone pick and iron axe"});


        costs.put("STONE_PICKAXE", 0);
        costs.put("GOLD_CHESTPLATE", 0);
        costs.put("STONE", 0);
        costs.put("SNOW_BALL", 0);
        costs.put("FISHING_ROD", 0);

        shop.setItem(0, default_kit);
        shop.setItem(2, gold_kit);
        shop.setItem(4, fishermen_kit);
        shop.setItem(6, snowman_kit);
        shop.setItem(8, rush_kit);
    }

    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        String mat = event.getCurrentItem().getType().name();
        if(plugin.manager.playerIn(player)){
            switch (mat){
                case "STONE_PICKAXE":
                    player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
                    player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                    player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                    player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
                    player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
                    player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
                    player.getInventory().addItem(new ItemStack(Material.STONE_SPADE));
                    event.setCancelled(true);
                    player.closeInventory();
                    break;
                case "GOLD_CHESTPLATE":
                    player.getInventory().setBoots(new ItemStack(Material.GOLD_BOOTS));
                    player.getInventory().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
                    player.getInventory().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
                    player.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET));
                    player.getInventory().addItem(new ItemStack(Material.GOLD_AXE));
                    player.getInventory().addItem(new ItemStack(Material.GOLD_PICKAXE));
                    player.getInventory().addItem(new ItemStack(Material.GOLD_SPADE));
                    event.setCancelled(true);
                    player.closeInventory();
                    break;
                case "STONE":
                    player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                    player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                    player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                    player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
                    player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                    player.getInventory().addItem(new ItemStack(Material.WOOD_PICKAXE));
                    player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                    player.getInventory().addItem(new ItemStack(Material.STONE, 16));
                    event.setCancelled(true);
                    player.closeInventory();
                    break;
                case "SNOW_BALL":
                    player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
                    player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                    player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
                    player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                    ItemStack diamond_shovel = new ItemStack(Material.DIAMOND_SPADE);
                    diamond_shovel.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
                    player.getInventory().addItem(diamond_shovel);
                    player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
                    player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                    player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 16));
                    event.setCancelled(true);
                    player.closeInventory();
                    break;
                case "FISHING_ROD":
                    ItemStack depth_strider_boots = new ItemStack(Material.DIAMOND_BOOTS);
                    depth_strider_boots.addEnchantment(Enchantment.DEPTH_STRIDER, 3);
                    player.getInventory().setBoots(depth_strider_boots);
                    player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                    player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
                    player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET));
                    player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                    ItemStack fishing_rod = new ItemStack(Material.FISHING_ROD);
                    fishing_rod.addEnchantment(Enchantment.LURE, 3);
                    fishing_rod.addEnchantment(Enchantment.LUCK, 3);
                    fishing_rod.addEnchantment(Enchantment.DURABILITY, 3);
                    player.getInventory().addItem(fishing_rod);
                    player.getInventory().addItem(new ItemStack(Material.WOOD_PICKAXE));
                    player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                    player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
                    event.setCancelled(true);
                    player.closeInventory();
                    break;
                default:
                    break;
            }
        }
    }
}
