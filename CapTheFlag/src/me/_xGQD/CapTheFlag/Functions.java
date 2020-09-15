package me._xGQD.CapTheFlag;

import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import me._xGQD.CapTheFlag.FastBoard.FastBoard;

public class Functions {
	public static void End(Main plugin) {
    	plugin.start = false;
    	for (FastBoard board : plugin.boards.values()) {
            board.updateLines("",
            		          "",
            		          "",
            		          "",
            		          "");
        }
		plugin.team_1.clear();
		plugin.team_2.clear();
	}
	
	public static Location RoundLoc(Main plugin, Location location) {
		return plugin.spawn_loc_1.getWorld().getBlockAt(location).getLocation();
	}
	public static void Box1(Main plugin, Material m) {
		plugin.spawn_loc_1.getWorld().getBlockAt(new Location(plugin.spawn_loc_1.getWorld(), plugin.spawn_loc_1.getX()-1, plugin.spawn_loc_1.getY(), plugin.spawn_loc_1.getZ())).setType(m);
		plugin.spawn_loc_1.getWorld().getBlockAt(new Location(plugin.spawn_loc_1.getWorld(), plugin.spawn_loc_1.getX()+1, plugin.spawn_loc_1.getY(), plugin.spawn_loc_1.getZ())).setType(m);
		plugin.spawn_loc_1.getWorld().getBlockAt(new Location(plugin.spawn_loc_1.getWorld(), plugin.spawn_loc_1.getX(), plugin.spawn_loc_1.getY(), plugin.spawn_loc_1.getZ()-1)).setType(m);
		plugin.spawn_loc_1.getWorld().getBlockAt(new Location(plugin.spawn_loc_1.getWorld(), plugin.spawn_loc_1.getX(), plugin.spawn_loc_1.getY(), plugin.spawn_loc_1.getZ()+1)).setType(m);
		plugin.spawn_loc_1.getWorld().getBlockAt(new Location(plugin.spawn_loc_1.getWorld(), plugin.spawn_loc_1.getX(), plugin.spawn_loc_1.getY()+2, plugin.spawn_loc_1.getZ())).setType(m);
	}

	public static void Box2(Main plugin, Material m) {
		plugin.spawn_loc_2.getWorld().getBlockAt(new Location(plugin.spawn_loc_2.getWorld(), plugin.spawn_loc_2.getX()-1, plugin.spawn_loc_2.getY(), plugin.spawn_loc_2.getZ())).setType(m);
		plugin.spawn_loc_2.getWorld().getBlockAt(new Location(plugin.spawn_loc_2.getWorld(), plugin.spawn_loc_2.getX()+1, plugin.spawn_loc_2.getY(), plugin.spawn_loc_2.getZ())).setType(m);
		plugin.spawn_loc_2.getWorld().getBlockAt(new Location(plugin.spawn_loc_2.getWorld(), plugin.spawn_loc_2.getX(), plugin.spawn_loc_2.getY(), plugin.spawn_loc_2.getZ()-1)).setType(m);
		plugin.spawn_loc_2.getWorld().getBlockAt(new Location(plugin.spawn_loc_2.getWorld(), plugin.spawn_loc_2.getX(), plugin.spawn_loc_2.getY(), plugin.spawn_loc_2.getZ()+1)).setType(m);
		plugin.spawn_loc_2.getWorld().getBlockAt(new Location(plugin.spawn_loc_2.getWorld(), plugin.spawn_loc_2.getX(), plugin.spawn_loc_2.getY()+2, plugin.spawn_loc_2.getZ())).setType(m);
	}
	
	public static void GivePlayerRed(Player player, Main plugin) {
		player.teleport(plugin.spawn_loc_1);
		
		ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		ItemStack glass = new ItemStack(Material.STAINED_GLASS, 64, (short) 14);
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		ItemStack steak = new ItemStack(Material.COOKED_BEEF);
		
		LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
		helmLeatherMeta.setColor(Color.RED);
		helm.setItemMeta(helmLeatherMeta);
		
		LeatherArmorMeta chestplateLeatherMeta = (LeatherArmorMeta)chestplate.getItemMeta();
		chestplateLeatherMeta.setColor(Color.RED);
		chestplate.setItemMeta(chestplateLeatherMeta);
		
		LeatherArmorMeta leggingsLeatherMeta = (LeatherArmorMeta)leggings.getItemMeta();
		leggingsLeatherMeta.setColor(Color.RED);
		leggings.setItemMeta(leggingsLeatherMeta);
		
		LeatherArmorMeta bootsLeatherMeta = (LeatherArmorMeta)boots.getItemMeta();
		bootsLeatherMeta.setColor(Color.RED);
		boots.setItemMeta(bootsLeatherMeta);
		
		
		player.getInventory().clear();
		player.getInventory().setHelmet(helm);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setBoots(boots);
		player.getInventory().setItem(0, sword);
		player.getInventory().setItem(1, glass);
		player.getInventory().setItem(2, steak);
		ReadBuffs(player, plugin);
	}
	
	public static void GivePlayerBlue(Player player, Main plugin) {
		player.teleport(plugin.spawn_loc_2);
		
		ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		ItemStack glass = new ItemStack(Material.STAINED_GLASS, 64, (short) 11);
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		ItemStack steak = new ItemStack(Material.COOKED_BEEF);
		
		
		LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
		helmLeatherMeta.setColor(Color.BLUE);
		helm.setItemMeta(helmLeatherMeta);
		
		LeatherArmorMeta chestplateLeatherMeta = (LeatherArmorMeta)chestplate.getItemMeta();
		chestplateLeatherMeta.setColor(Color.BLUE);
		chestplate.setItemMeta(chestplateLeatherMeta);
		
		LeatherArmorMeta leggingsLeatherMeta = (LeatherArmorMeta)leggings.getItemMeta();
		leggingsLeatherMeta.setColor(Color.BLUE);
		leggings.setItemMeta(leggingsLeatherMeta);
		
		LeatherArmorMeta bootsLeatherMeta = (LeatherArmorMeta)boots.getItemMeta();
		bootsLeatherMeta.setColor(Color.BLUE);
		boots.setItemMeta(bootsLeatherMeta);
		
		player.getInventory().clear();
		player.getInventory().setHelmet(helm);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setBoots(boots);
		player.getInventory().setItem(0, sword);
		player.getInventory().setItem(1, glass);
		player.getInventory().setItem(2, steak);
		ReadBuffs(player, plugin);
	}
	
	public static void ReadBuffs(Player player, Main plugin) {
		List<String> playerbuffs = plugin.buffs.get(player.getUniqueId());
		for(String string : playerbuffs) {
			if(string.equalsIgnoreCase("permsword")) {
				ItemStack permsword = new ItemStack(Material.GOLD_SWORD);
				permsword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
				player.getInventory().setItem(0, permsword);
			}
			if(string.equalsIgnoreCase("permarmor")) {
				player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
				player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_LEGGINGS));
				player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
			}
		}
	}
}
