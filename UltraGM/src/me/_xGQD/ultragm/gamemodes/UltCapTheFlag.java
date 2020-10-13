package me._xGQD.ultragm.gamemodes;

import me._xGQD.ultragm.Main;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class UltCapTheFlag extends CapTheFlagMap{
    HashMap<UUID, Integer> perks = new HashMap<>();
    HashSet<UUID> pearlcd = new HashSet<>();
    HashSet<UUID> inTimeWarp = new HashSet<>();
    public UltCapTheFlag(Main plugin, String name) {
        super(plugin, name);
        super.type = "UCTF";
    	pearlcd.clear();
    	inTimeWarp.clear();
    }

    @Override
    public void join(Player player) {
        super.join(player);
        plugin.shops.get(1).open(player);
        perks.put(player.getUniqueId(), 0);
    }
    @Override
    public void killPerks(Player player){
        super.killPerks(player);
        int perk = perks.get(player.getUniqueId());
        if(perk == 1){
            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) + 5);
        }
        if(perk == 2){
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));
        }
    }

    @Override
    public void spawn(Player player, int team){
        super.spawn(player, team);
        readPerks(player);
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action eventAction = event.getAction();
        Player player = event.getPlayer();
        if(player.getItemInHand().getType().equals(Material.ENDER_PEARL)){
            if(pearlcd.contains(player.getUniqueId())){
                event.setCancelled(true);
            }else{
                inTimeWarp.add(player.getUniqueId());
                pearlcd.add(player.getUniqueId());
                Location loc = player.getLocation();
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.teleport(loc);
                        inTimeWarp.remove(player.getUniqueId());
                        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                    }
                }, 100);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        pearlcd.remove(player.getUniqueId());
                    }
                }, 200);
            }
        }
        if((eventAction == Action.RIGHT_CLICK_BLOCK || eventAction == Action.RIGHT_CLICK_AIR)) {
            Block block = event.getPlayer().getTargetBlock((Set<Material>) null, 3);
            if (block != null && block.getType() == Material.STANDING_BANNER && !inTimeWarp.contains(player.getUniqueId())) {
                if(block.getData() == (byte) 8) {
                    if(player.getInventory().getHelmet().getType() == Material.BANNER) {
                        if(teams.get(player.getUniqueId()) == 0) {

                            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) + 10);

                            ItemStack helm = new ItemStack(Material.LEATHER_HELMET);

                            LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
                            helmLeatherMeta.setColor(Color.RED);
                            helm.setItemMeta(helmLeatherMeta);

                            player.getInventory().setHelmet(helm);

                            points[0] += 1;

                            Bukkit.broadcastMessage("Team 1 has captured a flag");

                            if(points[0] == 2) {
                                Bukkit.broadcastMessage("Team 1 has won");

                                end();
                            }
                        }
                    }else {
                        if(teams.get(player.getUniqueId()) == 1) {
                            if(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            }
                            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 1));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0));
                            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) + 10);

                            Bukkit.broadcastMessage("Team 2 has picked up the flag");

                            ItemStack banner = new ItemStack(Material.BANNER, 1, (byte) 1);

                            player.getInventory().setHelmet(banner);
                        }

                    }
                }else {
                    if(player.getInventory().getHelmet().getType() == Material.BANNER) {
                        if(teams.get(player.getUniqueId()) == 1) {

                            ItemStack helm = new ItemStack(Material.LEATHER_HELMET);

                            LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
                            helmLeatherMeta.setColor(Color.BLUE);
                            helm.setItemMeta(helmLeatherMeta);

                            player.getInventory().setHelmet(helm);

                            points[1] += 1;

                            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) + 10);

                            Bukkit.broadcastMessage("Team 2 has captured a flag");

                            if(points[1] == 2) {
                                Bukkit.broadcastMessage("Team 2 has won");

                                end();
                            }
                        }

                    }else {
                        if(teams.get(player.getUniqueId()) == 0) {
                            if(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            }
                            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 1));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0));
                            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) + 10);

                            Bukkit.broadcastMessage("Team 1 has picked up the flag");

                            ItemStack banner = new ItemStack(Material.BANNER, 1, (byte) 4);

                            player.getInventory().setHelmet(banner);
                        }
                    }
                }
            }
        }
    }
    @Override
    public void end() {
    	super.end();
    	pearlcd.clear();
    	inTimeWarp.clear();
    }
    public void readPerks(Player player){
        if(perks.containsKey(player.getUniqueId())){
            int perk = perks.get(player.getUniqueId());
            if(perk == 3){
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
            }else if(perk == 4){
                ItemStack sword = player.getInventory().getItem(0);
                sword.addEnchantment(Enchantment.KNOCKBACK, 1);
                player.getInventory().setItem(0, sword);
            }else if(perk == 5){
                player.getInventory().addItem(new ItemStack(Material.BOW));
                player.getInventory().setItem(9, new ItemStack(Material.ARROW));
            }
        }
    }
    public boolean setPerk(Player player, ItemStack item) {
        boolean close = false;
        if(Material.GOLD_INGOT.equals(item.getType())){
            close = true;
            perks.put(player.getUniqueId(), 1);
            gold.put(player.getUniqueId(), 30);
        }
        if(Material.POTION.equals(item.getType())){
            close = true;
            perks.put(player.getUniqueId(), 2);
        }
        if(Material.ENDER_PEARL.equals(item.getType())){
            close = true;
            perks.put(player.getUniqueId(), 3);
        }
        if(Material.IRON_SWORD.equals(item.getType())){
            close = true;
            perks.put(player.getUniqueId(), 4);
        }
        if(Material.BOW.equals(item.getType())){
            close = true;
            perks.put(player.getUniqueId(), 5);
        }
        readPerks(player);
        return close;
    }
}
