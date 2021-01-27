package me._xGQD.turtlegm.Maps.StickFight;

import com.connorlinfoot.titleapi.TitleAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.function.operation.Operations;
import me._xGQD.turtlegm.ItemUtilities;
import me._xGQD.turtlegm.Maps.CTF.CTFMap;
import me._xGQD.turtlegm.Maps.Map;
import me._xGQD.turtlegm.Maps.SkyUHC.SkyUHCPlayerData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class StickFightMap extends Map {
    protected List<UUID> playerUUIDs;
    protected Location[] stick_spawns;


    public static String getType(){
        return "stickfight";
    }

    @Override
    public String getMapType(){
        return "stickfight";
    }

    public StickFightMap(String name, boolean load) {
        super(name, load);
        playerUUIDs = new ArrayList<>();
    }
    @Override
    public void onJoin(Player player){
        if(opened && playerUUIDs.size() < 2){
            for(PotionEffect effect : player.getActivePotionEffects()){
                player.removePotionEffect(effect.getType());
            }
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 255));
            player.teleport(stick_spawns[playerUUIDs.size()]);
            playerUUIDs.add(player.getUniqueId());
            playerData.put(player.getUniqueId(), new SkyUHCPlayerData(playerUUIDs.size()));
            player.setGameMode(GameMode.SURVIVAL);
            player.setLevel(0);
            player.setExp(0);
            for(PotionEffect effect : player.getActivePotionEffects()){
                player.removePotionEffect(effect.getType());
            }
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.getInventory().setBoots(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setHelmet(null);
            ItemStack stickkb = ItemUtilities.createItem(Material.STICK, "Knockback Stick", new String[]{""});
            stickkb.addUnsafeEnchantment(Enchantment.KNOCKBACK,1);
            player.getInventory().addItem(stickkb);
        }
    }

    public void loadAll(){
        try {
            stick_spawns = new Location[]{null, null};

            YamlConfiguration config = getConfig();

            World bukkitWorld = Bukkit.getWorld(config.getString("paste_location.world"));
            com.sk89q.worldedit.world.World worldEditWorld = BukkitUtil.getLocalWorld(bukkitWorld);
            stick_spawns[0] = new Location(bukkitWorld,
                    config.getDouble("loc1.x"),
                    config.getDouble("loc1.y"),
                    config.getDouble("loc1.z"));

            stick_spawns[1] = new Location(bukkitWorld,
                    config.getDouble("loc2.x"),
                    config.getDouble("loc2.y"),
                    config.getDouble("loc2.z"));

            loadMap();

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setupGame() {
        this.loadMap();

        playerUUIDs = new ArrayList<>();

        for(Location spawn : stick_spawns){
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()+2, spawn.getBlockZ()).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX()+1, spawn.getBlockY()+1, spawn.getBlockZ()).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX()-1, spawn.getBlockY()+1, spawn.getBlockZ()).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()+1, spawn.getBlockZ()+1).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()+1, spawn.getBlockZ()-1).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX()+1, spawn.getBlockY(), spawn.getBlockZ()).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX()-1, spawn.getBlockY(), spawn.getBlockZ()).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()+1).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()-1).setType(Material.BARRIER);
        }

        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitUtil.getLocalWorld(stick_spawns[0].getWorld()), -1);

        try {
            Operations.complete(editSession.commit());
        } catch (WorldEditException e) {
            e.printStackTrace();
        }

        opened = true;
    }

    @Override
    public void startGame(){
        started = true;

        for(Location spawn : stick_spawns){
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()-1, spawn.getBlockZ()).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()+2, spawn.getBlockZ()).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX()+1, spawn.getBlockY()+1, spawn.getBlockZ()).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX()-1, spawn.getBlockY()+1, spawn.getBlockZ()).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()+1, spawn.getBlockZ()+1).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()+1, spawn.getBlockZ()-1).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX()+1, spawn.getBlockY(), spawn.getBlockZ()).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX()-1, spawn.getBlockY(), spawn.getBlockZ()).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()+1).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()-1).setType(Material.AIR);
        }
    }

    @Override
    public void endGame() {
        this.loadMap();
        opened = false;
        started = false;
        for(UUID playerUUID: playerData.keySet()){
            plugin.manager.gotoLobby(playerUUID);
        }
        playerUUIDs.clear();
        playerData.clear();
    }

    @Override
    public void changeSettings(String[] args, CommandSender commandSender){
        if(!(commandSender instanceof Player)){
            return;
        }
        Player player = (Player) commandSender;
        try {
            YamlConfiguration config = getConfig();

            if(args[3].equals("setspawn")){
                if(stick_spawns == null){
                    stick_spawns = new Location[]{null, null};
                }
                if(args[4].equals("1")){
                    stick_spawns[0] = player.getLocation();

                    config.set("loc1.x", stick_spawns[0].getX());
                    config.set("loc1.y", stick_spawns[0].getY());
                    config.set("loc1.z", stick_spawns[0].getZ());
                }else if(args[4].equals("2")) {
                    stick_spawns[1] = player.getLocation();

                    config.set("loc2.x", stick_spawns[1].getX());
                    config.set("loc2.y", stick_spawns[1].getY());
                    config.set("loc2.z", stick_spawns[1].getZ());
                }
                player.sendMessage("You have set the spawn");
            }
            saveConfig(config);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event, Player player, Player cause) {
        event.setDamage(0);
    }

    @Override
    public void onPlayerDamage(EntityDamageEvent event, final Player player){
        if(event.getCause().equals(EntityDamageEvent.DamageCause.VOID)){
            event.setCancelled(true);
            for(PotionEffect effect : player.getActivePotionEffects()){
                player.removePotionEffect(effect.getType());
            }
            playerUUIDs.remove(player.getUniqueId());
            playerData.remove(player.getUniqueId());
            player.setGameMode(GameMode.SPECTATOR);
            if(event.getDamage() >= player.getHealth()){
                for (ItemStack itemStack : player.getInventory()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                }
            }
            if(playerUUIDs.size() <= 1){
                if(playerUUIDs.size() == 1 && Bukkit.getPlayer(playerUUIDs.get(0)) != null){
                    TitleAPI.sendTitle(Bukkit.getPlayer(playerUUIDs.get(0)), 5, 20, 5, "Victory", "You Won");
                }
                endGame();
            }else{
                player.teleport(Bukkit.getPlayer(playerUUIDs.get(0)));
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.manager.gotoLobby(player.getUniqueId());
                }}, 40);
            for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                if(playerData.containsKey(onlinePlayer.getUniqueId())){
                    onlinePlayer.sendMessage(player.getName() + " has died");
                }
            }
        }
        if(event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
            event.setCancelled(true);
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) { final Block block = event.getBlock();
        Player player = event.getPlayer();
        final Material type = block.getType();
        final Byte data = block.getData();
        block.setType(Material.AIR);
        player.sendMessage("You cannot break this block");
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                block.setType(type);
                block.setData(data);
            }
        });
    }
}
