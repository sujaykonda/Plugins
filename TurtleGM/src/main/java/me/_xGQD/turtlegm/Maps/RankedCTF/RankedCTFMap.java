package me._xGQD.turtlegm.Maps.RankedCTF;

import com.connorlinfoot.titleapi.TitleAPI;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import me._xGQD.turtlegm.Maps.CTF.CTFMap;
import me._xGQD.turtlegm.Maps.CTF.CTFPlayerData;
import me._xGQD.turtlegm.Maps.Map;
import me._xGQD.turtlegm.PlayerStats;
import me._xGQD.turtlegm.scoreboard.common.EntryBuilder;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class RankedCTFMap extends CTFMap {

    public RankedCTFMap(String map_name, boolean load) {
        super(map_name, load);
        map_type = "RCTF";
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event){
        Action action = event.getAction();
        Player player = event.getPlayer();
        CTFPlayerData data = (CTFPlayerData) playerData.get(player.getUniqueId());
        if(action.equals(Action.RIGHT_CLICK_BLOCK)){
            Block block = player.getTargetBlock((Set<Material>) null, 3);
            if(block != null && block.getType() == Material.STANDING_BANNER){
                if(block.getData() == (byte) 8){
                    if(data.capturedFlag){
                        if(data.team == 0){
                            data.gold += 10;
                            ItemStack helm = new ItemStack(Material.LEATHER_HELMET);

                            LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
                            helmLeatherMeta.setColor(Color.RED);
                            helm.setItemMeta(helmLeatherMeta);

                            player.getInventory().setHelmet(helm);

                            points[0] += 1;
                            data.capturedFlag = false;

                            if(points[0] == 2) {
                                for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                                    if(playerData.containsKey(onlinePlayer.getUniqueId())){
                                        TitleAPI.sendTitle(onlinePlayer, 10, 40, 10, "Team 1 has won the game", null);
                                    }
                                }
                                for(UUID playerUUID : playerData.keySet()){
                                    if(!plugin.manager.playerElo.containsKey(playerUUID)){
                                        plugin.manager.playerElo.put(playerUUID, new PlayerStats());
                                    }
                                    if(playerData.get(playerUUID).team == 0){
                                        plugin.manager.playerElo.get(playerUUID).addElo("RankedCTF", 10);
                                    }else{
                                        plugin.manager.playerElo.get(playerUUID).addElo("RankedCTF", -10);
                                    }
                                }
                                onEnd();
                            }else{
                                for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                                    if(playerData.containsKey(onlinePlayer.getUniqueId())){
                                        onlinePlayer.sendMessage("Team 1 has captured a flag");
                                    }
                                }
                            }
                        }
                    }else{
                        if(data.team == 1) {

                            if(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            }

                            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 0));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0));

                            data.gold += 10;
                            data.capturedFlag = true;
                            for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                                if(playerData.containsKey(onlinePlayer.getUniqueId())){
                                    onlinePlayer.sendMessage("Team 2 has picked up the flag");
                                }
                            }

                            ItemStack banner = new ItemStack(Material.BANNER, 1, (byte) 1);

                            player.getInventory().setHelmet(banner);
                        }
                    }
                }else {

                    if(data.capturedFlag) {
                        if(data.team == 1) {
                            data.gold += 10;

                            ItemStack helm = new ItemStack(Material.LEATHER_HELMET);

                            LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
                            helmLeatherMeta.setColor(Color.BLUE);
                            helm.setItemMeta(helmLeatherMeta);

                            player.getInventory().setHelmet(helm);

                            points[1] += 1;
                            data.capturedFlag = false;

                            if(points[1] == 2) {
                                for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                                    if(playerData.containsKey(onlinePlayer.getUniqueId())){
                                        TitleAPI.sendTitle(onlinePlayer, 10, 40, 10, "Team 2 has won the game", null);
                                    }
                                    for(UUID playerUUID : playerData.keySet()){
                                        if(!plugin.manager.playerElo.containsKey(playerUUID)){
                                            plugin.manager.playerElo.put(playerUUID, new PlayerStats());
                                        }
                                        if(playerData.get(playerUUID).team == 1){
                                            plugin.manager.playerElo.get(playerUUID).addElo("RankedCTF", 10);
                                        }else{
                                            plugin.manager.playerElo.get(playerUUID).addElo("RankedCTF", -10);
                                        }
                                    }
                                }
                                onEnd();
                            }else{
                                for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                                    if(playerData.containsKey(onlinePlayer.getUniqueId())){
                                        onlinePlayer.sendMessage("Team 2 has captured a flag");
                                    }
                                }
                            }
                        }

                    }else {
                        if(data.team == 0) {
                            if(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            }

                            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 0));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0));

                            data.gold += 10;
                            data.capturedFlag = true;
                            for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                                if(playerData.containsKey(onlinePlayer.getUniqueId())){
                                    onlinePlayer.sendMessage("Team 1 has picked up a flag");
                                }
                            }

                            ItemStack banner = new ItemStack(Material.BANNER, 1, (byte) 4);

                            player.getInventory().setHelmet(banner);
                        }
                    }
                }
            }
        }
    }
}

