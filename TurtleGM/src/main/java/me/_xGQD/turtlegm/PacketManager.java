package me._xGQD.turtlegm;

import me._xGQD.turtlegm.Event.LeftClickPlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import java.lang.reflect.Field;
import java.util.Arrays;

public class PacketManager {
    Main plugin = JavaPlugin.getPlugin(Main.class);
    public PacketManager(){
        PacketListenerAPI.addPacketHandler(new PacketHandler() {
            @Override
            public void onSend(SentPacket sentPacket) {
            }

            @Override
            public void onReceive(ReceivedPacket recPacket) {
                Object packet = recPacket.getPacket();
                final Player player = recPacket.getPlayer();
                if(recPacket.getPacketName().equals("PacketPlayInUseEntity")) {
                    int id = (int) recPacket.getPacketValue("a");
                    if(recPacket.getPacketValue("action").toString().equalsIgnoreCase("ATTACK")){
                        System.out.println("yes");
                        for(final Entity clickedPlayer : Bukkit.getOnlinePlayers()){
                            if(clickedPlayer.getEntityId() == id){
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        Bukkit.getPluginManager().callEvent(new LeftClickPlayerEvent(player, (Player) clickedPlayer));
                                    }
                                });
                            }
                        }
                    }

                }

            }
        });
    }
}
