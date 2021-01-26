package me._xGQD.turtlegm.CustomEvent;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LeftClickEntityEvent extends Event {
    Entity clickedEntity;
    Player player;
    static HandlerList HANDLERS = new HandlerList();
    public LeftClickEntityEvent(Player player, Entity clickedEntity){
        this.player = player;
        this.clickedEntity = clickedEntity;
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

    public Entity getClickedPlayer(){
        return clickedEntity;
    }
}
