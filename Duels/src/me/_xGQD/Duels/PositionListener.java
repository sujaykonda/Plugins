package me._xGQD.Duels;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PositionListener implements Listener{
	private static Main plugin;
	
	public PositionListener(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onPlayerBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Material type = block.getType();
		if(player.getItemInHand().hasItemMeta()) {
			if(player.getItemInHand().getItemMeta().getDisplayName().equals("Duels Wand")) {
				plugin.pos1[0] = block.getX();
				plugin.pos1[1] = block.getY(); 
				plugin.pos1[2] = block.getZ();
				plugin.world = block.getWorld();
				player.sendMessage("Pos 1 has been set to " + String.valueOf(block.getX()) + " " + String.valueOf(block.getY()) + " " + String.valueOf(block.getZ())) ;
				new BukkitRunnable() {
	                @Override
	                public void run() {
	                    block.setType(type);
	                }
	            }.runTaskLater(plugin, 1);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action eventAction = event.getAction();
		Player player = event.getPlayer();
		if(player.getItemInHand().hasItemMeta()) {
			if(player.getItemInHand().getItemMeta().getDisplayName().equals("Duels Wand")) { 
				if(eventAction == Action.RIGHT_CLICK_BLOCK || eventAction == Action.RIGHT_CLICK_AIR) {
					Block block = event.getPlayer().getTargetBlock((Set<Material>) null, 6);
					if (block != null && block.getType() != Material.AIR) {
						plugin.pos2[0] = block.getX();
						plugin.pos2[1] = block.getY(); 
						plugin.pos2[2] = block.getZ();
						player.sendMessage("Pos 2 has been set to " + String.valueOf(block.getX()) + " " + String.valueOf(block.getY()) + " " + String.valueOf(block.getZ()));
						plugin.world = block.getWorld();
					}
					else {
						player.sendMessage("The selected block was air or none could be found");
					}
				}
			}
		}
	}
}
