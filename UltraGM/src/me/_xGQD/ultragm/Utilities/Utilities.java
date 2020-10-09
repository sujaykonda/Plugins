package me._xGQD.ultragm.Utilities;


import me._xGQD.ultragm.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Set;

public class Utilities {
    public static class CommandStruct{
        Node<String, String> struct;
        public CommandStruct(Node<String, String> struct){
            this.struct = struct;
        }
        public void add(String[] path, String key, String result){
            Node<String, String> parent = struct.getNodeFromPath(0, path).value2;
            Node<String, String> child = new Node<>(key, result);
            parent.addChild(child);
            child.setParent(parent);
        }
        public Pair<String, Integer> getResult(String[] args){
            Pair<Integer, Node<String, String>> nodePair = struct.getNodeFromPath(0, args);
            return new Pair<>(nodePair.value2.getData(), nodePair.value1);
        }
    }
    public static ItemStack createItem(Material mat, String displayName, String[] newLore){
        ItemStack item = new ItemStack(mat);
        ItemMeta meta;
        if(item.hasItemMeta()) {
		    meta = item.getItemMeta();
		    meta.setDisplayName(displayName);
		    List<String> lore = meta.getLore();
		    for(String loreLine: newLore){
		        lore.add(loreLine);
		    }
		    meta.setLore(lore);
		    item.setItemMeta(meta);
        }
        return item;
    }
    public static class Wand{
        String name;
        Material material;
        Location pos_1;
        Location pos_2;
        Main plugin;
        public Wand(Main plugin, String name, Material material){
            this.plugin = plugin;
            this.name = name;
            this.material = material;
        }
        public ItemStack getWand(){
            ItemStack wand = new ItemStack(material);
            if(wand.hasItemMeta()){
                ItemMeta meta = wand.getItemMeta();
                meta.setDisplayName(name);
                List<String> lore = meta.getLore();
                lore.add("Select two points with left and right click");
                lore.add("then run any command that requires a wand");
                meta.setLore(lore);
                wand.setItemMeta(meta);
            }
            return wand;
        }
        public Location[] getPos(){
            return(new Location[]{pos_1, pos_2});
        }
        public String getName(){
            return(name);
        }
        public Material getType(){ return(material); }
        public void onPlayerBlockBreak(BlockBreakEvent event) {
            Player player = event.getPlayer();
            Block block = event.getBlock();
            Material type = block.getType();
            byte data = block.getData();
            pos_1 = block.getLocation();
            player.sendMessage("Pos 1 has been set to " + String.valueOf(block.getX()) + " " + String.valueOf(block.getY()) + " " + String.valueOf(block.getZ())) ;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    block.setType(type);
                    block.setData(data);
                }
            }, 1);
        }
        public void onPlayerInteract(PlayerInteractEvent event){
            Player player = event.getPlayer();
            Action eventAction = event.getAction();
            if(eventAction == Action.RIGHT_CLICK_BLOCK || eventAction == Action.RIGHT_CLICK_AIR) {
                Block block = event.getPlayer().getTargetBlock((Set<Material>) null, 6);
                if (block != null && block.getType() != Material.AIR) {
                    pos_2 = block.getLocation();
                    player.sendMessage("Pos 2 has been set to " + String.valueOf(block.getX()) + " " + String.valueOf(block.getY()) + " " + String.valueOf(block.getZ()));
                }
                else {
                    player.sendMessage("The selected block was air or none could be found");
                }
            }
        }
    }
}
