package me._xGQD.CapTheFlag;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import me._xGQD.CapTheFlag.FastBoard.FastBoard;


public class PositionListener implements Listener{
	private static Main plugin;
	
	public PositionListener(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        FastBoard board = new FastBoard(player);

        board.updateTitle(ChatColor.GREEN + "TURTE PVP");
        
        board.updateLines(
        		"",
        		"",
        		"",
        		"",
        		"");
        
        plugin.boards.put(player.getUniqueId(), board);
    }
	
	@EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        FastBoard board = plugin.boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }
	
	@EventHandler 
	public void onBlockPlace(BlockPlaceEvent event){
		if((plugin.start || plugin.open) && (plugin.team_1.contains(event.getPlayer().getUniqueId()) || plugin.team_2.contains(event.getPlayer().getUniqueId()))) {
			Block block = event.getBlock();
			if(block.getLocation().getX() == plugin.spawn_loc_1.getX() && block.getLocation().getZ() == plugin.spawn_loc_1.getZ() && block.getLocation().getY() >= plugin.spawn_loc_1.getY()) {
				event.getPlayer().sendMessage("You cannot place a block at the spawn point");
				event.setCancelled(true);
			}
			if(block.getLocation().getX() == plugin.spawn_loc_2.getX() && block.getLocation().getZ() == plugin.spawn_loc_2.getZ() && block.getLocation().getY() >= plugin.spawn_loc_2.getY()) {
				event.getPlayer().sendMessage("You cannot place a block at the spawn point");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if((plugin.start || plugin.open) && (plugin.team_1.contains(player.getUniqueId()) || plugin.team_2.contains(player.getUniqueId()))) {
				if (event instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
					if(e.getDamager() instanceof Player) {
						Player damagePlayer = (Player) e.getDamager();
						if(plugin.team_1.contains(player.getUniqueId()) && plugin.team_1.contains(damagePlayer.getUniqueId())) {
							event.setCancelled(true);
						}
						else if(plugin.team_2.contains(player.getUniqueId()) && plugin.team_2.contains(damagePlayer.getUniqueId())) {
							event.setCancelled(true);
						}else if(plugin.spawn_prot.contains(player.getUniqueId())){
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerKill(PlayerDeathEvent e) {
		if(e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if((plugin.start || plugin.open) && (plugin.team_1.contains(player.getUniqueId()) || plugin.team_2.contains(player.getUniqueId()))) {
				Player p = e.getEntity().getKiller();
				FastBoard board = plugin.boards.get(p.getUniqueId());
				board.updateLine(3, "Gold: " + (plugin.gold.get(p.getUniqueId()) + 10));
				p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 3));
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeathDrop(PlayerDeathEvent e) {
		if(e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if((plugin.start || plugin.open) && (plugin.team_1.contains(player.getUniqueId()) || plugin.team_2.contains(player.getUniqueId()))) {
				e.getDrops().clear();
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if((plugin.start || plugin.open) && (plugin.team_1.contains(event.getPlayer().getUniqueId()) || plugin.team_2.contains(event.getPlayer().getUniqueId()))) {
			Block block = event.getBlock();
			Player player = event.getPlayer();
			Material type = block.getType();
			Byte data = block.getData();
			if(block.getType() != Material.STAINED_GLASS) {
				block.setType(Material.AIR);
				player.sendMessage("You cannot break this block");
				new BukkitRunnable() {
	                @Override
	                public void run() {
	                    block.setType(type);
	                    block.setData(data);
	                }
	            }.runTaskLater(plugin, 1);
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if((plugin.start || plugin.open) && (plugin.team_1.contains(event.getPlayer().getUniqueId()) || plugin.team_2.contains(event.getPlayer().getUniqueId()))) {
			Player player = event.getPlayer();
			plugin.spawn_prot.add(player.getUniqueId());
			if(plugin.team_1.contains(player.getUniqueId())) {
					
				BukkitScheduler scheduler = plugin.getServer().getScheduler();
			    scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			    	@Override
			        public void run() {
			    		Functions.GivePlayerRed(player, plugin);
			        }
			    }, 20L);
			}
			
			if(plugin.team_2.contains(player.getUniqueId())) {
				
				BukkitScheduler scheduler1 = plugin.getServer().getScheduler();
			    scheduler1.scheduleSyncDelayedTask(plugin, new Runnable() {
			    	@Override
			        public void run() {
						Functions.GivePlayerBlue(player, plugin);
			        }
		        }, 20L);		
			}
			BukkitScheduler scheduler1 = plugin.getServer().getScheduler();
		    scheduler1.scheduleSyncDelayedTask(plugin, new Runnable() {
		    	@Override
		        public void run() {
		    		plugin.spawn_prot.remove(player.getUniqueId());
		        }
		    }, 40L);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(plugin.start && plugin.team_1.contains(event.getPlayer().getUniqueId()) || plugin.team_2.contains(event.getPlayer().getUniqueId())) {
			Action eventAction = event.getAction();
			Player player = event.getPlayer();
			if(eventAction == Action.RIGHT_CLICK_BLOCK || eventAction == Action.RIGHT_CLICK_AIR) {
				Block block = event.getPlayer().getTargetBlock((Set<Material>) null, 6);
				if (block != null && block.getType() == Material.STANDING_BANNER) {
					if(block.getData() == (byte) 8) {
						if(player.getInventory().getHelmet().getType() == Material.BANNER) {
							if(plugin.team_1.contains(player.getUniqueId())) {
								Bukkit.broadcastMessage("Team 1 has won");
								
								Functions.End(plugin);
							}
						}else {
							if(plugin.team_2.contains(player.getUniqueId())) {
								Bukkit.broadcastMessage("Team 2 has picked up the flag");
							
								ItemStack banner = new ItemStack(Material.BANNER, 1, (byte) 1);
							
								player.getInventory().setHelmet(banner);
							}
							
						}
					}else {
						if(player.getInventory().getHelmet().getType() == Material.BANNER) {
							if(plugin.team_2.contains(player.getUniqueId())) {
								Bukkit.broadcastMessage("Team 2 has won");

								Functions.End(plugin);
							}
							
						}else {
							if(plugin.team_1.contains(player.getUniqueId())) {
								Bukkit.broadcastMessage("Team 1 has picked up the flag");
								
								ItemStack banner = new ItemStack(Material.BANNER, 1, (byte) 4);
								
								player.getInventory().setHelmet(banner);
							}
						}
					}
				}
			}
		}
	}
}
