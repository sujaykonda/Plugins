package me._xGQD.BetterVanilla.ExtraItems;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class Item {
    ItemStack item;
    public Item(){
        item = new ItemStack(Material.STICK);

    }
    public ItemStack getItemStack(){
        return item;
    }

    public void onAttack(){

    }

    public void onAttacked(){

    }

    public void runAbility(Player player, Action action){

    }

    public void getStats(){

    }

}
