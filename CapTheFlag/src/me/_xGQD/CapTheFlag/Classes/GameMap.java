package me._xGQD.CapTheFlag.Classes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me._xGQD.CapTheFlag.Main;
import me._xGQD.CapTheFlag.FastBoard.FastBoard;

public class GameMap {
	
	public Location spawn_loc_1;
	public Location spawn_loc_2;
	public Set<UUID> team_1 = new HashSet<UUID>();
	public Set<UUID> team_2 = new HashSet<UUID>();
	public Map<UUID, Player> lastHit = new HashMap<UUID, Player>();
	public int team_1_caps = 0;
	public int team_2_caps = 0;
	public Set<UUID> spawn_prot = new HashSet<UUID>();
	public boolean start = false;
	public boolean open = false;
	public String name;
	
	public GameMap(String name, Location spawn_loc_1, Location spawn_loc_2) {
		this.spawn_loc_1 = spawn_loc_1;
		this.spawn_loc_2 = spawn_loc_2;
		this.name = name;
	}
	
	public GameMap(String name) {
		this.name = name;
	}
	
	public boolean PlayerIn(Player player) {
		return (this.team_1.contains(player.getUniqueId()) | this.team_2.contains(player.getUniqueId()));
	}
	
	
	public void GivePlayerRed(Player player, Main plugin) {
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.teleport(this.spawn_loc_1);
		
		ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		ItemStack glass = new ItemStack(Material.STAINED_GLASS, 64, (short) 14);
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		ItemStack steak = new ItemStack(Material.COOKED_BEEF);
		ItemStack shop = new ItemStack(Material.NETHER_STAR);
		
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
		
		ItemMeta shopmeta = shop.getItemMeta();
		shopmeta.setDisplayName("Shop");
		shop.setItemMeta(shopmeta);
		
		player.getInventory().clear();
		player.getInventory().setHelmet(helm);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setBoots(boots);
		player.getInventory().setItem(0, sword);
		player.getInventory().setItem(1, glass);
		player.getInventory().setItem(2, steak);
		player.getInventory().setItem(8, shop);
		this.ReadBuffs(player, plugin);
		
	}
	
	public void GivePlayerBlue(Player player, Main plugin) {
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.teleport(this.spawn_loc_2);
		
		ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
		ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		ItemStack glass = new ItemStack(Material.STAINED_GLASS, 64, (short) 11);
		ItemStack sword = new ItemStack(Material.IRON_SWORD);
		ItemStack steak = new ItemStack(Material.COOKED_BEEF);
		ItemStack shop = new ItemStack(Material.NETHER_STAR);
		
		
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
		
		ItemMeta shopmeta = shop.getItemMeta();
		shopmeta.setDisplayName("Shop");
		shop.setItemMeta(shopmeta);
		
		player.getInventory().clear();
		player.getInventory().setHelmet(helm);
		player.getInventory().setChestplate(chestplate);
		player.getInventory().setLeggings(leggings);
		player.getInventory().setBoots(boots);
		player.getInventory().setItem(0, sword);
		player.getInventory().setItem(1, glass);
		player.getInventory().setItem(2, steak);
		player.getInventory().setItem(8, shop);
		this.ReadBuffs(player, plugin);
			
	}
	
	public void ReadBuffs(Player player, Main plugin) {
		List<String> playerbuffs = plugin.buffs.get(player.getUniqueId());
		for(String string : playerbuffs) {
			if(string.equalsIgnoreCase("permsword")) {
				ItemStack permsword = new ItemStack(Material.GOLD_SWORD);
				permsword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
				permsword.addEnchantment(Enchantment.DURABILITY, 3);
				player.getInventory().setItem(0, permsword);
			}
			if(string.equalsIgnoreCase("permarmor")) {
				player.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
				player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
				player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
			}
			if(string.equalsIgnoreCase("haste")) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 9999999, 3));
			}
		}
	} 
	public void End(Main plugin) {
    	this.start = false;
    	for (FastBoard board : plugin.boards.values()) {
            board.updateLines("",
            		          "",
            		          "",
            		          "",
            		          "");
        }
		this.team_1.clear();
		this.team_2.clear();
	}
	
	public Location RoundLoc(Location location) {
		return this.spawn_loc_1.getWorld().getBlockAt(location).getLocation();
	}
	public void Box1(Material m) {
		this.spawn_loc_1.getWorld().getBlockAt(new Location(this.spawn_loc_1.getWorld(), this.spawn_loc_1.getX()-1, this.spawn_loc_1.getY(), this.spawn_loc_1.getZ())).setType(m);
		this.spawn_loc_1.getWorld().getBlockAt(new Location(this.spawn_loc_1.getWorld(), this.spawn_loc_1.getX()+1, this.spawn_loc_1.getY(), this.spawn_loc_1.getZ())).setType(m);
		this.spawn_loc_1.getWorld().getBlockAt(new Location(this.spawn_loc_1.getWorld(), this.spawn_loc_1.getX(), this.spawn_loc_1.getY(), this.spawn_loc_1.getZ()-1)).setType(m);
		this.spawn_loc_1.getWorld().getBlockAt(new Location(this.spawn_loc_1.getWorld(), this.spawn_loc_1.getX(), this.spawn_loc_1.getY(), this.spawn_loc_1.getZ()+1)).setType(m);
		this.spawn_loc_1.getWorld().getBlockAt(new Location(this.spawn_loc_1.getWorld(), this.spawn_loc_1.getX(), this.spawn_loc_1.getY()+2, this.spawn_loc_1.getZ())).setType(m);
	}

	public void Box2(Material m) {
		this.spawn_loc_2.getWorld().getBlockAt(new Location(this.spawn_loc_2.getWorld(), this.spawn_loc_2.getX()-1, this.spawn_loc_2.getY(), this.spawn_loc_2.getZ())).setType(m);
		this.spawn_loc_2.getWorld().getBlockAt(new Location(this.spawn_loc_2.getWorld(), this.spawn_loc_2.getX()+1, this.spawn_loc_2.getY(), this.spawn_loc_2.getZ())).setType(m);
		this.spawn_loc_2.getWorld().getBlockAt(new Location(this.spawn_loc_2.getWorld(), this.spawn_loc_2.getX(), this.spawn_loc_2.getY(), this.spawn_loc_2.getZ()-1)).setType(m);
		this.spawn_loc_2.getWorld().getBlockAt(new Location(this.spawn_loc_2.getWorld(), this.spawn_loc_2.getX(), this.spawn_loc_2.getY(), this.spawn_loc_2.getZ()+1)).setType(m);
		this.spawn_loc_2.getWorld().getBlockAt(new Location(this.spawn_loc_2.getWorld(), this.spawn_loc_2.getX(), this.spawn_loc_2.getY()+2, this.spawn_loc_2.getZ())).setType(m);
	}
}
