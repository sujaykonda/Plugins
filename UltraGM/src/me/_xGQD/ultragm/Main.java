package me._xGQD.ultragm;

import me._xGQD.ultragm.FastBoard.FastBoard;
import me._xGQD.ultragm.Utilities.Utilities;
import me._xGQD.ultragm.commands.UGCommand;
import me._xGQD.ultragm.gamemodes.CapTheFlagMap;
import me._xGQD.ultragm.gamemodes.Map;
import me._xGQD.ultragm.gamemodes.TeamDeathMatchMap;
import me._xGQD.ultragm.gamemodes.UltCapTheFlag;
import me._xGQD.ultragm.listeners.*;
import me._xGQD.ultragm.npctraits.TDMTrait;
import me._xGQD.ultragm.shops.CTFShop;
import me._xGQD.ultragm.shops.Shop;
import me._xGQD.ultragm.shops.TDMShop;
import me._xGQD.ultragm.shops.UCTFShop;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public List<Map> maps = new ArrayList<>();
    public Utilities.Wand wand;
    public HashMap<String, Shop> shops;
    public HashMap<UUID, FastBoard> boards;
    public HashMap<UUID, Integer> players;

    @Override
    public void onEnable(){
        System.out.println(getServer().getPluginManager().getPlugin("Citizens"));
        System.out.println(getServer().getPluginManager().getPlugin("Citizens").isEnabled());
        if(getServer().getPluginManager().getPlugin("Citizens") == null) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        start();
    }
    @Override
    public void onDisable(){
        for(Map map : maps){
            if(map instanceof TeamDeathMatchMap){
                for(NPC npc : ((TeamDeathMatchMap) map).npcs){
                    npc.destroy();
                }
            }
        }
    }
    private void updateBoard(FastBoard board) {
        if(players.containsKey(board.getPlayer().getUniqueId())){
            int map_i = players.get(board.getPlayer().getUniqueId());
            maps.get(map_i).boardChange(board.getPlayer(), board);
        }
        board.updateLine(1, ChatColor.RED + "Online: " + ChatColor.GRAY + getServer().getOnlinePlayers().size());
    }
    void start(){

        //Register your trait with Citizens.
        CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(TDMTrait.class).withName("TDMTrait"));

        load();
        wand = new Utilities.Wand(this, "UG Wand", Material.DIAMOND_SPADE);
        shops = new HashMap<>();
        players = new HashMap<>();
        boards = new HashMap<>();

        Shop ctfshop = new CTFShop(this, "CTF");
        Shop uctfshop = new UCTFShop(this, "UCTF");
        Shop tdmshop = new TDMShop(this, "TDM");
        shops.put("CTF", ctfshop);
        shops.put("UCTF", uctfshop);
        shops.put("TDM", tdmshop);



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
    void load(){
        if(!getDataFolder().exists()){
            getDataFolder().mkdir();
        }
        if(getDataFolder().listFiles() != null){
            List<File> customConfigFiles = Arrays.asList(getDataFolder().listFiles());
            for(File file : customConfigFiles){
                YamlConfiguration config = new YamlConfiguration();
                try{
                    config.load(file);
                    if(config.getString("Type").equals("CTF")){
                        getServer().getConsoleSender().sendMessage("Loading Map...");
                        CapTheFlagMap ctfmap = new CapTheFlagMap(this, file.getName().replaceAll(".yml", ""));
                        ctfmap.load(config);
                        ctfmap.close();
                        maps.add(ctfmap);
                    }
                    if(config.getString("Type").equals("UCTF")){
                        getServer().getConsoleSender().sendMessage("Loading Map...");
                        UltCapTheFlag uctfmap = new UltCapTheFlag(this, file.getName().replaceAll(".yml", ""));
                        uctfmap.load(config);
                        uctfmap.close();
                        maps.add(uctfmap);
                    }
                    if(config.getString("Type").equals("TDM")){
                        getServer().getConsoleSender().sendMessage("Loading Map...");
                        TeamDeathMatchMap tdmmap = new TeamDeathMatchMap(this, file.getName().replaceAll(".yml", ""));
                        tdmmap.load(config);
                        tdmmap.close();
                        maps.add(tdmmap);
                    }
                }catch(IOException | InvalidConfigurationException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
