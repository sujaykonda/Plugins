package me._xGQD.ultragm;

import me._xGQD.ultragm.FastBoard.FastBoard;
import me._xGQD.ultragm.Utilities.Utilities;
import me._xGQD.ultragm.commands.UGCommand;
import me._xGQD.ultragm.gamemodes.CapTheFlagMap;
import me._xGQD.ultragm.gamemodes.Map;
import me._xGQD.ultragm.gamemodes.UltCapTheFlag;
import me._xGQD.ultragm.listeners.*;
import me._xGQD.ultragm.shops.CTFShop;
import me._xGQD.ultragm.shops.Shop;
import me._xGQD.ultragm.shops.UCTFShop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends JavaPlugin {

    private List<File> customConfigFiles = new ArrayList<>();
    public List<Map> maps = new ArrayList<>();
    private List<FileConfiguration> customConfigs = new ArrayList<>();
    public Utilities.Wand wand;
    public List<Shop> shops;
    public HashMap<UUID, FastBoard> boards = new HashMap<>();

    @Override
    public void onEnable(){
        load();
        wand = new Utilities.Wand(this, "UG Wand", Material.DIAMOND_SPADE);
        shops = new ArrayList<>();
        Shop ctfshop = new CTFShop(this, "CTF");
        Shop uctfshop = new UCTFShop(this, "UCTF");
        shops.add(ctfshop);
        shops.add(uctfshop);
        new BlockBreakListener(this);
        new BlockPlaceListener(this);
        new HitListener(this);
        new InventoryClickListener(this);
        new PlayerInteractListener(this);
        new PlayerJoinQuitListener(this);
        new PlayerRespawnListener(this);
        new UGCommand(this);
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (FastBoard board : boards.values()) {
                updateBoard(board);
            }
        }, 0, 20);
    }
    @Override
    public void onDisable(){

    }
    private void updateBoard(FastBoard board) {
        int map_i = getMap(board.getPlayer());
        if(map_i != -1){
            maps.get(map_i).boardChange(board.getPlayer(), board);
        }
        board.updateLine(1, ChatColor.RED + "Online: " + ChatColor.GRAY + getServer().getOnlinePlayers().size());
    }
    public int getMap(Player player){
        for(int i = 0; i < maps.size(); i++){
            if(maps.get(i).in(player)){
                return i;
            }
        }
        return -1;
    }
    void load(){
        if(!getDataFolder().exists()){
            getDataFolder().mkdir();
        }
        if(getDataFolder().listFiles() != null){
            customConfigFiles = Arrays.asList(getDataFolder().listFiles());
            for(File file : customConfigFiles){
                YamlConfiguration config = new YamlConfiguration();
                try{
                    config.load(file);
                    customConfigs.add(config);
                    if(config.getString("Type").equals("CTF")){
                        getServer().getConsoleSender().sendMessage("Loading Map...");
                        CapTheFlagMap ctfmap = new CapTheFlagMap(this, file.getName().replaceAll(".yml", ""));
                        ctfmap.load(config);
                        ctfmap.close();
                        maps.add(ctfmap);
                    }
                    if(config.getString("Type").equals("UCTF")){
                        getServer().getConsoleSender().sendMessage("Loading Map...");
                        UltCapTheFlag ctfmap = new UltCapTheFlag(this, file.getName().replaceAll(".yml", ""));
                        ctfmap.load(config);
                        ctfmap.close();
                        maps.add(ctfmap);
                    }
                }catch(IOException | InvalidConfigurationException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
