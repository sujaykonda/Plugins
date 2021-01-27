package me._xGQD.turtlegm.Maps.StickFight;

import com.connorlinfoot.titleapi.TitleAPI;
import me._xGQD.turtlegm.ItemUtilities;
import me._xGQD.turtlegm.Maps.CTF.CTFMap;
import me._xGQD.turtlegm.Maps.Map;
import me._xGQD.turtlegm.Maps.SkyUHC.SkyUHCPlayerData;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class StickFightMap extends Map {
    protected List<UUID> playerUUIDs;
    protected Location[] island_spawns;


    public StickFightMap(String name, boolean load) {
        super(name, load);
        playerUUIDs = new ArrayList<>();
    }
    @Override
    public void onJoin(Player player){
        if(opened&&playerUUIDs.size()<2){
            player.teleport(island_spawns[playerUUIDs.size()]);
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
            ItemStack stickkb = ItemUtilities.createItem((new ItemStack(Material.STICK), "Knockback Stick"));
            stickkb.addEnchantment(Enchantment.KNOCKBACK,1);
            player.getInventory().addItem(stickkb);
        }
    }
}
