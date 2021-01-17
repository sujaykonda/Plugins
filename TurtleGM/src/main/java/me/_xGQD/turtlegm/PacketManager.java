package me._xGQD.turtlegm;

import me._xGQD.turtlegm.Event.LeftClickEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

public class PacketManager {
    Main plugin = JavaPlugin.getPlugin(Main.class);
    public PacketManager(){
        PacketListenerAPI.addPacketHandler(new PacketHandler() {
            @Override
            public void onSend(SentPacket sentPacket) {
            }

            @Override
            public void onReceive(ReceivedPacket recPacket) {
                final Player player = recPacket.getPlayer();
                if(player != null){
                    if(recPacket.getPacketName().equals("PacketPlayInUseEntity")) {
                        int id = (int) recPacket.getPacketValue("a");
                        if(recPacket.getPacketValue("action").toString().equalsIgnoreCase("ATTACK")){
                            for(final Entity clickedEntity : player.getWorld().getEntities()){
                                if(clickedEntity.getEntityId() == id){
                                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            Bukkit.getPluginManager().callEvent(new LeftClickEntityEvent(player, clickedEntity));
                                        }
                                    });
                                }
                            }
                        }
                    }
                }

            }
        });
    }
}
