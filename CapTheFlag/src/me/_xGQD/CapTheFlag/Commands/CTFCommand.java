package me._xGQD.CapTheFlag.Commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me._xGQD.CapTheFlag.Main;
import me._xGQD.CapTheFlag.Classes.GameMap;

public class CTFCommand implements CommandExecutor{
	
	private Main plugin;
	
	public CTFCommand(Main plugin) {
		this.plugin = plugin;
		
		plugin.getCommand("ctf").setExecutor(this);
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage("CAPTURE THE FLAG");
        	sender.sendMessage("---------------------");
        	sender.sendMessage("/ctf maps - shows all maps");
        	sender.sendMessage("/ctf join [map] - teleports you to that map");
        	sender.sendMessage("/ctf help [map] - help message");
        	if(sender.hasPermission("capturetheflag.*")) {
	        	sender.sendMessage("/ctf start [map] - starts the game for that map");
	        	sender.sendMessage("/ctf end [map] - ends the game for that map");
	        	sender.sendMessage("/ctf open [map] - allows people to do /ctf join for that map");
	        	sender.sendMessage("/ctf close [map] - stops people from joining the map");
	        	sender.sendMessage("/ctf create [map] - creates a new map");
	        	sender.sendMessage("/ctf delete [map] - deletes a new map");
	        	sender.sendMessage("/ctf setspawn [map] [1 or 2](the side) - sets the spawn for the map");
        	}
		}
		if(args.length >= 1){
			String subcmd = args[0].toLowerCase();
			switch (subcmd){
				case "maps":
					for(GameMap map : plugin.maps) {
						sender.sendMessage(map.name);
					}
					break;
		    	case "join":
		    		if(args.length == 2) {
			    		if (!(sender instanceof Player)) {
			    			sender.sendMessage("Only players can execute this command");
							return true;
						}
			    		Player player = (Player) sender;
			    		if(player.hasPermission("capturetheflag.play")) {
			        		for(int i = 0; i < plugin.maps.size(); i++) {
			        			GameMap map = plugin.maps.get(i);
								if(map.name.equalsIgnoreCase(args[1])) {
									if(map.open) {
							    		plugin.buffs.put(player.getUniqueId(), new ArrayList<String>());
							    		if(map.team_2.size() >= map.team_1.size()) {
							    			if(!map.team_2.contains(player.getUniqueId())) {
							    				map.Box1(Material.BARRIER);
							    				map.team_1.add(player.getUniqueId());
							    				map.GivePlayerRed(player, plugin);
									    		plugin.boards.get(player.getUniqueId()).updateLine(3, "Gold: " + 0);
							              
							    			} else {
							    				map.Box2(Material.BARRIER);
							    				map.GivePlayerBlue(player, plugin);
							    			}
							    		}else{
							    			if(!map.team_1.contains(player.getUniqueId())) {
							    				map.Box2(Material.BARRIER);
							    				map.team_2.add(player.getUniqueId());
							    				map.GivePlayerBlue(player, plugin);
									    		plugin.boards.get(player.getUniqueId()).updateLine(3, "Gold: " + 0);
							              
							    			}else {
							    				map.Box1(Material.BARRIER);
							    				map.GivePlayerRed(player, plugin);
							    			}
							    		}
							    		plugin.gold.put(player.getUniqueId(), 0);
									}
						         	plugin.maps.set(i, map);
								}
							}
			    		}else {
			    			player.sendMessage("You dont have permission to do that");
			    		}
		    		}
		    		break;
		    	case "open":
		    		if(args.length == 2) {
			    		if(sender.hasPermission("capturetheflag.*")) {
			        		for(int i = 0; i < plugin.maps.size(); i++) {
			        			GameMap map = plugin.maps.get(i);
								if(map.name.equalsIgnoreCase(args[1])) {
									map.open = true;
						         	plugin.maps.set(i, map);
								}
							}
			    			
			    			sender.sendMessage("You opened the game");
			    		}else {
			    			sender.sendMessage("You dont have permission to do that");
			    		}
		    		}
		    		break;
		    	case "close":
		    		if(sender.hasPermission("capturetheflag.*")) {
		        		for(int i = 0; i < plugin.maps.size(); i++) {
		        			GameMap map = plugin.maps.get(i);
							if(map.name.equalsIgnoreCase(args[1])) {
								map.open = false;
					         	plugin.maps.set(i, map);
							}
						}
		    			sender.sendMessage("You closed the game");
		    		}else {
		    			sender.sendMessage("You dont have permission to do that");
		    		}
		    		break;
		        case "start":
		        	if(sender.hasPermission("capturetheflag.*")) {
		        		for(int i = 0; i < plugin.maps.size(); i++) {
		        			GameMap map = plugin.maps.get(i);
							if(map.name.equalsIgnoreCase(args[1])) {
								map.open = false;
					         	map.start = true;
					         	map.lastHit.clear();
					         	map.team_1_caps = 0;
					         	map.team_2_caps = 0;
					         	map.Box1(Material.AIR);
					         	map.Box2(Material.AIR);
					         	plugin.maps.set(i, map);
					         	sender.sendMessage("You started the game");
							}
						}
		        	}else {
		        		sender.sendMessage("You do not have permission to run this command");
		        	}
		         	break;
		        case "end":
		        	if(sender.hasPermission("capturetheflag.*")) {
		        		for(int i = 0; i < plugin.maps.size(); i++) {
		        			GameMap map = plugin.maps.get(i);
							if(map.name.equalsIgnoreCase(args[1])) {
								map.End(plugin);
					         	plugin.maps.set(i, map);
							}
						}
						sender.sendMessage("You ended the game");
		        	}else {
		        		sender.sendMessage("You dont have permission to do that");
		        	}
					break;
		        case "help":
		        	sender.sendMessage("CAPTURE THE FLAG");
		        	sender.sendMessage("---------------------");
		        	sender.sendMessage("/ctf maps - shows all maps");
		        	sender.sendMessage("/ctf join [map] - teleports you to that map");
		        	sender.sendMessage("/ctf help [map] - help message");
		        	if(sender.hasPermission("capturetheflag.*")) {
			        	sender.sendMessage("/ctf start [map] - starts the game for that map");
			        	sender.sendMessage("/ctf end [map] - ends the game for that map");
			        	sender.sendMessage("/ctf open [map] - allows people to do /ctf join for that map");
			        	sender.sendMessage("/ctf close [map] - stops people from joining the map");
			        	sender.sendMessage("/ctf create [map] - creates a new map");
			        	sender.sendMessage("/ctf delete [map] - deletes a new map");
			        	sender.sendMessage("/ctf setspawn [map] [1 or 2](the side) - sets the spawn for the map");
		        	}
		        	break;
		        case "create":
		        	if(sender.hasPermission("capturetheflag.*")) {
			        	if(args.length == 2) {
			        		if (plugin.config.contains(args[1])) {
			        			sender.sendMessage("Already a map");
			        		}else {
			        			plugin.maps.add(new GameMap(args[1]));
			        			plugin.config.createSection(args[1]);
			        			plugin.config.options().copyDefaults(true);
			        			plugin.saveConfig();
			        			sender.sendMessage("Done");
			        		}
			        	}else {
			        		sender.sendMessage("Not the correct amount of arguments");
			        	}
		        	}else {
		        		sender.sendMessage("You do not haver permssion to do this command");
		        	}
		        	
		        	break;
		        case "delete":
		        	if(sender.hasPermission("capturetheflag.*")) {
			        	if(args.length == 2) {
			        		if (plugin.config.contains(args[1])) {
			        			for(int i = 0; i < plugin.maps.size(); i++) {
			        				if(plugin.maps.get(i).name.equalsIgnoreCase(args[1])) {
			        					plugin.maps.remove(i);
			        					plugin.config.set(args[1], null);
					        			plugin.config.options().copyDefaults(true);
					        			plugin.saveConfig();
					        			sender.sendMessage("Done");
			        				}
			        			}
			        		}else {
			        			sender.sendMessage("Not a map");
			        		}
			        	}else {
			        		sender.sendMessage("Not the correct amount of arguments");
			        	}
		        	}else {
		        		sender.sendMessage("You do not have permission to do this command");
		        	}
		        	
		        	break;
		        case "setspawn":
		        	if (!(sender instanceof Player)) {
		        		sender.sendMessage("Only players can execute this command");
		        		return true;
		        	}
		        	Player player1 = (Player) sender;
		        	if(player1.hasPermission("capturetheflag.*")){
		        		if(args.length == 3){
		        			player1.sendMessage("Correct Number of args");
			        		for(int i = 0; i < plugin.maps.size(); i++) {
			        			GameMap map = plugin.maps.get(i);
			        			player1.sendMessage(map.name);
								if(map.name.equalsIgnoreCase(args[1])) {
				        			if(args[2].equalsIgnoreCase("1")) {
				        				map.spawn_loc_1 = player1.getLocation();
				        				plugin.config.getConfigurationSection(args[1]).set("spawn_loc_1_w", player1.getLocation().getWorld().getName());
				        				plugin.config.getConfigurationSection(args[1]).set("spawn_loc_1_x", player1.getLocation().getX());
				        				plugin.config.getConfigurationSection(args[1]).set("spawn_loc_1_y", player1.getLocation().getY());
				        				plugin.config.getConfigurationSection(args[1]).set("spawn_loc_1_z", player1.getLocation().getZ());
				        				player1.sendMessage("Done");
				        			}else {
				        				map.spawn_loc_2 = player1.getLocation();
				        				plugin.config.getConfigurationSection(args[1]).set("spawn_loc_2_w", player1.getLocation().getWorld().getName());
				        				plugin.config.getConfigurationSection(args[1]).set("spawn_loc_2_x", player1.getLocation().getX());
				        				plugin.config.getConfigurationSection(args[1]).set("spawn_loc_2_y", player1.getLocation().getY());
				        				plugin.config.getConfigurationSection(args[1]).set("spawn_loc_2_z", player1.getLocation().getZ());
				        				player1.sendMessage("Done");
				        			}
				        			plugin.maps.set(i, map);
				        			plugin.config.options().copyDefaults(true);
				        			plugin.saveConfig();
				        		}
							}
			        	}else{
			        		player1.sendMessage("Not the correct amount of arguments");
			       			return true;
			       		}  
		        	}else{
		        		player1.sendMessage("You do not have permission to set the spawn");
		        		return true;
		        	}
		        	break;
		        case "shop":
		        	if (!(sender instanceof Player)) {
		        		sender.sendMessage("Only players can execute this command");
		        		return true;
		        	}
		        	Player player2 = (Player) sender;
		        	
		        	player2.openInventory(plugin.shop);
		        	break;
		        default:
		        	sender.sendMessage("That command does not exist");
		        	return true;
			}
		}
		return false;
	}
}