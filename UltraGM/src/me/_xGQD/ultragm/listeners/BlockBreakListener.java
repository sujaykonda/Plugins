package me._xGQD.ultragm.listeners;

import me._xGQD.ultragm.Main;
import me._xGQD.ultragm.gamemodes.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockBreakListener implements Listener {
    Main plugin;
    public BlockBreakListener(Main plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(player.getItemInHand().getType().equals(plugin.wand.getType())){
            plugin.wand.onPlayerBlockBreak(event);
        }
        int map_i = plugin.getMap(player);
        if(map_i != -1){
            Map map = plugin.maps.get(map_i);
            if(map.isOpen()){
                Block block = event.getBlock();
                Material type = block.getType();
                byte data = block.getData();
                block.setType(Material.AIR);
                player.sendMessage("You cannot break this block");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        block.setType(type);
                        block.setData(data);
                    }
                },1);
            }
            if(map.isStart()){
                map.onBlockBreak(event);
            }
        }
    }
}
