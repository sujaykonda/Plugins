package me._xGQD.CapTheFlag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Statistic;
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
	
	// Fired when plugin is first enabled
    @Override
    public void onEnable() {
    	
    	new CTFCommand(this);
    	new PositionListener(this);
    	
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
