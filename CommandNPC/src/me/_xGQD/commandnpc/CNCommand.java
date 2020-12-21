package me._xGQD.commandnpc;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CNCommand implements CommandExecutor {
    Main plugin;
    public CNCommand(Main plugin){
        this.plugin = plugin;
        plugin.getCommand("cn").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String cmd, String[] args) {
        if(args.length > 0){
            if(args[0].equals("create")){
                if(commandSender instanceof Player){
                    Player player = (Player) commandSender;
                    if(args.length == 3){
                        Location spawn_loc = player.getLocation();
                        Entity entity = spawn_loc.getWorld().spawnEntity(spawn_loc, EntityType.PLAYER);
                        entity.setCustomName(args[1]);
                        plugin.npcs.put(args[1], args[2]);
                        ConfigurationSection section = plugin.getConfig().createSection(args[1]);
                        section.set("x", player.getLocation().getX());
                        section.set("y", player.getLocation().getY());
                        section.set("z", player.getLocation().getZ());
                        section.set("world", player.getLocation().getWorld());
                        section.set("cmd", args[2].replace("_", " "));
                        plugin.saveDefaultConfig();
                    }
                }else{
                    commandSender.sendMessage("Only players can execute this command");
                    return true;
                }
            }
            if(args[0].equals("delete")){
                if(args.length == 2){
                    if(plugin.npcs.containsKey(args[1])){
                        plugin.npcs.remove(args[1]);
                        plugin.getConfig().set(args[1], null);
                    }else{
                        commandSender.sendMessage("Not a npc");
                        return true;
                    }
                }else {
                    commandSender.sendMessage("Not enough arguments");
                    return true;
                }
            }
            if(args[0].equals("edit")){
                if(args.length == 3){
                    if(plugin.npcs.containsKey(args[1])){
                        plugin.npcs.put(args[1], args[2]);
                        ConfigurationSection section = plugin.getConfig().getConfigurationSection(args[1]);
                        section.set("cmd", args[2].replace("_", " "));
                        plugin.saveDefaultConfig();
                    }else{
                        commandSender.sendMessage("Not a npc");
                        return true;
                    }
                }else {
                    commandSender.sendMessage("Not enough arguments");
                    return true;
                }
            }
        }
        return false;
    }
}
