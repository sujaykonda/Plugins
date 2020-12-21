package me._xGQD.turtlegm.Maps;

import me._xGQD.turtlegm.Main;
import me._xGQD.turtlegm.Schematic;
import me._xGQD.turtlegm.scoreboard.common.EntryBuilder;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.List;

@SuppressWarnings("deprecation")
public class Map {
    protected String map_type;
    protected List<NPC> npcs;
    protected HashMap<UUID, PlayerData> playerData;
    protected Main plugin = JavaPlugin.getPlugin(Main.class);
    protected String name;
    public Map(String name, boolean load){
        this.name = name;
        if(load){
            load();
        }
        map_type = "MAP";
        npcs = new ArrayList<>();
        playerData = new HashMap<>();
    }

    public void onJoin(Player player){ }

    public void changeSettings(String[] args, CommandSender commandSender){ }
    public void onOpen(){ }
    public void onClose(){ }
    public void onStart(){ }
    public void onEnd(){ }

    public PlayerData getPlayerData(Player player){
        return playerData.get(player.getUniqueId());
    }

    public PlayerData setPlayerData(Player player, PlayerData data){
        return playerData.put(player.getUniqueId(), data);
    }

    public void onDisable(){
        for(NPC npc: npcs){
            npc.destroy();
        }
    }

    public void onPlayerDrop(PlayerDropItemEvent event){ }
    public void onPlayerInteract(PlayerInteractEvent event){ }
    public void onEntityDamage(EntityDamageEvent event){ }
    public void onBlockPlace(BlockPlaceEvent event){ }
    public void onBlockBreak(BlockBreakEvent event){ }

    public void load(){
    }
    public EntryBuilder updateBoard(EntryBuilder builder, Player player){
        return builder;
    }
    public void save(Player player) {
    }

}