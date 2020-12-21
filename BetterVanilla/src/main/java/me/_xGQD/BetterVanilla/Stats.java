package me._xGQD.BetterVanilla;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Stats {
    public Stats(int dmg, int str, int health, int defense){

    }

    public ItemStack addStats(ItemStack item) {

        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        List<String> lore = meta.getLore();
        return item;
    }
}
