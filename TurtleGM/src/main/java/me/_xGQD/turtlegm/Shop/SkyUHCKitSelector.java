package me._xGQD.turtlegm.Shop;

import me._xGQD.turtlegm.ItemUtilities;
import org.bukkit.Bukkit;
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

        ItemStack default_kit = ItemUtilities.createItem(Material.IRON_PICKAXE, "Default", new String[]{
                "Gives you chainmail armor and iron tools"});
        ItemStack ench_kit = ItemUtilities.createItem(Material.ENCHANTMENT_TABLE, "Enchanter", new String[]{
                "Gives you gold armor, a enchanting kit (with some bookshelves), along with gold tools"});
        ItemStack rush_kit = ItemUtilities.createItem(Material.STONE, "Rush", new String[]{
                "Gives you stone blocks and some chainmail and iron armor"});
        ItemStack alch_kit = ItemUtilities.createItem(Material.BREWING_STAND_ITEM, "Alchemist", new String[]{
                "Give you full chainmail armor and stone tools",
                "Also a brewing stand, ghast tear, wart, and some bottles"});
        ItemStack fishermen_kit = ItemUtilities.createItem(Material.FISHING_ROD, "Fishermen", new String[]{
                "Gives you chainmail helmet, chestplate, pants and diamond boots with depth strider",
                "Also a fishing rod with max rod enchants, stone pick and iron axe"});

        shop.setItem(0, default_kit);
        shop.setItem(2, ench_kit);
        shop.setItem(4, fishermen_kit);
        shop.setItem(6, alch_kit);
        shop.setItem(8, rush_kit);
    }

    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        Material mat = event.getCurrentItem().getType();
        if(plugin.manager.playerIn(player)){
            switch (mat){
                case IRON_PICKAXE:
                    player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
                    player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                    player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                    player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
                    player.getInventory().addItem(new ItemStack(Material.IRON_AXE));
                    player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
                    player.getInventory().addItem(new ItemStack(Material.IRON_SPADE));
                    event.setCancelled(true);
                    player.closeInventory();
                    break;
                case ENCHANTMENT_TABLE:
                    player.getInventory().setBoots(new ItemStack(Material.GOLD_BOOTS));
                    player.getInventory().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
                    player.getInventory().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
                    player.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET));
                    player.getInventory().addItem(new ItemStack(Material.GOLD_AXE));
                    player.getInventory().addItem(new ItemStack(Material.GOLD_PICKAXE));
                    player.getInventory().addItem(new ItemStack(Material.GOLD_SPADE));
                    player.getInventory().addItem(new ItemStack(Material.ENCHANTMENT_TABLE));
                    player.getInventory().addItem(new ItemStack(Material.EXP_BOTTLE,20));
                    player.getInventory().addItem(new ItemStack(Material.BOOKSHELF,4));
                    player.getInventory().addItem(new ItemStack(351,64,(short) 4));
                    event.setCancelled(true);
                    player.closeInventory();
                    break;
                case STONE:
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
                case BREWING_STAND_ITEM:
                    player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
                    player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                    player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                    player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
                    player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
                    player.getInventory().addItem(new ItemStack(Material.STONE_AXE));
                    player.getInventory().addItem(new ItemStack(Material.BREWING_STAND_ITEM));
                    player.getInventory().addItem(new ItemStack(Material.GHAST_TEAR));
                    player.getInventory().addItem(new ItemStack(Material.NETHER_STALK,1));
                    player.getInventory().addItem(new ItemStack(Material.GLASS_BOTTLE,3));
                    event.setCancelled(true);
                    player.closeInventory();
                    break;
                case FISHING_ROD:
                    ItemStack depth_strider_boots = new ItemStack(Material.DIAMOND_BOOTS);
                    depth_strider_boots.addEnchantment(Enchantment.DEPTH_STRIDER, 3);
                    player.getInventory().setBoots(depth_strider_boots);
                    player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                    player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                    player.getInventory().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
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
