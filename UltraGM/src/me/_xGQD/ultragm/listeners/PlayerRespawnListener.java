package me._xGQD.ultragm.listeners;

import me._xGQD.ultragm.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class PlayerRespawnListener implements Listener {
    Main plugin;
    public PlayerRespawnListener(Main plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 9999999, 255));
    }
}
