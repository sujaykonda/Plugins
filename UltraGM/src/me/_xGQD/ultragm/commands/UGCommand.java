package me._xGQD.ultragm.commands;

import me._xGQD.ultragm.Main;
import me._xGQD.ultragm.Utilities.Node;
import me._xGQD.ultragm.Utilities.Pair;
import me._xGQD.ultragm.Utilities.Utilities;
import me._xGQD.ultragm.gamemodes.CapTheFlagMap;
import me._xGQD.ultragm.gamemodes.Map;
import me._xGQD.ultragm.gamemodes.TeamDeathMatchMap;
import me._xGQD.ultragm.gamemodes.UltCapTheFlag;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UGCommand implements CommandExecutor {
    Node<String, String> commands;
    Main plugin;
    Utilities.CommandStruct struct;
    public UGCommand(Main plugin){
        this.plugin = plugin;
        commands = new Node<>("root", "C1");
        commands.addChild(new Node<>("help","C1"));
        commands.addChild(new Node<>("wand","C2"));
        commands.addChild(new Node<>("join","C3"));
        commands.addChild(new Node<>("maps","C4"));
        commands.addChild(new Node<>("types","C5"));
        Node<String, String> arena = new Node<>("map","CA");
        arena.addChild(new Node<>("save","CA1"));
        arena.addChild(new Node<>("setspawn","CA2"));
        arena.addChild(new Node<>("create","CA3"));
        arena.addChild(new Node<>("type","CA4"));
        arena.addChild(new Node<>("open","CA5"));
        arena.addChild(new Node<>("close","CA6"));
        arena.addChild(new Node<>("start","CA7"));
        arena.addChild(new Node<>("end","CA8"));
        arena.addChild(new Node<>("autostart","CA9"));
        commands.addChild(arena);
        arena.setParent(commands);
        struct = new Utilities.CommandStruct(commands);
        plugin.getCommand("ug").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Pair<String, Integer> res = struct.getResult(args);
        String cmd = res.value1;
        int argTaken = res.value2;
        int length = args.length - argTaken;
        Player player = null;
        if(commandSender instanceof Player){
            player = (Player) commandSender;
        }
        switch (cmd){
            case "C1":
                commandSender.sendMessage(ChatColor.GREEN + "List Of Commands");
                commandSender.sendMessage(ChatColor.BLUE + "/ug help - This shows the help message");
                commandSender.sendMessage(ChatColor.BLUE + "/ug join [map_name] - This teleports you into the map");
                commandSender.sendMessage(ChatColor.BLUE + "/ug maps - This shows all maps");
                commandSender.sendMessage(ChatColor.BLUE + "/ug types - This shows all the map types");
                commandSender.sendMessage(ChatColor.BLUE + "/ug map type [map_name] - This shows the type of a map");
                if(commandSender.hasPermission("ug.admin")){
                    commandSender.sendMessage(ChatColor.GREEN + "List of Admin Commands");
                    commandSender.sendMessage(ChatColor.BLUE + "/ug wand - This gives you the wand");
                    commandSender.sendMessage(ChatColor.BLUE + "/ug map open [map_name] - This opens a map for joining");
                    commandSender.sendMessage(ChatColor.BLUE + "/ug map save [map_name] - Select two points with the wand then");
                    commandSender.sendMessage(ChatColor.BLUE + "run this command to save a map");
                    commandSender.sendMessage(ChatColor.BLUE + "/ug map setspawn [map_name] [team_num] - This sets the spawns in a map");
                    commandSender.sendMessage(ChatColor.BLUE + "/ug map create [map_name] [map_type] - This creates a map");
                    commandSender.sendMessage(ChatColor.BLUE + "/ug map close [map_name] - This closes a map for joining");
                    commandSender.sendMessage(ChatColor.BLUE + "/ug map start [map_name] - This starts the game in a map");
                    commandSender.sendMessage(ChatColor.BLUE + "/ug map end [map_name] - This ends a game in a map");
                    commandSender.sendMessage(ChatColor.BLUE + "/ug map autostart [map_name] [num of players] - This starts the");
                    commandSender.sendMessage(ChatColor.BLUE + "game automatically when the correct amount of players joins");
                }
                break;
            case "C2":
                if(!(commandSender instanceof Player)){
                    commandSender.sendMessage("Only players can run this command");
                    return true;
                }
                if(player.hasPermission("ug.admin")){
                    player.getInventory().addItem(plugin.wand.getWand());
                }else{
                    player.sendMessage("You do not have permission to do that");
                }
                break;
            case "C3":
                if(!(commandSender instanceof Player)){
                    commandSender.sendMessage("Only players can run this command");
                    return true;
                }
                if(length == 1){
                    boolean success = false;
                    for(int i = 0; i < plugin.maps.size(); i++){
                        if(plugin.maps.get(i).getName().equals(args[argTaken])){
                            Map map = plugin.maps.get(i);
                            if(map.isOpen()){
                                player.sendMessage("You are joining the map, please wait.");
                                map.join(player);
                                plugin.maps.set(i, map);
                            }else{
                                player.sendMessage("That map is not open");
                            }
                            success = true;
                        }
                    }
                    if(!success){
                        player.sendMessage("Couldn't find the map you where looking for");
                    }
                }else{
                    commandSender.sendMessage("Not the correct amount of arguments");
                }
                break;
            case "C4":
                for(int i = 0; i < plugin.maps.size(); i++){
                    Map map = plugin.maps.get(i);
                    commandSender.sendMessage(map.getName());
                }
                break;
            case "C5":
                commandSender.sendMessage("CTF for now");
                break;
            case "CA1":
                if(!(commandSender instanceof Player)){
                    commandSender.sendMessage("Only players can run this command");
                    return true;
                }
                if(length == 1){
                    if(player.hasPermission("ug.admin")){
                        boolean success = false;
                        for(int i = 0; i < plugin.maps.size(); i++){
                            if(plugin.maps.get(i).getName().equals(args[argTaken])){
                                Map map = plugin.maps.get(i);
                                map.save(plugin.wand.getPos()[0], plugin.wand.getPos()[1], map.spawn);
                                plugin.maps.set(i, map);
                                player.sendMessage("Done");
                                success = true;
                            }
                        }
                        if(!success){
                            player.sendMessage("Couldn't find the map you where looking for");
                        }
                    }else{
                        player.sendMessage("You do not have permission to do that");
                    }
                }else{
                    commandSender.sendMessage("Not the correct amount of arguments");
                }
                break;
            case "CA2":
                if(!(commandSender instanceof Player)){
                    commandSender.sendMessage("Only players can run this command");
                    return true;
                }
                if(length == 2){
                    if(player.hasPermission("ug.admin")){
                        boolean success = false;
                        for(int i = 0; i < plugin.maps.size(); i++){
                            if(plugin.maps.get(i).getName().equals(args[argTaken])){
                                Map map = plugin.maps.get(i);
                                map.setspawn(player, args[argTaken + 1]);
                                plugin.maps.set(i, map);
                                success = true;
                                player.sendMessage("You have set spawn " + args[argTaken + 1]);
                            }
                        }
                        if(!success){
                            player.sendMessage("Couldn't find the map you where looking for");
                        }
                    }else{
                        player.sendMessage("You do not have permission to do that");
                    }
                }else{
                    commandSender.sendMessage("Not the correct amount of arguments");
                }
                break;
            case "CA3":
                if(!(commandSender instanceof Player)){
                    commandSender.sendMessage("Only players can run this command");
                    return true;
                }
                if(length == 2){
                    if(player.hasPermission("ug.admin")){
                        if(args[argTaken + 1].equals("CTF")){
                            CapTheFlagMap newMap = new CapTheFlagMap(plugin, args[argTaken]);
                            plugin.maps.add(newMap);
                            player.sendMessage("You have created a map called" + args[argTaken]);
                        }else if(args[argTaken + 1].equals("UCTF")) {
                            UltCapTheFlag newMap = new UltCapTheFlag(plugin, args[argTaken]);
                            plugin.maps.add(newMap);
                            player.sendMessage("You have created a map called" + args[argTaken]);
                    	}else if(args[argTaken + 1].equals("TDM")) {
                            TeamDeathMatchMap newMap = new TeamDeathMatchMap(plugin, args[argTaken]);
                            plugin.maps.add(newMap);
                            player.sendMessage("You have created a map called" + args[argTaken]);
                        }else{
                            player.sendMessage("Not a valid type name");
                        }
                    }else{
                        player.sendMessage("You do not have permission to do that");
                    }
                }else{
                    commandSender.sendMessage("Not the correct amount of arguments");
                }
                break;
            case "CA4":
                if(length == 1){
                    boolean success = false;
                    for(int i = 0; i < plugin.maps.size(); i++){
                        if(plugin.maps.get(i).getName().equals(args[argTaken])){
                            Map map = plugin.maps.get(i);
                            commandSender.sendMessage(map.getType());
                            success = true;
                        }
                    }
                    if(!success){
                        commandSender.sendMessage("Couldn't find the map you where looking for");
                    }
                }else{
                    commandSender.sendMessage("Not the correct amount of arguments");
                }
                break;
            case "CA5":
                if(length == 1){
                    if(commandSender.hasPermission("ug.admin")){
                        boolean success = false;
                        for(int i = 0; i < plugin.maps.size(); i++){
                            if(plugin.maps.get(i).getName().equals(args[argTaken])){
                                Map map = plugin.maps.get(i);
                                map.open();
                                plugin.maps.set(i, map);
                                commandSender.sendMessage("You have opened a map");
                                success = true;
                            }
                        }
                        if(!success){
                            commandSender.sendMessage("Couldn't find the map you where looking for");
                        }
                    }else{
                        commandSender.sendMessage("You do not have permission to do that");
                    }
                }else{
                    commandSender.sendMessage("Not the correct amount of arguments");
                }
                break;
            case "CA6":
                if(length == 1){
                    if(player.hasPermission("ug.admin")){
                        boolean success = false;
                        for(int i = 0; i < plugin.maps.size(); i++){
                            if(plugin.maps.get(i).getName().equals(args[argTaken])){
                                Map map = plugin.maps.get(i);
                                map.close();
                                plugin.maps.set(i, map);
                                commandSender.sendMessage("You have closed a map");
                                success = true;
                            }
                        }
                        if(!success){
                            commandSender.sendMessage("Couldn't find the map you where looking for");
                        }
                    }else{
                        player.sendMessage("You do not have permission to do that");
                    }
                }else{
                    commandSender.sendMessage("Not the correct amount of arguments");
                }
                break;
            case "CA7":
                if(length == 1){
                    if(commandSender.hasPermission("ug.admin")){
                        boolean success = false;
                        for(int i = 0; i < plugin.maps.size(); i++){
                            if(plugin.maps.get(i).getName().equals(args[argTaken])){
                                Map map = plugin.maps.get(i);
                                map.start();
                                plugin.maps.set(i, map);
                                commandSender.sendMessage("You have started a map");
                                success = true;
                            }
                        }
                        if(!success){
                            commandSender.sendMessage("Couldn't find the map you where looking for");
                        }
                    }else{
                        player.sendMessage("You do not have permission to do that");
                    }
                }else{
                    commandSender.sendMessage("Not the correct amount of arguments");
                }
                break;
            case "CA8":
                if(length == 1){
                    if(commandSender.hasPermission("ug.admin")){
                        boolean success = false;
                        for(int i = 0; i < plugin.maps.size(); i++){
                            if(plugin.maps.get(i).getName().equals(args[argTaken])){
                                Map map = plugin.maps.get(i);
                                map.end();
                                plugin.maps.set(i, map);
                                commandSender.sendMessage("You have ended a map");
                                success = true;
                            }
                        }
                        if(!success){
                            commandSender.sendMessage("Couldn't find the map you where looking for");
                        }
                    }else{
                        player.sendMessage("You do not have permission to do that");
                    }
                }else{
                    commandSender.sendMessage("Not the correct amount of arguments");
                }
                break;
            case "CA9":
                if(length == 2){
                    if(commandSender.hasPermission("ug.admin")){
                        boolean success = false;
                        for(int i = 0; i < plugin.maps.size(); i++){
                            if(plugin.maps.get(i).getName().equals(args[argTaken])){
                                Map map = plugin.maps.get(i);
                                map.autostart = Integer.valueOf(args[argTaken + 1]);
                                plugin.maps.set(i, map);
                                success = true;
                            }
                        }
                        if(!success){
                            commandSender.sendMessage("Couldn't find the map you where looking for");
                        }
                    }else{
                        player.sendMessage("You do not have permission to do that");
                    }
                }else{
                    commandSender.sendMessage("Not the correct amount of arguments");
                }
                break;
            default:
                commandSender.sendMessage("That is not a command");
                return false;
        }
        return true;
    }
}
