package me._xGQD.turtlegm;

import me._xGQD.turtlegm.Maps.CTF.CTFMap;
import me._xGQD.turtlegm.Maps.Map;
import me._xGQD.turtlegm.Maps.RankedCTF.RankedCTFMap;
import me._xGQD.turtlegm.Maps.UltimateCTF.UltimateCTFMap;
import me._xGQD.turtlegm.scoreboard.common.EntryBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {

    public HashMap<UUID, PlayerStats> playerElo;

    HashMap<String, Map> maps;
    HashMap<UUID, String> playerPlace;

    Main plugin = JavaPlugin.getPlugin(Main.class);
    public PlayerManager() throws IOException, InvalidConfigurationException {
        maps = new HashMap<>();
        playerPlace = new HashMap<>();
        playerElo = new HashMap<>();
        FileConfiguration mainConfig = plugin.getConfig();
        for(String uuid: mainConfig.getKeys(false)){
            PlayerStats elo = new PlayerStats();
            for(String name: mainConfig.getConfigurationSection(uuid).getKeys(false)){
                elo.elos.put(name, mainConfig.getConfigurationSection(uuid).getInt(name));
            }
            playerElo.put(UUID.fromString(uuid), elo);
        }
        File maps_dir = new File(plugin.getDataFolder(), "/maps");
        if (!maps_dir.exists())
            maps_dir.mkdirs();
        for(File file: maps_dir.listFiles()){
            YamlConfiguration config = new YamlConfiguration();
            config.load(file);
            if(config.getString("type").equals("CTF")){
                String map_name = file.getName().substring(0, file.getName().length()-4);
                System.out.println(map_name);
                maps.put(map_name, new CTFMap(map_name, true));
            }
            else if(config.getString("type").equals("RCTF")){
                String map_name = file.getName().substring(0, file.getName().length()-4);
                System.out.println(map_name);
                maps.put(map_name, new RankedCTFMap(map_name, true));
            }
            else if(config.getString("type").equals("UCTF")){
                String map_name = file.getName().substring(0, file.getName().length()-4);
                System.out.println(map_name);
                maps.put(map_name, new UltimateCTFMap(map_name, true));
            }
        }
    }
    public PlayerStats getPlayerStats(UUID playerUUID){
        return playerElo.get(playerUUID);
    }
    public void join(Player player){
        playerPlace.put(player.getUniqueId(), "lobby");
    }
    public void disable(){
        for(Map map: maps.values()){
            map.onDisable();
        }
    }
    public boolean isMap(String map){return maps.containsKey(map);}
    public boolean playerIn(Player player){
        return !playerPlace.get(player.getUniqueId()).equals("lobby");
    }
    public void gotoLobby(UUID uuid){
        playerPlace.put(uuid, "lobby");
    }
    public Map getMap(String name){
        return maps.get(name);
    }
    public void setMap(String name, Map map){
        maps.put(name, map);
    }
    public String getMapName(Player player){
        return playerPlace.get(player.getUniqueId());
    }
    public void joinMap(Player player, String map_name){
        if(map_name.equals("lobby")){
            return;
        }
        Map map = maps.get(map_name);
        map.onJoin(player);
        maps.put(map_name, map);
        playerPlace.put(player.getUniqueId(), map_name);
    }

    public void changeSettings(String[] args, CommandSender commandSender, String map_name){
        Map map = maps.get(map_name);
        map.changeSettings(args, commandSender);
        maps.put(map_name, map);
    }
    public void end(Player player, String map_name){
        Map map = maps.get(map_name);
        map.onEnd();
        maps.put(map_name, map);
    }

    public void start(Player player, String map_name){
        Map map = maps.get(map_name);
        map.onStart();
        maps.put(map_name, map);
    }

    public void open(Player player, String map_name){
        Map map = maps.get(map_name);
        map.onOpen();
        maps.put(map_name, map);
    }

    public void close(Player player, String map_name){
        Map map = maps.get(map_name);
        map.onClose();
        maps.put(map_name, map);
    }

    public void playerDrop(PlayerDropItemEvent event){
        String map_name = playerPlace.get(event.getPlayer().getUniqueId());
        Map map = maps.get(map_name);
        map.onPlayerDrop(event);
        maps.put(map_name, map);
    }

    public void playerInteract(PlayerInteractEvent event){
        String map_name = playerPlace.get(event.getPlayer().getUniqueId());
        Map map = maps.get(map_name);
        map.onPlayerInteract(event);
        maps.put(map_name, map);
    }

    public void entityDamage(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player)){
            return;
        }
        Player player = (Player) event.getEntity();
        String map_name = playerPlace.get(player.getUniqueId());
        Map map = maps.get(map_name);
        map.onEntityDamage(event);
        maps.put(map_name, map);
    }

    public void blockPlace(BlockPlaceEvent event){
        String map_name = playerPlace.get(event.getPlayer().getUniqueId());
        Map map = maps.get(map_name);
        map.onBlockPlace(event);
        maps.put(map_name, map);
    }

    public void blockBreak(BlockBreakEvent event){
        String map_name = playerPlace.get(event.getPlayer().getUniqueId());
        Map map = maps.get(map_name);
        map.onBlockBreak(event);
        maps.put(map_name, map);
    }
    public void saveMap(Player player, String map_name){
        Map map = maps.get(map_name);
        map.save(player);
        maps.put(map_name, map);
    }
    public void createMap(String map_name, String map_type){
        if(map_type.equals("CTF")){
            maps.put(map_name, new CTFMap(map_name, false));
        }
        if(map_type.equals("RCTF")){
            maps.put(map_name, new RankedCTFMap(map_name, false));
        }
        if(map_type.equals("UCTF")){
            maps.put(map_name, new UltimateCTFMap(map_name, false));
        }
    }
    public void removeMap(String map_name){
        maps.remove(map_name);
    }
    public void updateBoard(Player player, EntryBuilder builder){
        maps.get(getMapName(player)).updateBoard(builder, player);
    }
}

