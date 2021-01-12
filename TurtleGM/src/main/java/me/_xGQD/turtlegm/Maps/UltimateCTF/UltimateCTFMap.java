package me._xGQD.turtlegm.Maps.UltimateCTF;

import com.connorlinfoot.titleapi.TitleAPI;
import me._xGQD.turtlegm.Maps.CTF.CTFMap;
import me._xGQD.turtlegm.Shop.UltimateCTFPerks;
import me._xGQD.turtlegm.scoreboard.common.EntryBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import me._xGQD.turtlegm.Maps.UltimateCTF.UltimateCTFPlayerData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.Set;
import java.util.UUID;

public class UltimateCTFMap extends CTFMap {
    public UltimateCTFMap(String name, boolean load) {
        super(name, load);
    }
    public static String getType(){
        return "uctf";
    }
    @Override
    public void readBuffs(Player player){
        UltimateCTFPlayerData data = getPlayerData(player.getUniqueId());
        super.readBuffs(player);
        if (data.perk == 4) {
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        }
    }
    @Override
    public void onJoin(Player player){
        if(opened){
            File file = new File(plugin.getDataFolder(), "/" + player.getUniqueId().toString() + ".yml");
            if(file.exists() && YamlConfiguration.loadConfiguration(file).getConfigurationSection("ctf") != null){
                kits.put(player.getUniqueId(), loadKit(player.getUniqueId(), YamlConfiguration.loadConfiguration(file).getConfigurationSection("ctf")));
            }else{
                kits.put(player.getUniqueId(), loadKit(player.getUniqueId(), null));
            }
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
    public void openShop(Player player){
        plugin.shops.get("uctf").open(player);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event){
        Action action = event.getAction();
        final Player player = event.getPlayer();
        UltimateCTFPlayerData data = getPlayerData(player.getUniqueId());
        if(player.getItemInHand().getType().equals(Material.ENDER_PEARL) && (action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) ){
            if(data.ePearlCD){
                final Location playerLoc = player.getLocation();
                data.inTimeWarp = true;
                getPlayerData(player.getUniqueId()).ePearlCD = false;
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.teleport(playerLoc);
                        getPlayerData(player.getUniqueId()).inTimeWarp = false;
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                getPlayerData(player.getUniqueId()).ePearlCD = true;
                            }
                        }, 100);
                    }
                }, 100);
            }else{
                event.setCancelled(true);
            }
        }
        super.onPlayerInteract(event);
    }
    @Override
    public UltimateCTFPlayerData getPlayerData(UUID playerUUID){
        return (UltimateCTFPlayerData) playerData.get(playerUUID);
    }
    @Override
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            final Player player = (Player) event.getEntity();
            UltimateCTFPlayerData data = getPlayerData(player.getUniqueId());
            if(event instanceof EntityDamageByEntityEvent){
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

                if(e.getDamager() instanceof FishHook && ((FishHook) e.getDamager()).getShooter() instanceof Player){
                    Player damager = (Player) ((FishHook) e.getDamager()).getShooter();
                    if(playerData.containsKey(damager.getUniqueId())){
                        UltimateCTFPlayerData damagerData = getPlayerData(damager.getUniqueId());
                        if (data.team == damagerData.team){
                            event.setCancelled(true);
                        } else if (data.spawnProt){
                            event.setCancelled(true);
                        } else {
                            data.lastHit = damager.getUniqueId();
                            if(damagerData.spawnProt){
                                damagerData.spawnProt = false;
                                playerData.put(damager.getUniqueId(), damagerData);
                            }
                            playerData.put(player.getUniqueId(), data);
                        }
                    }
                }

                if(e.getDamager() instanceof Player){
                    Player damager = (Player) e.getDamager();

                    if(playerData.containsKey(damager.getUniqueId())){
                        UltimateCTFPlayerData damagerData = getPlayerData(damager.getUniqueId());
                        if (data.team == damagerData.team){
                            event.setCancelled(true);
                        } else if (data.spawnProt){
                            event.setCancelled(true);
                        } else {
                            if(damagerData.perk == 2){
                                player.setVelocity(damager.getLocation().getDirection().setY(0).normalize().multiply(0.35));
                            }
                            data.lastHit = damager.getUniqueId();
                            playerData.put(player.getUniqueId(), data);
                        }
                    }
                }
            }
            if(event.getDamage() >= player.getHealth() || event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)){
                event.setCancelled(true);
                player.playEffect(EntityEffect.DEATH);
                if(data.lastHit != null){
                    Player lastHitPlayer = Bukkit.getPlayer(data.lastHit);
                    lastHitPlayer.playEffect(EntityEffect.FIREWORK_EXPLODE);
                    lastHitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 3));
                    lastHitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0));
                    if(getPlayerData(data.lastHit).perk == 3){
                        lastHitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
                    }
                    if(getPlayerData(data.lastHit).perk == 1){
                        getPlayerData(data.lastHit).gold += 15;
                    }else {
                        getPlayerData(data.lastHit).gold += 10;
                    }
                }
                for(PotionEffect effect : player.getActivePotionEffects()){
                    player.removePotionEffect(effect.getType());
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 255));
                getPlayerData(player.getUniqueId()).capturedFlag = false;
                getPlayerData(player.getUniqueId()).spawnProt = true;
                getPlayerData(player.getUniqueId()).lastHit = null;
                player.setGameMode(GameMode.SPECTATOR);
                for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                    if(playerData.containsKey(onlinePlayer.getUniqueId())){
                        onlinePlayer.sendMessage(player.getName() + "has died");
                    }
                }
                TitleAPI.sendTitle(player, 0, 20, 0, "", "You will be respawned in 2 second");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        TitleAPI.sendTitle(player, 0, 20, 0, "", "You will be respawned in 1 second");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                UltimateCTFPlayerData data = getPlayerData(player.getUniqueId());
                                spawn(player, data.team);
                                getPlayerData(player.getUniqueId()).ePearlCD = false;
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        getPlayerData(player.getUniqueId()).spawnProt = false;
                                        getPlayerData(player.getUniqueId()).ePearlCD = true;
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
