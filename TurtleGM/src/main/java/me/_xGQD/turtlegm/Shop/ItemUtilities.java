package me._xGQD.turtlegm.Shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtilities {
    public static ItemStack createItem(Material mat, String displayName, String[] newLore){
        ItemStack item = new ItemStack(mat);
        ItemMeta meta;
        meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        List<String> lore = new ArrayList<String>();
        for(int i = 0; i < newLore.length; i++){
            lore.add(newLore[i]);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }
}
