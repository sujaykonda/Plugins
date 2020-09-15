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
import me._xGQD.CapTheFlag.Functions;

public class CTFCommand implements CommandExecutor{
	
	private Main plugin;
	
	public CTFCommand(Main plugin) {
		this.plugin = plugin;
		
		plugin.getCommand("ctf").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length >= 1){
			String subcmd = args[0].toLowerCase();
			switch (subcmd){
		    	case "play":
		    		if (!(sender instanceof Player)) {
		    			sender.sendMessage("Only players can execute this command");
						return true;
					}
		    		Player player = (Player) sender;
		    		if(player.hasPermission("capturetheflag.play")) {
			    		if(plugin.open) {
				    		if(plugin.team_2.size() >= plugin.team_1.size()) {
				    			if(!plugin.team_2.contains(player.getUniqueId())) {
				    				Functions.Box1(plugin, Material.BARRIER);
				    				plugin.team_1.add(player.getUniqueId());
				    				Functions.GivePlayerRed(player, plugin);
				              
				    			} else {
				    				Functions.Box2(plugin, Material.BARRIER);
				    				Functions.GivePlayerBlue(player, plugin);
				    			}
				    		}else{
				    			if(!plugin.team_1.contains(player.getUniqueId())) {
				    				Functions.Box2(plugin, Material.BARRIER);
				    				plugin.team_2.add(player.getUniqueId());
				    				Functions.GivePlayerBlue(player, plugin);
				              
				    			}else {
				    				Functions.Box1(plugin, Material.BARRIER);
				    				Functions.GivePlayerRed(player, plugin);
				    			}
				    		}
				    		plugin.gold.put(player.getUniqueId(), 0);
				    		plugin.buffs.put(player.getUniqueId(), new ArrayList<String>());
				    		plugin.boards.get(player.getUniqueId()).updateLine(3, "Gold: " + 0);;
			    		}else {
			    			player.sendMessage("The game is not open");
			    		}
		    		}else {
		    			player.sendMessage("You dont have permission to do that");
		    		}
		    		break;
		    	case "open":
		    		if(sender.hasPermission("capturetheflag.*")) {
		    			plugin.open = true;
		    			sender.sendMessage("You opened the game");
		    		}else {
		    			sender.sendMessage("You dont have permission to do that");
		    		}
		    		break;
		    	case "close":
		    		if(sender.hasPermission("capturetheflag.*")) {
		    			plugin.open = false;
		    			sender.sendMessage("You closed the game");
		    		}else {
		    			sender.sendMessage("You dont have permission to do that");
		    		}
		    		break;
		        case "start":
		        	plugin.open = false;
		         	plugin.start = true;
    				Functions.Box1(plugin, Material.AIR);
    				Functions.Box2(plugin, Material.AIR);
		         	
		         	sender.sendMessage("You started the game");
		         	break;
		        case "end":
		        	if(sender.hasPermission("capturetheflag.*")) {
		        		Functions.End(plugin);
						sender.sendMessage("You ended the game");
		        	}else {
		        		sender.sendMessage("You dont have permission to do that");
		        	}
					break;
		        case "setspawn":
		        	if (!(sender instanceof Player)) {
		        		sender.sendMessage("Only players can execute this command");
		        		return true;
		        	}
		        	Player player1 = (Player) sender;
		        	if(player1.hasPermission("capturetheflag.*")){
		        		if(args.length == 2){
		        			if(args[1].equalsIgnoreCase("1")) {
		        				plugin.spawn_loc_1 = player1.getLocation();
		        				player1.sendMessage("Done");
		        			}else {
		        				plugin.spawn_loc_2 = player1.getLocation();
		        				player1.sendMessage("Done");
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