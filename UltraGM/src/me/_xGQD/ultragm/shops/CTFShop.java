package me._xGQD.ultragm.shops;

import me._xGQD.ultragm.Main;
import me._xGQD.ultragm.Utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class CTFShop extends Shop{

    public CTFShop(Main plugin, String mapType) {
        super(plugin, mapType);
        shop = Bukkit.createInventory(null, 9);
        ItemStack permchain = Utilities.createItem(Material.GOLD_CHESTPLATE, "Gold Armor",
                new String[]{"Permanent Gold Chestplate, Leggings, Boots", "Cost 100 Gold"});
        ItemStack permsword = Utilities.createItem(Material.GOLD_SWORD, "Sharp 2 Gold Sword",
                new String[]{"Permanent Sharpness 2 Golden Sword", "Cost 50 Gold"});
        permsword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
        ItemStack sword = Utilities.createItem(Material.DIAMOND_SWORD, "Diamond Sword",
                new String[]{"Temporary Diamond Sword for until you die", "Cost 25 Gold"});
        ItemStack iron = Utilities.createItem(Material.IRON_CHESTPLATE, "Iron Armor",
                new String[]{"Temporary Iron Armor for until you die", "Cost 50 Gold"});
        ItemStack haste = Utilities.createItem(Material.GOLD_PICKAXE, "Haste 3",
                new String[]{"Permanent Haste 3 for faster break time", "Cost 50 Gold"});
        ItemStack rod = Utilities.createItem(Material.FISHING_ROD, "Fishing Rod",
                new String[]{"Temporary Fishing Rod for until you die", "Cost 30 Gold"});
        ItemStack snow = Utilities.createItem(Material.SNOW_BLOCK, "Snow Block",
                new String[]{"Temporary Snow Blocks for longer break time", "Cost 20 Gold (4 per)"});;
        shop.setItem(0, permchain);
        shop.setItem(1, permsword);
        shop.setItem(2, sword);
        shop.setItem(3, iron);
        shop.setItem(4, haste);
        shop.setItem(5, rod);
        shop.setItem(6, snow);
    }
}
