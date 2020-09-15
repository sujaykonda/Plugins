package me._xGQD.CapTheFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me._xGQD.CapTheFlag.Commands.CTFCommand;
import me._xGQD.CapTheFlag.FastBoard.FastBoard;

public class Main extends JavaPlugin{
	
	public Location spawn_loc_1;
	public Location spawn_loc_2;
	public Set<UUID> team_1 = new HashSet<UUID>();
	public Set<UUID> team_2 = new HashSet<UUID>();
	public Set<UUID> spawn_prot = new HashSet<UUID>();
	public boolean start = false;
	public boolean open = false;
	
	public final Map<UUID, FastBoard> boards = new HashMap<>();
	public final Map<UUID, Integer> gold = new HashMap<>();
	public final Map<UUID, List<String>> buffs = new HashMap<>();
	public Inventory shop;
	// Fired when plugin is first enabled
    @Override
    public void onEnable() {
    	
    	new CTFCommand(this);
    	new PositionListener(this);
    	
    	shop = Bukkit.createInventory(null, 9);
    	
    	ItemStack permchain = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
    	ItemStack permsword = new ItemStack(Material.GOLD_SWORD);
    	ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
    	ItemStack iron = new ItemStack(Material.IRON_CHESTPLATE);
    	permsword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
    	
    	shop.setItem(0, permchain);
    	shop.setItem(2, permsword);
    	shop.setItem(4, iron);
    	shop.setItem(6, sword);
    	
    	getServer().getScheduler().runTaskTimer(this, () -> {
            for (FastBoard board : boards.values()) {
                updateBoard(board);
            }
        }, 0, 20);
    }
    
    private void updateBoard(FastBoard board) {
    	board.updateLine(1, "Online: " + getServer().getOnlinePlayers().size());
	}

	// Fired when plugin is disabled
    @Override
    public void onDisable() {
    	
    }
}
