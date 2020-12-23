package me._xGQD.turtlegm.Commands;

import me._xGQD.turtlegm.Main;
import me._xGQD.turtlegm.Maps.CTF.CTFMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TGCommand implements CommandExecutor {
    Main plugin = JavaPlugin.getPlugin(Main.class);
    public TGCommand(){
        plugin.getCommand("tg").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
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
                commandSender.sendMessage("/tg join [map] - you join the queue for the map");
                commandSender.sendMessage("/tg open (map) - opens the map specified");
                commandSender.sendMessage("/tg close (map) - closes the map specified");
                commandSender.sendMessage("/tg start (map) - starts the map specified");
                commandSender.sendMessage("/tg end (map) - ends the map specified");
                commandSender.sendMessage("/tg create (map) - create the map specified");
                commandSender.sendMessage("/tg save (map)  - saves the map specified");
                commandSender.sendMessage("/tg changeSettings (map) [settings...] - changes the settings of the map specified");
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
            case "open":
                if(args.length == 1 && commandSender instanceof Player){
                    if(plugin.manager.playerIn(player)){
                        plugin.manager.open(player, plugin.manager.getMapName(player));
                        commandSender.sendMessage("Opened the map");
                    }
                }else if(args.length == 2){
                    if(plugin.manager.isMap(args[1])){
                        plugin.manager.open(player, args[1]);
                        commandSender.sendMessage("Opened the map");
                    }else{
                        commandSender.sendMessage("Map could not be found");
                    }
                }
                break;
            case "close":
                if(args.length == 1 && commandSender instanceof Player){
                    if(plugin.manager.playerIn(player)){
                        plugin.manager.close(player, plugin.manager.getMapName(player));
                        commandSender.sendMessage("Closed The Map");
                    }
                }else if(args.length == 2){
                    if(plugin.manager.isMap(args[1])){
                        plugin.manager.close(player, args[1]);
                        commandSender.sendMessage("Closed The Map");
                    }else{
                        commandSender.sendMessage("Map could not be found");
                    }
                }
                break;
            case "start":
                if(args.length == 1 && commandSender instanceof Player){
                    if(plugin.manager.playerIn(player)){
                        plugin.manager.close(player, plugin.manager.getMapName(player));
                        plugin.manager.start(player, plugin.manager.getMapName(player));
                        commandSender.sendMessage("Started The Map");
                    }
                }else if(args.length == 2){
                    if(plugin.manager.isMap(args[1])){
                        plugin.manager.close(player, args[1]);
                        plugin.manager.start(player, args[1]);
                        commandSender.sendMessage("Started The Map");
                    }else{
                        commandSender.sendMessage("Map could not be found");
                    }
                }
                break;
            case "end":
                if(args.length == 1 && commandSender instanceof Player){
                    if(plugin.manager.playerIn(player)){
                        plugin.manager.end(player, plugin.manager.getMapName(player));
                        commandSender.sendMessage("Ended The Map");
                    }
                }else if(args.length == 2){
                    if(plugin.manager.isMap(args[1])){
                        plugin.manager.end(player, args[1]);
                        commandSender.sendMessage("Ended The Map");
                    }else{
                        commandSender.sendMessage("Map could not be found");
                    }
                }
                break;
            case "changesettings":
                if(args.length >= 2){
                    commandSender.sendMessage("Changing the settings...");
                    plugin.manager.changeSettings(args, commandSender, args[1]);
                }
                break;
            case "join":
                if(args.length == 2){
                    if(plugin.manager.isMap(args[1])){
                        plugin.manager.joinMap(player, args[1]);
                        commandSender.sendMessage("Joined the map");
                    }else{
                        commandSender.sendMessage("Map could not be found");
                    }
                }
                break;
            case "save":
                if(args.length == 2){
                    if(plugin.manager.isMap(args[1])){
                        plugin.manager.saveMap(player, args[1]);
                        commandSender.sendMessage("Saved the map");
                    }else{
                        commandSender.sendMessage("Map could not be found");
                    }
                }
                break;
            case "create":
                if(args.length == 3){
                    plugin.manager.createMap(args[1], args[2]);
                    commandSender.sendMessage("Created a map");
                }
                break;
            case "delete":
                if(args.length == 2){
                    if(plugin.manager.isMap(args[1])){
                        plugin.manager.removeMap(args[1]);
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
