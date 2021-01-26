package me._xGQD.turtlegm.Commands;

import com.connorlinfoot.titleapi.TitleAPI;
import me._xGQD.turtlegm.Main;
import me._xGQD.turtlegm.Maps.CTF.CTFMap;
import me._xGQD.turtlegm.Maps.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class TGCommand implements CommandExecutor {
    Main plugin = JavaPlugin.getPlugin(Main.class);
    public TGCommand(){
        plugin.getCommand("tg").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, final String[] args) {
        String subcommand;
        if(args.length > 0){
            subcommand = args[0];
        }else{
            subcommand = "help";
        }
        Player player = null;
        if(commandSender instanceof Player){
            player = (Player) commandSender;
        }
        switch (subcommand){
            case "help":
                commandSender.sendMessage(ChatColor.GREEN + "HELP");
                commandSender.sendMessage("() is optional and [] is not optional");
                commandSender.sendMessage("/tg help - displays this help message");
                commandSender.sendMessage("/tg stats - displays your stats");
                commandSender.sendMessage("/tg join [map type] [map name] - you join the queue for the map");
                commandSender.sendMessage("/tg open (map type) (map name) - opens the map specified");
                commandSender.sendMessage("/tg start (map type) (map name) - starts the map specified");
                commandSender.sendMessage("/tg end (map type) (map name) - ends the map specified");
                commandSender.sendMessage("/tg create (map type) (map name) - create the map specified");
                commandSender.sendMessage("/tg save (map type) (map name)  - saves the map specified");
                commandSender.sendMessage("/tg changeSettings (map type) (map name) [settings...] - changes the settings of the map specified");
                break;
            case "stats":
                if(args.length == 1 && commandSender instanceof Player){
                    commandSender.sendMessage(plugin.manager.getPlayerStats(player.getUniqueId()).toString());
                }else if(args.length == 2){
                    Player statsPlayer = Bukkit.getPlayer(args[1]);
                    if(!plugin.manager.playerElo.containsKey(statsPlayer.getUniqueId())){
                        commandSender.sendMessage("No player found.");
                    }else{
                        commandSender.sendMessage(plugin.manager.getPlayerStats(statsPlayer.getUniqueId()).toString());
                    }
                }
                break;
            case "types":
                for(String type : plugin.manager.map_types.keySet()){
                    commandSender.sendMessage(type);
                }
                break;
            case "kit-open":
                if(args.length == 2 && commandSender instanceof Player){
                    if(args[1].equals("CTF")){
                        CTFMap.openKitEdit(player);
                    }
                }
                break;
            case "kit-save":
                if(args.length == 2 && commandSender instanceof Player){
                    if(args[1].equals("CTF")){
                        CTFMap.saveKitEdit(player);
                    }
                }
                break;
            case "close":
                if(args.length == 1 && commandSender instanceof Player){
                    if(plugin.manager.playerIn(player)){
                        String[] ids = plugin.manager.getMapIds(player);
                        plugin.manager.getMap(ids[0], ids[1]).opened = false;
                        commandSender.sendMessage("Closed the map");
                    }
                }else if(args.length == 3){
                    if(plugin.manager.isMap(args[1], args[2])){
                        String[] ids = plugin.manager.getMapIds(player);
                        plugin.manager.getMap(args[1], args[2]).opened = false;
                        commandSender.sendMessage("Closed the map");
                    }else{
                        commandSender.sendMessage("Map could not be found");
                    }
                }
                break;

            case "open":
                if(args.length == 1 && commandSender instanceof Player){
                    if(plugin.manager.playerIn(player)){
                        String[] ids = plugin.manager.getMapIds(player);
                        plugin.manager.getMap(ids[0], ids[1]).opened = true;
                        commandSender.sendMessage("Opened the map");
                    }
                }else if(args.length == 3){
                    if(plugin.manager.isMap(args[1], args[2])){
                        plugin.manager.getMap(args[1], args[2]).opened = true;
                        commandSender.sendMessage("Opened the map");
                    }else{
                        commandSender.sendMessage("Map could not be found");
                    }
                }
                break;
            case "start":
                if(args.length == 1 && commandSender instanceof Player){
                    if(plugin.manager.playerIn(player)){
                        String[] ids = plugin.manager.getMapIds(player);
                        plugin.manager.start(ids[0], ids[1]);
                        commandSender.sendMessage("Started The Map");
                    }
                }else if(args.length == 3){
                    if(plugin.manager.isMap(args[1], args[2])){
                        plugin.manager.start(args[1], args[2]);
                        commandSender.sendMessage("Started The Map");
                    }else{
                        commandSender.sendMessage("Map could not be found");
                    }
                }
                break;
            case "end":
                if(args.length == 1 && commandSender instanceof Player){
                    if(plugin.manager.playerIn(player)){
                        String[] ids = plugin.manager.getMapIds(player);
                        plugin.manager.end(ids[0], ids[1]);
                        commandSender.sendMessage("Ended The Map");
                    }
                }else if(args.length == 3){
                    if(plugin.manager.isMap(args[1], args[2])){
                        plugin.manager.end(args[1], args[2]);
                        commandSender.sendMessage("Ended The Map");
                    }else{
                        commandSender.sendMessage("Map could not be found");
                    }
                }
                break;
            case "changesettings":
                if(args.length >= 3){
                    commandSender.sendMessage("Changing the settings...");
                    plugin.manager.changeSettings(args, commandSender, args[1], args[2]);
                }
                break;
            case "join":
                if(args.length == 1 && commandSender instanceof Player){
                    player.openInventory(plugin.manager.typesInventory);
                }
                if(args.length == 2 && commandSender instanceof Player){
                    if(plugin.manager.map_types.containsKey(args[1])){
                        player.openInventory(plugin.manager.mapInventories.get(args[1]));
                    }
                }
                if(args.length == 3){
                    if(plugin.manager.isMap(args[1], args[2])){
                        if(!plugin.manager.getMap(args[1], args[2]).started){
                            if(!plugin.manager.getMap(args[1], args[2]).opened && !plugin.manager.getMap(args[1], args[2]).started){
                                plugin.manager.open(args[1], args[2]);
                            }
                            if(plugin.manager.getMap(args[1], args[2]).playerData.size() == 1){
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!plugin.manager.getMap(args[1], args[2]).started){
                                            for(UUID playerUUID : plugin.manager.getMap(args[1], args[2]).playerData.keySet()) {
                                                TitleAPI.sendTitle(Bukkit.getPlayer(playerUUID), 10, 20, 10, "15", null);
                                            }
                                        }
                                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                            @Override
                                            public void run() {
                                                if(!plugin.manager.getMap(args[1], args[2]).started){
                                                    for(UUID playerUUID : plugin.manager.getMap(args[1], args[2]).playerData.keySet()) {
                                                        TitleAPI.sendTitle(Bukkit.getPlayer(playerUUID), 10, 20, 10, "10", null);
                                                    }
                                                }
                                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if(!plugin.manager.getMap(args[1], args[2]).started){
                                                            for(UUID playerUUID : plugin.manager.getMap(args[1], args[2]).playerData.keySet()) {
                                                                TitleAPI.sendTitle(Bukkit.getPlayer(playerUUID), 10, 20, 10, "5", null);
                                                            }

                                                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    if(!plugin.manager.getMap(args[1], args[2]).started){
                                                                        for(UUID playerUUID : plugin.manager.getMap(args[1], args[2]).playerData.keySet()) {
                                                                            TitleAPI.sendTitle(Bukkit.getPlayer(playerUUID), 10, 20, 10, "4", null);
                                                                        }

                                                                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                if(!plugin.manager.getMap(args[1], args[2]).started){
                                                                                    for(UUID playerUUID : plugin.manager.getMap(args[1], args[2]).playerData.keySet()) {
                                                                                        TitleAPI.sendTitle(Bukkit.getPlayer(playerUUID), 10, 20, 10, "3", null);
                                                                                    }

                                                                                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            if(!plugin.manager.getMap(args[1], args[2]).started){
                                                                                                for(UUID playerUUID : plugin.manager.getMap(args[1], args[2]).playerData.keySet()) {
                                                                                                    TitleAPI.sendTitle(Bukkit.getPlayer(playerUUID), 10, 20, 10, "2", null);
                                                                                                }

                                                                                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                                                                                    @Override
                                                                                                    public void run() {
                                                                                                        if(!plugin.manager.getMap(args[1], args[2]).started){
                                                                                                            for(UUID playerUUID : plugin.manager.getMap(args[1], args[2]).playerData.keySet()) {
                                                                                                                TitleAPI.sendTitle(Bukkit.getPlayer(playerUUID), 10, 20, 10, "1", null);
                                                                                                            }
                                                                                                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                                                                                                @Override
                                                                                                                public void run() {
                                                                                                                    plugin.manager.start(args[1], args[2]);
                                                                                                                }
                                                                                                            }, 20);
                                                                                                        }
                                                                                                    }
                                                                                            }, 20);
                                                                                            }
                                                                                        }
                                                                                    }, 20);
                                                                                }
                                                                            }
                                                                        }, 20);
                                                                    }
                                                                }
                                                            }, 20);
                                                        }
                                                    }
                                                }, 100);
                                            }
                                        }, 100);
                                    }
                                }, 300);
                            }

                            plugin.manager.joinMap(player, args[1], args[2]);
                            commandSender.sendMessage("Joined the map");
                        }
                    }else{
                        commandSender.sendMessage("Map could not be found");
                    }
                }
                break;
            case "setlobby":
                if(commandSender instanceof Player){
                    plugin.manager.setLobby(player.getLocation());
                }
                break;
            case "lobby":
                if(commandSender instanceof Player){
                    player.sendMessage("Sending you to the lobby...");
                    plugin.manager.gotoLobby(player.getUniqueId());
                    player.sendMessage("Done");
                }
                break;
            case "spectate":
                if(args.length == 2){
                    if(commandSender instanceof Player){
                        if(Bukkit.getPlayer(args[1]) != null && plugin.manager.playerIn(Bukkit.getPlayer(args[1]))){
                            player.sendMessage("Teleporting you to that player...");
                            player.setGameMode(GameMode.SPECTATOR);
                            player.teleport(Bukkit.getPlayer(args[1]));
                            player.sendMessage("Done");
                        }else{
                            player.sendMessage("That player is not in a game or is not online");
                        }
                    }
                }
                break;
            case "save":
                if(args.length == 3){
                    if(plugin.manager.isMap(args[1], args[2])){
                        plugin.manager.saveMap(player, args[1], args[2]);
                        commandSender.sendMessage("Saved the map");
                    }else{
                        commandSender.sendMessage("Map could not be found");
                    }
                }
                break;
            case "create":
                if(args.length == 3){
                    try {
                        plugin.manager.createMap(args[1], args[2]);
                        commandSender.sendMessage("Created a map");
                    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "delete":
                if(args.length == 3){
                    if(plugin.manager.isMap(args[1], args[2])){
                        plugin.manager.removeMap(args[1], args[2]);
                        commandSender.sendMessage("Deleted the map");
                    }else{
                        commandSender.sendMessage("Map could not be found");
                    }
                }
                break;
        }
        return true;
    }
}
