package me._xGQD.ultragm.gamemodes;

import me._xGQD.ultragm.FastBoard.FastBoard;
import me._xGQD.ultragm.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Map {
    Main plugin;
    String name;
    String type = "Base";

    public Location spawn[];
    public int autostart = 0;
    boolean open = false;
    boolean start = false;

    HashMap<UUID, Integer> teams = new HashMap<>();
    int team_1_count = 0;
    int team_2_count = 0;
    public Map(Main plugin, String name){
        this.plugin = plugin;
        this.name = name;
        this.spawn = new Location[]{null, null};
    }
    public void setspawn(Player player, String num){
        if(num.equals("1")){
            spawn[0] = player.getLocation();
        }else if(num.equals("2")){
            spawn[1] = player.getLocation();
        }
    }
    public void spawn(Player player, int team){ }
    public void death(Player player, int team){ }
    public boolean in(Player player){
        return teams.containsKey(player.getUniqueId());
    }
    public void join(Player player){
        if(open && !teams.containsKey(player.getUniqueId())){
            plugin.getServer().getConsoleSender().sendMessage(String.valueOf(team_1_count > team_2_count));
            if(team_1_count <= team_2_count){
                teams.put(player.getUniqueId(), 0);
                team_1_count+=1;
            }else{
                teams.put(player.getUniqueId(), 1);
                team_2_count+=1;
            }
            spawn(player, teams.get(player.getUniqueId()));
            plugin.players.put(player.getUniqueId(), plugin.maps.indexOf(this));
        }
        if(autostart == (team_1_count + team_2_count)){
            close();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    start();
                }
            }, 60L);
        }
    }
    public void open(){
        open = true;
    }
    public void close(){
        open = false;
    }
    public void start(){
        open = false;
        start = true;
    }
    public void end(){
        start = false;
        teams.clear();
        File file = new File(plugin.getDataFolder(), name + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
            load(config);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    public void load(FileConfiguration config){
        String world_name = config.getString("World");
        plugin.getServer().getConsoleSender().sendMessage(world_name);
        ConfigurationSection spawn_1_sec = config.getConfigurationSection("spawn_1");
        ConfigurationSection spawn_2_sec = config.getConfigurationSection("spawn_2");
        World world = plugin.getServer().getWorld(world_name);
        spawn[0] = new Location(world, spawn_1_sec.getDouble("x"), spawn_1_sec.getDouble("y"), spawn_1_sec.getDouble("z"));
        spawn[1] = new Location(world, spawn_2_sec.getDouble("x"), spawn_2_sec.getDouble("y"), spawn_2_sec.getDouble("z"));
        ConfigurationSection map = config.getConfigurationSection("Map");
        for (String key : map.getKeys(false)){
            ConfigurationSection block = map.getConfigurationSection(key);
            int x = block.getInt("x");
            int y = block.getInt("y");
            int z = block.getInt("z");
            Block bl = world.getBlockAt(x, y, z);
            bl.setType(Material.getMaterial(block.getString("type")));
            bl.setData((byte) Integer.parseInt(block.getString("data")));
        }

    }
    public void save(Location pos_1, Location pos_2, Location[] newspawns){
        File file = new File(plugin.getDataFolder(), name + ".yml");
        if(!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        YamlConfiguration config = new YamlConfiguration();
        try{
            config.load(file);
            config.set("World", pos_1.getWorld().getName());
            config.set("Type", type);
            int x1 = pos_1.getBlockX();
            int y1 = pos_1.getBlockY();
            int z1 = pos_1.getBlockZ();
            int x2 = pos_2.getBlockX();
            int y2 = pos_2.getBlockY();
            int z2 = pos_2.getBlockZ();
            int xl = x1;
            int xh = x2;
            int yl = y1;
            int yh = y2;
            int zl = z1;
            int zh = z2;
            if(x1 > x2){
                xl = x2;
                xh = x1;
            }
            if(y1 > y2){
                yl = y2;
                yh = y1;
            }
            if(z1 > z2){
                zl = z2;
                zh = z1;
            }

            ConfigurationSection spawn_1_sec = config.createSection("spawn_1");
            spawn_1_sec.set("x", newspawns[0].getX());
            spawn_1_sec.set("y", newspawns[0].getY());
            spawn_1_sec.set("z", newspawns[0].getZ());

            ConfigurationSection spawn_2_sec = config.createSection("spawn_2");
            spawn_2_sec.set("x", newspawns[1].getX());
            spawn_2_sec.set("y", newspawns[1].getY());
            spawn_2_sec.set("z", newspawns[1].getZ());

            ConfigurationSection map = config.createSection("Map");

            int counter = 0;
            for(int x = xl; x <= xh; x++){
                for(int y = yl; y <= yh; y++){
                    for(int z = zl; z <= zh; z++){
                        ConfigurationSection section = map.createSection(String.valueOf(counter));
                        section.set("x", x);
                        section.set("y", y);
                        section.set("z", z);
                        section.set("type", pos_1.getWorld().getBlockAt(x, y, z).getType().toString());
                        section.set("data", String.valueOf(pos_1.getWorld().getBlockAt(x, y, z).getData()));
                        counter++;
                    }
                }
            }
            config.save(file);
        }catch(IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
    }
    public void boardChange(Player player, FastBoard board){}
    public void onBlockPlace(BlockPlaceEvent event){ }
    public void onHit(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            int team = teams.get(player.getUniqueId());
            if(event.getDamage() >= player.getHealth()){
                event.setCancelled(true);
                death(player, team);

                spawn(player, team);
            }
        }
    }
    public void onPlayerDrop(PlayerDropItemEvent event){
        event.setCancelled(true);
    }
    public void onPlayerDeathDrop(PlayerDeathEvent event){ }
    public void onBlockBreak(BlockBreakEvent event){ }
    public void onPlayerInteract(PlayerInteractEvent event){ }
    public boolean buy(Player player, ItemStack item){return true;}
    public String getName(){
        return name;
    }
    public String getType(){
        return type;
    }
    public boolean isOpen(){
        return open;
    }

    public boolean isStart() {
        return start;
    }
}
