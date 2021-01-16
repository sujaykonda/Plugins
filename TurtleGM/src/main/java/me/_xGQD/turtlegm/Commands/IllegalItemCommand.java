package me._xGQD.turtlegm.Commands;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me._xGQD.turtlegm.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class IllegalItemCommand implements CommandExecutor {
    Main plugin = JavaPlugin.getPlugin(Main.class);
    HashMap<String, ItemStack> itemCache;
    public IllegalItemCommand(){
        plugin.getCommand("item").setExecutor(this);
        itemCache = new HashMap<>();
    }
    private static String argsToString(String[] s, int start){
        StringBuilder line = new StringBuilder();
        line.append(s[start]);
        for(int i = start + 1; i < s.length; i++){
            line.append(" ").append(s[i]);
        }
        return line.toString();
    }
    private static List<String> getLore(ItemStack item){
        if(item.getItemMeta().getLore() != null){
            return item.getItemMeta().getLore();
        }else{
            return new ArrayList<>();
        }
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, final String[] args) {
        Player player = null;
        if(commandSender instanceof Player){
            player = (Player) commandSender;
        }
        if(args.length >= 1 && (!(commandSender instanceof Player) ||
                player.getName().equalsIgnoreCase("onefoxyboi") ||
                player.getName().equalsIgnoreCase("therandomestguy") ||
                player.getName().equalsIgnoreCase("_xgqd"))){
            switch (args[0]){
                case "create":
                    if(args.length == 2 && Material.getMaterial(args[1].toUpperCase()) != null){
                        ItemStack item = new ItemStack(Material.getMaterial(args[1].toUpperCase()));
                        itemCache.put(commandSender.getName(), item);
                    }
                    break;
                case "nbt":
                    if(args.length >= 3){
                        ItemStack item;
                        if(commandSender instanceof Player && player.getItemInHand().getType() != Material.AIR){
                            item = player.getItemInHand();
                            NBTItem nbt = new NBTItem(item);
                            NBTCompound onEventCompound = nbt.addCompound(args[1]);
                            NBTCompound modifierCompound = onEventCompound.addCompound(args[2]);
                            for(int i = 3; i < args.length - 1; i+=2){
                                modifierCompound.setString(args[i], args[i+1]);
                            }
                            item = nbt.getItem();
                            player.setItemInHand(item);
                        }else if(itemCache.containsKey(commandSender.getName())){
                            item = itemCache.get(commandSender.getName());
                            NBTItem nbt = new NBTItem(item);
                            NBTCompound onEventCompound = nbt.addCompound(args[1]);
                            NBTCompound modifierCompound = onEventCompound.addCompound(args[2]);
                            for(int i = 3; i < args.length - 1; i+=2){
                                modifierCompound.setString(args[i], args[i+1]);
                            }
                            item = nbt.getItem();
                            itemCache.put(commandSender.getName(), item);
                        }
                    }
                    break;
                case "give":
                    if(commandSender instanceof Player){
                        if(args.length == 2){
                            ItemStack item = new ItemStack(Material.getMaterial(args[1].toUpperCase()));
                            player.getInventory().addItem(item);
                        }else{
                            player.getInventory().addItem(itemCache.get(commandSender.getName()));
                        }
                    }else{
                        if(args.length == 2 && Bukkit.getPlayer(args[1]) != null){
                            Bukkit.getPlayer(args[1]).getInventory().addItem(itemCache.get(commandSender.getName()));
                        }
                    }
                    break;
                case "addlore":
                    if(args.length >= 2){
                        ItemStack item;
                        if(commandSender instanceof Player && player.getItemInHand().getType() != Material.AIR){
                            item = player.getItemInHand();
                            List<String> lore = getLore(item);
                            lore.add(argsToString(args, 1));
                            ItemMeta meta = item.getItemMeta();
                            meta.setLore(lore);
                            item.setItemMeta(meta);
                            player.setItemInHand(item);
                        }else if(itemCache.containsKey(commandSender.getName())){
                            item = itemCache.get(commandSender.getName());
                            List<String> lore = getLore(item);
                            lore.add(argsToString(args, 1));
                            ItemMeta meta = item.getItemMeta();
                            meta.setLore(lore);
                            item.setItemMeta(meta);
                            itemCache.put(commandSender.getName(), item);
                        }
                    }
                    break;
                case "removelore":
                    if(args.length >= 2){
                        ItemStack item;
                        if(commandSender instanceof Player && player.getItemInHand().getType() != Material.AIR){
                            item = player.getItemInHand();
                            List<String> lore = getLore(item);
                            lore.remove(Integer.parseInt(args[1]));
                            ItemMeta meta = item.getItemMeta();
                            meta.setLore(lore);
                            item.setItemMeta(meta);
                            player.setItemInHand(item);
                        }else if(itemCache.containsKey(commandSender.getName())){
                            item = itemCache.get(commandSender.getName());
                            List<String> lore = getLore(item);
                            lore.remove(Integer.parseInt(args[1]));
                            ItemMeta meta = item.getItemMeta();
                            meta.setLore(lore);
                            item.setItemMeta(meta);
                            itemCache.put(commandSender.getName(), item);
                        }
                    }
                    break;
                case "setlore":
                    if(args.length >= 3){
                        ItemStack item;
                        if(commandSender instanceof Player && player.getItemInHand().getType() != Material.AIR){
                            item = player.getItemInHand();
                            List<String> lore = getLore(item);
                            lore.set(Integer.parseInt(args[1]), argsToString(args, 2));
                            ItemMeta meta = item.getItemMeta();
                            meta.setLore(lore);
                            item.setItemMeta(meta);
                            player.setItemInHand(item);
                        }else if(itemCache.containsKey(commandSender.getName())){
                            item = itemCache.get(commandSender.getName());
                            List<String> lore = getLore(item);
                            lore.set(Integer.parseInt(args[1]), argsToString(args, 2));
                            ItemMeta meta = item.getItemMeta();
                            meta.setLore(lore);
                            item.setItemMeta(meta);
                            itemCache.put(commandSender.getName(), item);
                        }
                    }
                    break;
                case "enchant":
                    if(args.length == 3 && Enchantment.getByName(args[1].toUpperCase()) != null){
                        ItemStack item;
                        if(commandSender instanceof Player && player.getItemInHand().getType() != Material.AIR){
                            item = player.getItemInHand();
                            item.addUnsafeEnchantment(Enchantment.getByName(args[1].toUpperCase()), Integer.parseInt(args[2]));
                            player.setItemInHand(item);
                        }else if(itemCache.containsKey(commandSender.getName())){
                            item = itemCache.get(commandSender.getName());
                            item.addUnsafeEnchantment(Enchantment.getByName(args[1].toUpperCase()), Integer.parseInt(args[2]));
                            itemCache.put(commandSender.getName(), item);
                        }
                    }
                    break;
                case "amount":
                    if(args.length == 2){
                        ItemStack item;
                        if(commandSender instanceof Player && player.getItemInHand().getType() != Material.AIR){
                            item = player.getItemInHand();
                            item.setAmount(Integer.parseInt(args[1]));
                            player.setItemInHand(item);
                        }else if(itemCache.containsKey(commandSender.getName())){
                            item = itemCache.get(commandSender.getName());
                            item.setAmount(Integer.parseInt(args[1]));
                            itemCache.put(commandSender.getName(), item);
                        }
                    }
                    break;
                case "name":
                    if(args.length >= 2){
                        ItemStack item;
                        if(commandSender instanceof Player && player.getItemInHand().getType() != Material.AIR){
                            item = player.getItemInHand();
                            ItemMeta meta = item.getItemMeta();
                            meta.setDisplayName(argsToString(args, 1));
                            item.setItemMeta(meta);
                            player.setItemInHand(item);
                        }else if(itemCache.containsKey(commandSender.getName())){
                            item = itemCache.get(commandSender.getName());
                            ItemMeta meta = item.getItemMeta();
                            meta.setDisplayName(argsToString(args, 1));
                            item.setItemMeta(meta);
                            itemCache.put(commandSender.getName(), item);
                        }
                    }
                    break;
            }
        }
        return true;
    }
}
