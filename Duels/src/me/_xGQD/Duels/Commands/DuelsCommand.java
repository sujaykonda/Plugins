package me._xGQD.Duels.Commands;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me._xGQD.Duels.InventoryStringDeSerializer;
import me._xGQD.Duels.Main;

public class DuelsCommand implements CommandExecutor{
	
	private Main plugin;
	FileConfiguration config;
	
	public DuelsCommand(Main plugin) {
		this.plugin = plugin;
		
		config = plugin.getConfig();
		plugin.getCommand("duels").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can execute this command");
			return true;
		}
		Player player = (Player) sender;		
		
		String subcmd;
		
		if(args.length == 0) {
			subcmd = "help";
		}else {
			subcmd = args[0].toLowerCase();
		}
		
		switch(subcmd){
			case "help":	
				player.sendMessage("------------- Help ------------");
				player.sendMessage("-------------------------------");
				player.sendMessage("/duels arena create [arena] [gamemode]");
				player.sendMessage("/duels arena delete [arena]");
				player.sendMessage("/duels arena list");
				player.sendMessage("/duels gamemode create [gamemode]");
				break;
			case "wand":
				ItemStack wand = new ItemStack(Material.STICK);
				ItemMeta wandmeta = wand.getItemMeta();
				wandmeta.setDisplayName("Duels Wand");
				wand.setItemMeta(wandmeta);
				player.getInventory().addItem(wand);
				break;
			case "gm":
			case "gamemode":
				String subsubcmd;
				if(args.length == 1) {
					subsubcmd = "create";
				}else {
					subsubcmd = args[1].toLowerCase();
				}
				switch(subsubcmd) {
					case "create":
						if(args.length == 3) {
							if(!config.contains("Gamemodes")) {
								config.createSection("Gamemodes");
							}
							ConfigurationSection gamemodes = config.getConfigurationSection("Gamemodes");
							
							gamemodes.set(args[3], InventoryStringDeSerializer.InventoryToString(player.getInventory()));
						}
				}
					
			case "ar":
			case "arena":
				String subsubcmd1;
				if(args.length == 1) {
					subsubcmd1 = "create";
				}else {
					subsubcmd1 = args[1].toLowerCase();
				}
				switch(subsubcmd1) {
					case "list":
						if(args.length == 2) {
							Set <String> arenas = config.getConfigurationSection("Maps").getKeys(false);
							Iterator iter = arenas.iterator();
							player.sendMessage("Arenas: ");
							while (iter.hasNext()) {
								player.sendMessage((String) iter.next());
							}
						}
						break;
					case "delete":
						if(args.length == 3) {
							if(!config.getConfigurationSection("Maps").contains(args[1])) {
								player.sendMessage("That map does not exist");
								break;
							}
							
							config.getConfigurationSection("Maps").set(args[1], null);
							player.sendMessage("The arena has been deleted");
						}else {
							player.sendMessage("False number of args");
						}
						plugin.saveConfig();
						break;
					case "create":
						if(args.length == 3) {
							if(plugin.pos1[0] == -1 || plugin.pos2[0] == -1) {
								player.sendMessage("Please select a region that will be the arena");
								break;
							}
							
							if(!config.contains("Maps")) {
								config.createSection("Maps");
							}
							
							ConfigurationSection Maps = config.getConfigurationSection("Maps");
							
							if(Maps.contains(args[1])) {
								player.sendMessage("That map already exists");
								break;
							}
							
							Maps.createSection(args[1]);
							
							Maps.getConfigurationSection(args[1]).createSection("Blocks");
							
							ConfigurationSection Arena = Maps.getConfigurationSection(args[1]).getConfigurationSection("Blocks");
							
							Maps.getConfigurationSection(args[1]).createSection("Gamemodes");
							
							int x1 = plugin.pos1[0];
							int y1 = plugin.pos1[1];
							int z1 = plugin.pos1[2];
							
							int x2 = plugin.pos2[0];
							int y2 = plugin.pos2[1];
							int z2 = plugin.pos2[2];
							
							if(x1 > x2) {
								x1 = plugin.pos2[0];
								x2 = plugin.pos1[0];
							}
							
							if(y1 > y2) {
								y1 = plugin.pos2[1];
								y2 = plugin.pos1[1];
							}
							
							if(z1 > z2) {
								z1 = plugin.pos2[2];
								z2 = plugin.pos1[2];
							}
							for(int x = x1; x < x2; x++) {
								for(int y = y1; y < y2; y++) {
									for(int z = z1; z < z2; z++) {
										player.sendMessage("Saving");
										Arena.set(String.valueOf(x) + ";" + String.valueOf(y) + ";" + String.valueOf(z), plugin.world.getBlockAt(x, y, z).getType().toString() + ":" + ((int) plugin.world.getBlockAt(x, y, z).getData()));
									}
								}
							}
							
							plugin.saveConfig();
							player.sendMessage("Arena " + args[1] + " has been created as gamemode " + args[2]);
		
						}else {
							player.sendMessage("False number of args");
						}
						break;
				}
		}
		return false;
	}
	
}
