package me._xGQD.commandnpc;

import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Main extends JavaPlugin {
    HashMap<String, String> npcs = new HashMap<>();
    @Override
    public void onEnable(){
        List<Location> spawn_locs = new ArrayList<>();
        List<String> cmds = new ArrayList<>();
        List<String> names = new ArrayList<>();
        FileConfiguration config = getConfig();

        new InteractListener(this);
        new CNCommand(this);
        
        for(String sec: config.getKeys(false)){
            ConfigurationSection section = config.getConfigurationSection(sec);
            spawn_locs.add(new Location(getServer().getWorld(section.getString("world")), section.getDouble("x"), section.getDouble("y"), section.getDouble("z")));
            cmds.add(section.getString("cmd"));
            names.add(section.getName());
        }

        for(int i = 0; i < spawn_locs.size(); i++){
            Entity entity = spawn_locs.get(i).getWorld().spawnEntity(spawn_locs.get(i), EntityType.PLAYER);
            entity.setCustomName(names.get(i));
            npcs.put(names.get(i), cmds.get(i));
        }
    }

    @Override
    public void onDisable(){

    }
}
