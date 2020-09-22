package me._xGQD.CapTheFlag.Listeners;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import me._xGQD.CapTheFlag.Main;
import me._xGQD.CapTheFlag.Classes.GameMap;
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

        board.updateTitle(ChatColor.GREEN + "TURTLE " + ChatColor.BLUE + "PVP");
        
        board.updateLines(
        		"",
        		"",
        		"",
        		"",
        		"");
        
        plugin.boards.put(player.getUniqueId(), board);
	    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 9999999, 255));
    }
	
	@EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        FastBoard board = plugin.boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }
	
	public void onPlayerRespawn(PlayerRespawnEvent event) {
	    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 9999999, 255));
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked(); // The player that clicked the item
		ItemStack clicked = event.getCurrentItem(); // The item that was clicked
		Inventory inventory = event.getInventory(); // The inventory that was clicked in
		GameMap map = plugin.getMap(player);
		if (map != null) {
			if (inventory.getName().equals(plugin.shop.getName())) { // The inventory is our custom Inventory
				if (clicked.getType() == Material.IRON_CHESTPLATE) { // The item that the player clicked it dirt
					event.setCancelled(true); // Make it so the dirt is back in its original spot
					player.closeInventory(); // Closes there inventory
					if (plugin.gold.get(player.getUniqueId()) >= 50) {
						plugin.gold.put(player.getUniqueId(), plugin.gold.get(player.getUniqueId())-50);
						player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
						player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
						player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
					}
				}
				if (clicked.getType() == Material.DIAMOND_SWORD) {
					event.setCancelled(true); // Make it so the dirt is back in its original spot
					player.closeInventory(); // Closes there inventory
					if (plugin.gold.get(player.getUniqueId()) >= 30) {
						plugin.gold.put(player.getUniqueId(), plugin.gold.get(player.getUniqueId())-30);
						player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
					}
				}
				if(clicked.getType() == Material.GOLD_SWORD) {
					event.setCancelled(true); // Make it so the dirt is back in its original spot
					player.closeInventory(); // Closes there inventory
					if (plugin.gold.get(player.getUniqueId()) >= 50) {
						plugin.gold.put(player.getUniqueId(), plugin.gold.get(player.getUniqueId())-50);
						List<String> buffs = plugin.buffs.get(player.getUniqueId());
						buffs.add("permsword");
						plugin.buffs.put(player.getUniqueId(), buffs);
						map.ReadBuffs(player, plugin);
					}
				}
				if(clicked.getType() == Material.CHAINMAIL_CHESTPLATE) {
					event.setCancelled(true); // Make it so the dirt is back in its original spot
					player.closeInventory(); // Closes there inventory
					if (plugin.gold.get(player.getUniqueId()) >= 100) {
						plugin.gold.put(player.getUniqueId(), plugin.gold.get(player.getUniqueId())-100);
						List<String> buffs = plugin.buffs.get(player.getUniqueId());
						buffs.add("permarmor");
						plugin.buffs.put(player.getUniqueId(), buffs);
						map.ReadBuffs(player, plugin);
					}
				}
				if(clicked.getType() == Material.GOLD_PICKAXE) {
					event.setCancelled(true); // Make it so the dirt is back in its original spot
					player.closeInventory(); // Closes there inventory
					if (plugin.gold.get(player.getUniqueId()) >= 50) {
						plugin.gold.put(player.getUniqueId(), plugin.gold.get(player.getUniqueId())-50);
						List<String> buffs = plugin.buffs.get(player.getUniqueId());
						buffs.add("haste");
						plugin.buffs.put(player.getUniqueId(), buffs);
						map.ReadBuffs(player, plugin);
					}
				}
				
				if(clicked.getType() == Material.FISHING_ROD) {
					event.setCancelled(true); // Make it so the dirt is back in its original spot
					player.closeInventory(); // Closes there inventory
					if (plugin.gold.get(player.getUniqueId()) >= 30) {
						plugin.gold.put(player.getUniqueId(), plugin.gold.get(player.getUniqueId())-30);
						ItemStack rod = new ItemStack(Material.FISHING_ROD);
				    	rod.setDurability((short) 10);
				    	player.getInventory().addItem(rod);
					}
				}
				
				if(clicked.getType() == Material.SNOW_BLOCK) {
					event.setCancelled(true); // Make it so the dirt is back in its original spot
					player.closeInventory(); // Closes there inventory
					if (plugin.gold.get(player.getUniqueId()) >= 5) {
						plugin.gold.put(player.getUniqueId(), plugin.gold.get(player.getUniqueId())-5);
				    	player.getInventory().addItem(new ItemStack(Material.SNOW_BLOCK));
					}
				}
				plugin.boards.get(player.getUniqueId()).updateLine(3, "Gold: " + plugin.gold.get(player.getUniqueId()));
			}
		}
	}
	
	@EventHandler 
	public void onBlockPlace(BlockPlaceEvent event){
		GameMap map = plugin.getMap(event.getPlayer());
		if(map != null) {
			Block block = event.getBlock();
			if(Math.floor(block.getLocation().getX()) == Math.floor(map.spawn_loc_1.getX()) && Math.floor(block.getLocation().getZ()) == Math.floor(map.spawn_loc_1.getZ()) && Math.floor(block.getLocation().getY()) >= Math.floor(map.spawn_loc_1.getY())) {
				event.getPlayer().sendMessage("You cannot place a block at the spawn point");
				event.setCancelled(true);
			}
			if(Math.floor(block.getLocation().getX()) == Math.floor(map.spawn_loc_2.getX()) && Math.floor(block.getLocation().getZ()) == Math.floor(map.spawn_loc_2.getZ()) && Math.floor(block.getLocation().getY()) >= Math.floor(map.spawn_loc_2.getY())) {
				event.getPlayer().sendMessage("You cannot place a block at the spawn point");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			GameMap map = plugin.getMap(player);
			if(map != null) {
				if (event instanceof EntityDamageByEntityEvent) {
					EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
					if(e.getDamager() instanceof Player) {
						Player damagePlayer = (Player) e.getDamager();
						if(map.team_1.contains(player.getUniqueId()) && map.team_1.contains(damagePlayer.getUniqueId())) {
							event.setCancelled(true);
						}
						else if(map.team_2.contains(player.getUniqueId()) && map.team_2.contains(damagePlayer.getUniqueId())) {
							event.setCancelled(true);
						}else if(map.spawn_prot.contains(player.getUniqueId())){
							event.setCancelled(true);
						}else {
							map.lastHit.put(player.getUniqueId(), damagePlayer);
						}
					}
				}
				if(event.getDamage() > player.getHealth()){
					if(map.lastHit.containsKey(player.getUniqueId())) {
						Player damagePlayer = map.lastHit.get(player.getUniqueId());
						FastBoard board = plugin.boards.get(damagePlayer.getUniqueId());
						plugin.gold.put(damagePlayer.getUniqueId(), plugin.gold.get(damagePlayer.getUniqueId()) + 10);
						board.updateLine(3, "Gold: " + plugin.gold.get(damagePlayer.getUniqueId()));
						damagePlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 3));
						damagePlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0));
						
					}
					map.lastHit.remove(player.getUniqueId());
					event.setCancelled(true);
					player.setGameMode(GameMode.SPECTATOR);
				    for(PotionEffect potioneffect : player.getActivePotionEffects()) {
				    	player.removePotionEffect(potioneffect.getType());
				    }
				    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 9999999, 255));
				    map.spawn_prot.add(player.getUniqueId());
					if(map.team_1.contains(player.getUniqueId())) {
							
						BukkitScheduler scheduler = plugin.getServer().getScheduler();
					    scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
					    	@Override
					        public void run() {
					    		map.GivePlayerRed(player, plugin);
					        }
					    }, 20L);
					}
					
					if(map.team_2.contains(player.getUniqueId())) {
						
						BukkitScheduler scheduler1 = plugin.getServer().getScheduler();
					    scheduler1.scheduleSyncDelayedTask(plugin, new Runnable() {
					    	@Override
					        public void run() {
					    		map.GivePlayerBlue(player, plugin);
					        }
				        }, 20L);		
					}
					BukkitScheduler scheduler1 = plugin.getServer().getScheduler();
				    scheduler1.scheduleSyncDelayedTask(plugin, new Runnable() {
				    	@Override
				        public void run() {
				    		map.spawn_prot.remove(player.getUniqueId());
				        }
				    }, 40L);
			    }
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeathDrop(PlayerDeathEvent e) {
		if(e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			GameMap map = plugin.getMap(player);
			if(map != null) {
				e.getDrops().clear();
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		GameMap map = plugin.getMap(event.getPlayer());
		if(map != null) {
			Block block = event.getBlock();
			Player player = event.getPlayer();
			Material type = block.getType();
			Byte data = block.getData();
			if(block.getType() != Material.STAINED_GLASS && block.getType() != Material.SNOW_BLOCK) {
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
	public void onPlayerInteract(PlayerInteractEvent event) {
		GameMap map = plugin.getMap(event.getPlayer());
		if(map != null) {
			Action eventAction = event.getAction();
			Player player = event.getPlayer();
			if(eventAction == Action.RIGHT_CLICK_BLOCK || eventAction == Action.RIGHT_CLICK_AIR) {
				Block block = event.getPlayer().getTargetBlock((Set<Material>) null, 3);
				if (block != null && block.getType() == Material.STANDING_BANNER) {
					if(block.getData() == (byte) 8) {
						if(player.getInventory().getHelmet().getType() == Material.BANNER) {
							if(map.team_1.contains(player.getUniqueId())) {
								
								plugin.gold.put(player.getUniqueId(), plugin.gold.get(player.getUniqueId()) + 10);
								
								ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
								
								LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
								helmLeatherMeta.setColor(Color.RED);
								helm.setItemMeta(helmLeatherMeta);
								
								player.getInventory().setHelmet(helm);
								
								map.team_1_caps += 1;
								
								Bukkit.broadcastMessage("Team 1 has captured a flag");
								
								if(map.team_1_caps == 2) {
									Bukkit.broadcastMessage("Team 1 has won");
									
									map.End(plugin);
								}
							}
						}else {
							if(map.team_2.contains(player.getUniqueId())) {
								if(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
									player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
								}
								player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 1));
								player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0));
								plugin.gold.put(player.getUniqueId(), plugin.gold.get(player.getUniqueId()) + 10);
								
								Bukkit.broadcastMessage("Team 2 has picked up the flag");
							
								ItemStack banner = new ItemStack(Material.BANNER, 1, (byte) 1);
							
								player.getInventory().setHelmet(banner);
							}
							
						}
					}else {
						if(player.getInventory().getHelmet().getType() == Material.BANNER) {
							if(map.team_2.contains(player.getUniqueId())) {
								
								ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
								
								LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
								helmLeatherMeta.setColor(Color.BLUE);
								helm.setItemMeta(helmLeatherMeta);
								
								player.getInventory().setHelmet(helm);
								
								map.team_2_caps += 1;
								
								plugin.gold.put(player.getUniqueId(), plugin.gold.get(player.getUniqueId()) + 10);
								
								if(map.team_2_caps == 2) {
									Bukkit.broadcastMessage("Team 2 has won");
	
									map.End(plugin);
								}
							}
							
						}else {
							if(map.team_1.contains(player.getUniqueId())) {
								if(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
									player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
								}
								player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 1));
								player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0));
								plugin.gold.put(player.getUniqueId(), plugin.gold.get(player.getUniqueId()) + 10);
								
								Bukkit.broadcastMessage("Team 1 has picked up the flag");
								
								ItemStack banner = new ItemStack(Material.BANNER, 1, (byte) 4);
								
								player.getInventory().setHelmet(banner);
							}
						}
					}
				}
				if(event.getPlayer().getItemInHand().getType() == Material.NETHER_STAR) {
		        	Player player1 = event.getPlayer();
		        	
		        	player1.openInventory(plugin.shop);
				}
			}
		}
	}
}
