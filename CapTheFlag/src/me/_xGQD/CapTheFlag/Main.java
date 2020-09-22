package me._xGQD.CapTheFlag;

import java.io.ObjectInputFilter.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me._xGQD.CapTheFlag.Classes.GameMap;
import me._xGQD.CapTheFlag.Commands.CTFCommand;
import me._xGQD.CapTheFlag.FastBoard.FastBoard;
import me._xGQD.CapTheFlag.Listeners.PositionListener;

public class Main extends JavaPlugin{
	
	
	public List<GameMap> maps = new ArrayList<GameMap>();
	public final Map<UUID, FastBoard> boards = new HashMap<>();
	public final Map<UUID, Integer> gold = new HashMap<>();
	public final Map<UUID, List<String>> buffs = new HashMap<>();
	public Inventory shop;
	public FileConfiguration config;
	// Fired when plugin is first enabled
    @Override
    public void onEnable() {
    	
    	config = this.getConfig();
    	
    	for(String key : config.getKeys(false)) {
            ConfigurationSection map = config.getConfigurationSection(key);
            GameMap loadedMap = new GameMap(key);
            if(map.contains("spawn_loc_1_w") && map.contains("spawn_loc_2_w")) {
            	loadedMap.spawn_loc_1 = new Location(getServer().getWorld(map.getString("spawn_loc_1_w")), map.getDouble("spawn_loc_1_x"), map.getDouble("spawn_loc_1_y"), map.getDouble("spawn_loc_1_z"));
            	loadedMap.spawn_loc_2 = new Location(getServer().getWorld(map.getString("spawn_loc_2_w")), map.getDouble("spawn_loc_2_x"), map.getDouble("spawn_loc_2_y"), map.getDouble("spawn_loc_2_z"));
            }
            maps.add(loadedMap);
        }
    	
    	new CTFCommand(this);
    	new PositionListener(this);
    	
    	shop = Bukkit.createInventory(null, 9);
    	
    	ItemStack permchain = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
    	ItemStack permsword = new ItemStack(Material.GOLD_SWORD);
    	ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
    	ItemStack iron = new ItemStack(Material.IRON_CHESTPLATE);
    	ItemStack haste = new ItemStack(Material.GOLD_PICKAXE);
    	ItemStack rod = new ItemStack(Material.FISHING_ROD);
    	ItemStack snow = new ItemStack(Material.SNOW_BLOCK);
    	
    	permsword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
    	
    	ItemMeta permchainmeta = permchain.getItemMeta();
    	permchainmeta.setDisplayName("Chain Armor");
    	List<String> permchainlore = new ArrayList<String>();
    	permchainlore.add("Permanent Chain Mail Chestplate, Leggings, and Boots");
    	permchainlore.add("Cost 100");
    	permchainmeta.setLore(permchainlore);
    	permchain.setItemMeta(permchainmeta);
    	
    	ItemMeta permswordmeta = permsword.getItemMeta();
    	permswordmeta.setDisplayName("Sharp 2 Gold Sword");
    	List<String> permswordlore = new ArrayList<String>();
    	permswordlore.add("Permanent Sharpness 2 Gold Sword");
    	permswordlore.add("Cost 50 Gold");
    	permswordmeta.setLore(permswordlore);
    	permsword.setItemMeta(permswordmeta);
    	
    	ItemMeta swordmeta = sword.getItemMeta();
    	swordmeta.setDisplayName("Diamond Sword");
    	List<String> swordlore = new ArrayList<String>();
    	swordlore.add("Temporary Diamond Sword for until you die");
    	swordlore.add("Cost 50 Gold");
    	swordmeta.setLore(swordlore);
    	sword.setItemMeta(swordmeta);
    	
    	ItemMeta ironmeta = iron.getItemMeta();
    	ironmeta.setDisplayName("Iron Armor");
    	List<String> ironlore = new ArrayList<String>();
    	ironlore.add("Temporary Iron Chestplate, Leggings, and Boots");
    	ironlore.add("Cost 100 Gold");
    	ironmeta.setLore(ironlore);
    	iron.setItemMeta(ironmeta);
    	
    	ItemMeta hastemeta = haste.getItemMeta();
    	hastemeta.setDisplayName("Haste 3");
    	List<String> hastelore = new ArrayList<String>();
    	hastelore.add("Permanent Haste 3");
    	hastelore.add("Cost 50 Gold");
    	hastemeta.setLore(hastelore);
    	haste.setItemMeta(hastemeta);
    	
    	ItemMeta rodmeta = rod.getItemMeta();
    	rodmeta.setDisplayName("Fishing Rod");
    	List<String> rodlore = new ArrayList<String>();
    	rodlore.add("Temporary Fishing Rod");
    	rodlore.add("Cost 30 Gold");
    	rodmeta.setLore(rodlore);
    	rod.setItemMeta(rodmeta);
    	
    	rod.setDurability((short) 10);
    	
    	ItemMeta snowmeta = snow.getItemMeta();
    	snowmeta.setDisplayName("Snow Blocks");
    	List<String> snowlore = new ArrayList<String>();
    	snowlore.add("Temporary Snow Blocks Longer Break Time");
    	snowlore.add("Cost 5 Gold (each)");
    	snowmeta.setLore(snowlore);
    	snow.setItemMeta(snowmeta);
    	
    	shop.setItem(0, permchain);
    	shop.setItem(1, permsword);
    	shop.setItem(2, iron);
    	shop.setItem(3, sword);
    	shop.setItem(4, haste);
    	shop.setItem(5, rod);
    	shop.setItem(6, snow);
    	
    	getServer().getScheduler().runTaskTimer(this, () -> {
            for (FastBoard board : boards.values()) {
                updateBoard(board);
            }
        }, 0, 20);
    }
    
    private void updateBoard(FastBoard board) {
    	board.updateLine(1, ChatColor.RED + "Online: " + ChatColor.GRAY + getServer().getOnlinePlayers().size());
	}

	// Fired when plugin is disabled
    @Override
    public void onDisable() {
    	
    }
    
    public GameMap getMap(Player player) {
    	for(GameMap map: maps) {
    		if(map.PlayerIn(player)) {
    			return map;
    		}
    	}
    	return null;
    }
}
