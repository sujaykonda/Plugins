package me._xGQD.turtlegm;

import me._xGQD.turtlegm.Maps.CTF.CTFMap;
import me._xGQD.turtlegm.Maps.Map;
import me._xGQD.turtlegm.Maps.RankedCTF.RankedCTFMap;
import me._xGQD.turtlegm.Maps.SkyUHC.SkyUHCMap;
import me._xGQD.turtlegm.Maps.StickFight.StickFightMap;
import me._xGQD.turtlegm.Maps.UltimateCTF.UltimateCTFMap;
import me._xGQD.turtlegm.scoreboard.common.EntryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {

    public HashMap<UUID, PlayerStats> playerElo;
    public HashMap<String, Class> map_types;

    public HashMap<String, Material> map_icons;

    public Inventory typesInventory;
    public HashMap<String, Inventory> mapInventories;

    HashMap<String, HashMap<String, Map>> maps;
    HashMap<UUID, String> playerPlace;

    Main plugin = JavaPlugin.getPlugin(Main.class);
    public PlayerManager() throws IOException, InvalidConfigurationException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        map_types = new HashMap<>();
        map_icons = new HashMap<>();

        map_icons.put("ctf", Material.BANNER);
        map_icons.put("uctf", Material.ENDER_PEARL);
        map_icons.put("rctf", Material.WOOL);
        map_icons.put("skyuhc", Material.DIAMOND_ORE);;
        map_icons.put("stickfight", Material.STICK);

        typesInventory = Bukkit.createInventory(null, 9);
        mapInventories = new HashMap<>();

        map_types.put("ctf", CTFMap.class);
        map_types.put("uctf", UltimateCTFMap.class);
        map_types.put("rctf", RankedCTFMap.class);
        map_types.put("skyuhc", SkyUHCMap.class);
        map_types.put("stickfight", StickFightMap.class);

        typesInventory.setItem(0, ItemUtilities.createItem(map_icons.get("ctf"), "Capture The Flag", new String[]{}));
        typesInventory.setItem(1, ItemUtilities.createItem(map_icons.get("uctf"), "Ultimate Capture The Flag", new String[]{}));
        typesInventory.setItem(2, ItemUtilities.createItem(map_icons.get("rctf"), "Ranked Capture The Flag", new String[]{}));
        typesInventory.setItem(3, ItemUtilities.createItem(map_icons.get("skyuhc"), "SkyUHC", new String[]{}));
        typesInventory.setItem(4, ItemUtilities.createItem(map_icons.get("stickfight"), "Stick Fight", new String[]{}));

        maps = new HashMap<>();
        playerPlace = new HashMap<>();
        playerElo = new HashMap<>();

        for(String type : map_types.keySet()){
            mapInventories.put(type, Bukkit.createInventory(null, 27));
            maps.put(type, new HashMap<String, Map>());
        }

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
            String map_name = file.getName().substring(config.getString("type").length(), file.getName().length()-4);
            Class map_class = map_types.get(config.getString("type"));
            HashMap<String, Map> maps_with_types = maps.get(config.getString("type"));
            Map map = (Map) map_class.getConstructor(String.class, boolean.class).newInstance(map_name, true);
            ItemStack icon = ItemUtilities.createItem(map_icons.get(config.getString("type")), map_name, new String[]{});
            for(int i = 0; i < mapInventories.get(config.getString("type")).getSize(); i++){
                if(mapInventories.get(config.getString("type")).getItem(i) == null){
                    mapInventories.get(config.getString("type")).setItem(i, icon);
                    break;
                }
            }
            maps_with_types.put(map_name, map);
        }
    }
    public PlayerStats getPlayerStats(UUID playerUUID){
        return playerElo.get(playerUUID);
    }
    public void join(Player player){
        if(!playerPlace.containsKey(player.getUniqueId())){
            playerPlace.put(player.getUniqueId(), "lobby");
        }
    }
    public void disable(){
        for(HashMap<String, Map> maps_with_types : maps.values()){
            for(Map map : maps_with_types.values()){
                map.onDisable();
            }
        }
    }
    public boolean isMap(String type, String name){
        return maps.containsKey(type) && maps.get(type).containsKey(name);
    }
    public boolean playerIn(Player player){
        if(!playerPlace.containsKey(player.getUniqueId())){
            playerPlace.put(player.getUniqueId(), "lobby");
        }
        return !playerPlace.get(player.getUniqueId()).equals("lobby");
    }
    public void gotoLobby(final UUID uuid){
        playerPlace.put(uuid, "lobby");
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(plugin.getDataFolder(), "/lobby.yml");
                    if(Bukkit.getPlayer(uuid) != null){
                        if(file.exists()) {
                            YamlConfiguration config = new YamlConfiguration();
                            config.load(file);

                            Bukkit.getPlayer(uuid).teleport(new Location(Bukkit.getWorld(config.getString("world")), config.getDouble("x"), config.getDouble("y"), config.getDouble("z")));
                        }else{
                            Bukkit.getPlayer(uuid).setHealth(0);
                        }
                    }
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }, 40);
    }
    public void setLobby(Location location){
        try {
            File file = new File(plugin.getDataFolder(), "/lobby.yml");
            YamlConfiguration config = new YamlConfiguration();
            config.set("world", location.getWorld().getName());
            config.set("x", location.getX());
            config.set("y", location.getY());
            config.set("z", location.getZ());
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Map getMap(String type, String name){
        return maps.get(type).get(name);
    }
    public void setMap(String type, String name, Map map){
        maps.get(type).put(name, map);
    }
    public String[] getMapIds(Player player){
        return playerPlace.get(player.getUniqueId()).split(" ");
    }
    public void joinMap(Player player, String type, String name){
        if(name.equals("lobby")){
            return;
        }

        Map map = getMap(type, name);
        map.onJoin(player);
        setMap(type, name, map);
        playerPlace.put(player.getUniqueId(), type + " " + name);
    }

    public void changeSettings(String[] args, CommandSender commandSender, String type, String name){
        Map map = getMap(type, name);
        map.changeSettings(args, commandSender);
        setMap(type, name, map);
    }
    public void end(String type, String name){
        Map map = getMap(type, name);
        map.endGame();
        setMap(type, name, map);
    }

    public void start(String type, String name){
        Map map = getMap(type, name);
        map.startGame();
        setMap(type, name, map);
    }

    public void open(String type, String name){
        Map map = getMap(type, name);
        map.setupGame();
        setMap(type, name, map);
    }

    public void playerDrop(PlayerDropItemEvent event){
        String[] map_ids = playerPlace.get(event.getPlayer().getUniqueId()).split(" ");
        Map map = getMap(map_ids[0], map_ids[1]);
        map.onPlayerDrop(event);
        setMap(map_ids[0], map_ids[1], map);
    }

    public void playerInteract(PlayerInteractEvent event){
        String[] map_ids = playerPlace.get(event.getPlayer().getUniqueId()).split(" ");
        Map map = getMap(map_ids[0], map_ids[1]);
        map.onPlayerInteract(event);
        setMap(map_ids[0], map_ids[1], map);
    }

    public void playerMove(PlayerMoveEvent event){
        String[] map_ids = playerPlace.get(event.getPlayer().getUniqueId()).split(" ");
        Map map = getMap(map_ids[0], map_ids[1]);
        map.onPlayerMove(event);
        setMap(map_ids[0], map_ids[1], map);
    }

    public void entityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            String[] map_ids = playerPlace.get(player.getUniqueId()).split(" ");
            Map map = getMap(map_ids[0], map_ids[1]);
            if(event instanceof EntityDamageByEntityEvent){
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                Player cause = null;
                if(e.getDamager() instanceof Player){
                    if(playerIn((Player) e.getDamager())){
                        cause = (Player) e.getDamager();
                    }
                }
                if(e.getDamager() instanceof Projectile){
                    Projectile proj = (Projectile) e.getDamager();
                    if(proj.getShooter() instanceof Player){
                        if(playerIn((Player) proj.getShooter())){
                            cause = (Player) proj.getShooter();
                        }
                    }
                }
                if(cause != null){
                    map.onPlayerDamageByPlayer(e, player, cause);
                }
            }
            map.onPlayerDamage(event, player);
            setMap(map_ids[0], map_ids[1], map);
        }
    }

    public void blockPlace(BlockPlaceEvent event){
        String[] map_ids = playerPlace.get(event.getPlayer().getUniqueId()).split(" ");
        Map map = getMap(map_ids[0], map_ids[1]);
        map.onBlockPlace(event);
        setMap(map_ids[0], map_ids[1], map);
    }

    public void blockBreak(BlockBreakEvent event){
        String[] map_ids = playerPlace.get(event.getPlayer().getUniqueId()).split(" ");
        Map map = getMap(map_ids[0], map_ids[1]);
        map.onBlockBreak(event);
        setMap(map_ids[0], map_ids[1], map);
    }
    public void saveMap(Player player, String type, String name){
        Map map = getMap(type, name);
        map.saveMap(player);
        setMap(type, name, map);
    }
    public void createMap(String type, String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class map_class = map_types.get(type);
        System.out.println(map_class);
        maps.get(type).put(name, (Map) map_class.getConstructor(String.class, boolean.class).newInstance(name, false));

    }
    public void removeMap(String type, String name){
        maps.get(type).remove(name);
    }
    public void updateBoard(Player player, EntryBuilder builder){
        String[] ids = getMapIds(player);
        getMap(ids[0], ids[1]).updateBoard(builder, player);
    }
}

