package me._xGQD.turtlegm.Maps.UltimateCTF;

import com.connorlinfoot.titleapi.TitleAPI;
import me._xGQD.turtlegm.Maps.CTF.CTFMap;
import me._xGQD.turtlegm.Maps.CTF.CTFPlayerData;
import me._xGQD.turtlegm.scoreboard.common.EntryBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UltimateCTFMap extends CTFMap {
    public UltimateCTFMap(String name, boolean load) {
        super(name, load);
    }
    @Override
    public void readBuffs(Player player){
        UltimateCTFPlayerData data = (UltimateCTFPlayerData) playerData.get(player.getUniqueId());
        for(String buff : data.buffs){
            switch (buff){
                case "permarmor":
                    ItemStack chest = new ItemStack(Material.GOLD_CHESTPLATE);
                    ItemStack boots = new ItemStack(Material.GOLD_BOOTS);
                    chest.addEnchantment(Enchantment.DURABILITY, 3);
                    boots.addEnchantment(Enchantment.DURABILITY, 3);
                    player.getInventory().setChestplate(chest);
                    player.getInventory().setBoots(boots);
                    break;
                case "permsword":
                    ItemStack sword = new ItemStack(Material.GOLD_SWORD);
                    sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
                    sword.addEnchantment(Enchantment.DURABILITY, 3);
                    player.getInventory().setItem(0, sword);
                    break;
                case "haste":
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 9999999, 2));
                    break;
            }
        }
        switch (data.perk){
            case 2:
                ItemStack currentSword = player.getInventory().getItem(0);
                currentSword.addEnchantment(Enchantment.KNOCKBACK, 1);
                player.getInventory().setItem(0, currentSword);
                break;
            case 4:
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                break;
        }
    }
    @Override
    public void onJoin(Player player){
        if(opened){
            if(team_count_1 <= team_count_2){
                playerData.put(player.getUniqueId(), new UltimateCTFPlayerData(0));
                spawn(player, 0);
                team_count_1 += 1;
            }else{
                playerData.put(player.getUniqueId(), new UltimateCTFPlayerData(1));
                spawn(player, 1);
                team_count_2 += 1;
            }
            plugin.shops.get("uctfPerks").open(player);
        }
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event){
        Action action = event.getAction();
        final Player player = event.getPlayer();
        UltimateCTFPlayerData data = (UltimateCTFPlayerData) playerData.get(player.getUniqueId());
        if(player.getItemInHand().getType().equals(Material.ENDER_PEARL)){
            final Location playerLoc = player.getLocation();
            data.inTimeWarp = true;
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    player.teleport(playerLoc);
                    ((UltimateCTFPlayerData) playerData.get(player.getUniqueId())).inTimeWarp = false;
                    player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                }
            }, 200);
        }
        if(player.getItemInHand().getType().equals(Material.NETHER_STAR)){
            plugin.shops.get("uctf").open(player);
        }
        if(action.equals(Action.RIGHT_CLICK_BLOCK)){
            Block block = player.getTargetBlock((Set<Material>) null, 3);
            if(block != null && block.getType() == Material.STANDING_BANNER && !data.inTimeWarp){
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
    @Override
    public EntryBuilder updateBoard(EntryBuilder builder, Player player){
        builder.blank();
        builder.next(ChatColor.DARK_GREEN + "Gold: " + ChatColor.YELLOW + ((CTFPlayerData) playerData.get(player.getUniqueId())).gold);
        builder.blank();
        return builder;
    }
    @Override
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            final Player player = (Player) event.getEntity();
            UltimateCTFPlayerData data = (UltimateCTFPlayerData) playerData.get(player.getUniqueId());
            if(event instanceof EntityDamageByEntityEvent){
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                if(e.getDamager() instanceof Player){
                    Player damager = (Player) e.getDamager();
                    UltimateCTFPlayerData damagerData = (UltimateCTFPlayerData) playerData.get(damager.getUniqueId());
                    if (data.team == damagerData.team){
                        event.setCancelled(true);
                    } else if (data.spawnProt){
                        event.setCancelled(true);
                    } else {
                        data.lastHit = damager.getUniqueId();
                        playerData.put(player.getUniqueId(), data);
                    }
                }
            }
            if(event.getDamage() >= player.getHealth() || event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)){
                event.setCancelled(true);
                if(data.lastHit != null){
                    Player lastHitPlayer = Bukkit.getPlayer(data.lastHit);
                    lastHitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 3));
                    lastHitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0));
                    if(((UltimateCTFPlayerData) playerData.get(data.lastHit)).perk == 3){
                        lastHitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
                    }
                    if(((UltimateCTFPlayerData) playerData.get(data.lastHit)).perk == 1){
                        ((UltimateCTFPlayerData) playerData.get(data.lastHit)).gold += 15;
                    }else {
                        ((UltimateCTFPlayerData) playerData.get(data.lastHit)).gold += 10;
                    }
                }
                for(PotionEffect effect : player.getActivePotionEffects()){
                    player.removePotionEffect(effect.getType());
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 255));
                ((UltimateCTFPlayerData) playerData.get(player.getUniqueId())).capturedFlag = false;
                ((UltimateCTFPlayerData) playerData.get(player.getUniqueId())).spawnProt = true;
                ((UltimateCTFPlayerData) playerData.get(player.getUniqueId())).lastHit = null;
                player.setGameMode(GameMode.SPECTATOR);
                TitleAPI.sendTitle(player, 0, 20, 0, "", "You will be respawned in 2 second");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        TitleAPI.sendTitle(player, 0, 20, 0, "", "You will be respawned in 1 second");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                UltimateCTFPlayerData data = (UltimateCTFPlayerData) playerData.get(player.getUniqueId());
                                spawn(player, data.team);
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        ((UltimateCTFPlayerData) playerData.get(player.getUniqueId())).spawnProt = false;
                                    }
                                }, 40);
                            }
                        }, 20);
                    }
                }, 20);
            }
        }
    }
}
