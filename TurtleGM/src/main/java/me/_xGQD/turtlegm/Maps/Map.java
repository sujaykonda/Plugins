package me._xGQD.turtlegm.Maps;

import me._xGQD.turtlegm.Main;
import me._xGQD.turtlegm.Schematic;
import me._xGQD.turtlegm.scoreboard.common.EntryBuilder;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.List;

public class Map {
    protected List<NPC> npcs;
    public HashMap<UUID, PlayerData> playerData;
    public boolean opened;
    public boolean started;
    protected static Main plugin = JavaPlugin.getPlugin(Main.class);
    protected String name;
    protected String type;
    public Map(String name, boolean load){
        this.name = name;
        if(load){
            load();
        }
        opened = false;
        npcs = new ArrayList<>();
        playerData = new HashMap<>();
    }

    public void onJoin(Player player) { }
    public static String getType(){
        return "map";
    }
    public String getMapType(){
        return "map";
    }

    public YamlConfiguration getConfig() throws IOException, InvalidConfigurationException {
        File maps_dir = new File(plugin.getDataFolder(), "/maps");
        if (!maps_dir.exists())
            maps_dir.mkdirs();

        File map_file = new File(plugin.getDataFolder(), "/maps/" + getMapType() + name + ".yml");

        YamlConfiguration config = new YamlConfiguration();

        config.load(map_file);

        return config;
    }

    public void saveConfig(YamlConfiguration config) throws IOException {
        File maps_dir = new File(plugin.getDataFolder(), "/maps");
        if (!maps_dir.exists())
            maps_dir.mkdirs();

        File map_file = new File(plugin.getDataFolder(), "/maps/" + getMapType() + name + ".yml");

        config.save(map_file);
    }

    public File getSchematicFile(){
        File schematics_dir = new File(plugin.getDataFolder(), "/schematics");
        if (!schematics_dir.exists())
            schematics_dir.mkdirs();

        return new File(plugin.getDataFolder(), "/schematics/" + getMapType() + name + ".schematic");
    }

    public void changeSettings(String[] args, CommandSender commandSender){ }
    public void setupGame(){ }
    public void startGame(){ }
    public void endGame(){ }
    public static void openKitEdit(Player player){ }
    public static void saveKitEdit(Player player) { }
    public static Object loadKit(UUID uuid, ConfigurationSection config){ return null; }

    public void onDisable(){
        for(NPC npc: npcs){
            npc.destroy();
        }
    }
    public PlayerData getPlayerData(UUID playerUUID){
        return playerData.get(playerUUID);
    }
    public void onPlayerDrop(PlayerDropItemEvent event){ }
    public void onPlayerInteract(PlayerInteractEvent event){ }
    public void onPlayerDamage(EntityDamageEvent event){ }
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event, Player player, Player cause){ }
    public void onBlockPlace(BlockPlaceEvent event){ }
    public void onBlockBreak(BlockBreakEvent event){ }
    public void onPlayerMove(PlayerMoveEvent event){ }

    public void load(){ }
    public EntryBuilder updateBoard(EntryBuilder builder, Player player){
        return builder;
    }
    public void saveMap(Player player) { }

}