package me._xGQD.turtlegm.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LeftClickPlayerEvent extends Event {
    Player clickedPlayer;
    Player player;
    static HandlerList HANDLERS = new HandlerList();
    public LeftClickPlayerEvent(Player player, Player clickedPlayer){
        this.player = player;
        this.clickedPlayer = clickedPlayer;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer(){
        return player;
    }

    public Player getClickedPlayer(){
        return clickedPlayer;
    }
}
