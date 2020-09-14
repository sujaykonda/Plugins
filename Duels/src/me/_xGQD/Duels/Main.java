package me._xGQD.Duels;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me._xGQD.Duels.Commands.DuelsCommand;
public class Main extends JavaPlugin{
	public int[] pos1 = {-1, -1, -1};
	public int[] pos2 = {-1, -1, -1};
	public World world;
	
	// Fired when plugin is first enabled
    @Override
    public void onEnable() {
  
    	
    	new DuelsCommand(this);
    	new PositionListener(this);
    }
    
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
    	
    }
}
