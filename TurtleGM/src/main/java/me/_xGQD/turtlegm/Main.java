package me._xGQD.turtlegm;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me._xGQD.turtlegm.Commands.TGCommand;
import me._xGQD.turtlegm.Listeners.MapListener;
import me._xGQD.turtlegm.Shop.CTFShop;
import me._xGQD.turtlegm.Shop.Shop;
import me._xGQD.turtlegm.Shop.UltimateCTFPerks;
import me._xGQD.turtlegm.Shop.UltimateCTFShop;
import me._xGQD.turtlegm.scoreboard.ScoreboardLib;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    public WorldEditPlugin wep;
    public WorldEdit we;
    public PlayerManager manager;
    public HashMap<String, Shop> shops;

    @Override
    public void onEnable(){
        if(!Bukkit.getServer().getPluginManager().isPluginEnabled("WorldEdit")){
            getLogger().log(Level.SEVERE, "WorldEdit not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        ScoreboardLib.setPluginInstance(this);

        shops = new HashMap<>();
        shops.put("ctf", new CTFShop());
        shops.put("uctfPerks", new UltimateCTFPerks());
        shops.put("uctf", new UltimateCTFShop());

        new MapListener();

        new TGCommand();


        wep = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        we = wep.getWorldEdit();
        try {
            manager = new PlayerManager();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable(){
        manager.disable();
        FileConfiguration config = getConfig();
        for(UUID playerUUID: manager.playerElo.keySet()){
            ConfigurationSection section = config.createSection(playerUUID.toString());
            for(String game : manager.playerElo.get(playerUUID).elos.keySet()){
                section.set(game, manager.playerElo.get(playerUUID).elos.get(game));
            }
        }
        saveConfig();
    }
}
