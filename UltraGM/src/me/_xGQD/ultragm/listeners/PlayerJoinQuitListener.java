package me._xGQD.ultragm.listeners;

import me._xGQD.ultragm.FastBoard.FastBoard;
import me._xGQD.ultragm.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerJoinQuitListener implements Listener {
    Main plugin;
    public PlayerJoinQuitListener(Main plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        FastBoard board = new FastBoard(player);

        board.updateTitle(ChatColor.GREEN + "TURTLE " + ChatColor.BLUE + "PVP");

        board.updateLines(
                "",
                "",
                "",
                "",
                "");

        plugin.boards.put(player.getUniqueId(), board);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 9999999, 255));
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();

        FastBoard board = plugin.boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }

    }
}
