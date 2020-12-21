package me._xGQD.turtlegm;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class PlayerStats {
    HashMap<String, Integer> elos;
    public PlayerStats(){
        elos = new HashMap<>();
    }
    public PlayerStats(ConfigurationSection section){
        elos = new HashMap<>();
        for(String key: section.getKeys(false)){
            elos.put(key, section.getInt(key));
        }
    }
    public int getElo(String game){
        return elos.get(game);
    }
    public void addElo(String game, int elo_added){
        if(!elos.containsKey(game)){
            elos.put(game, 1000);
        }
        elos.put(game, elos.get(game) + elo_added);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        for(String game: elos.keySet()){
            string.append(game).append(": ").append(elos.get(game)).append("\n");
        }
        return string.toString();
    }
}
