package me._xGQD.turtlegm.Listeners;

import me._xGQD.turtlegm.Main;
import me._xGQD.turtlegm.scoreboard.ScoreboardLib;
import me._xGQD.turtlegm.scoreboard.common.EntryBuilder;
import me._xGQD.turtlegm.scoreboard.type.Entry;
import me._xGQD.turtlegm.scoreboard.type.Scoreboard;
import me._xGQD.turtlegm.scoreboard.type.ScoreboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MapListener implements Listener {
    Main plugin = JavaPlugin.getPlugin(Main.class);
    public MapListener(){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event){
        plugin.manager.join(event.getPlayer());
        plugin.prevVelocity.put(event.getPlayer().getUniqueId(), event.getPlayer().getVelocity());

        Scoreboard scoreboard = ScoreboardLib.createScoreboard(event.getPlayer())
                .setHandler(new ScoreboardHandler() {
                    @Override
                    public String getTitle(Player player) {
                        return ChatColor.GREEN + "TurtlePvP";
                    }

                    @Override
                    public List<Entry> getEntries(Player player) {
                        EntryBuilder builder = new EntryBuilder();
                        builder.next(ChatColor.DARK_GREEN + "Online Players: " + ChatColor.GRAY + Bukkit.getOnlinePlayers().size());
                        if(plugin.manager.playerIn(player)){
                            plugin.manager.updateBoard(player, builder);
                        }
                        return builder.build();
                    }
                }).setUpdateInterval(20);
        scoreboard.activate();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event){
        if(plugin.manager.playerIn(event.getPlayer())){
            plugin.manager.blockBreak(event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event){
        if(plugin.manager.playerIn(event.getPlayer())){
            plugin.manager.blockPlace(event);
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event){
        if(plugin.manager.playerIn(event.getPlayer())){
            plugin.manager.playerMove(event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event){
        if(plugin.manager.playerIn(event.getPlayer())) {
            plugin.manager.playerInteract(event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDrop(PlayerDropItemEvent event){
        if(plugin.manager.playerIn(event.getPlayer())){
            plugin.manager.playerDrop(event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player && plugin.manager.playerIn((Player) event.getEntity())){
            plugin.manager.entityDamage(event);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getInventory().equals(plugin.manager.typesInventory)){
            switch (event.getCurrentItem().getType()){
                case BANNER:
                    event.setCancelled(true);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().openInventory(plugin.manager.mapInventories.get("ctf"));
                    break;
                case ENDER_PEARL:
                    event.setCancelled(true);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().openInventory(plugin.manager.mapInventories.get("uctf"));
                    break;
                case WOOL:
                    event.setCancelled(true);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().openInventory(plugin.manager.mapInventories.get("rctf"));
                    break;
                case DIAMOND_ORE:
                    event.setCancelled(true);
                    event.getWhoClicked().closeInventory();
                    event.getWhoClicked().openInventory(plugin.manager.mapInventories.get("skyuhc"));
                    break;
            }
        }
        for(String map_type : plugin.manager.mapInventories.keySet()){
            if(plugin.manager.mapInventories.get(map_type).equals(event.getInventory())){
                if(event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()){
                    String map_name = event.getCurrentItem().getItemMeta().getDisplayName();
                    event.setCancelled(true);
                    event.getWhoClicked().closeInventory();
                    Bukkit.dispatchCommand(event.getWhoClicked(), "tg join " + map_type + " " + map_name);
                    break;
                }
            }
        }
        for(String shop : plugin.shops.keySet()){
            if (plugin.shops.get(shop).isShop(event.getInventory())){
                plugin.shops.get(shop).onInventoryClick(event);
            }
        }
    }
}
