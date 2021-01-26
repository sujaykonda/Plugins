package me._xGQD.turtlegm.Listeners;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me._xGQD.turtlegm.CustomEvent.LeftClickEntityEvent;
import me._xGQD.turtlegm.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class NBTListener implements Listener {
    Main plugin = JavaPlugin.getPlugin(Main.class);
    public NBTListener(){
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityClicked(LeftClickEntityEvent event){
        final Player player = event.getPlayer();
        final Entity hitEntity = event.getClickedPlayer();
        if(player != null && player.getItemInHand() != null){
            NBTItem nbt = new NBTItem(player.getItemInHand());
            if(nbt.hasKey("onHit")){
                NBTCompound eventCompound = nbt.getCompound("onHit");
                for(String modifier : eventCompound.getKeys()){
                    NBTCompound compound = eventCompound.getCompound(modifier);
                    switch (modifier){
                        case "explode":
                            int delay = 0;
                            int fuse = 0;
                            if(compound.hasKey("delay")){
                                delay = Integer.parseInt(compound.getString("delay"));
                            }
                            if(compound.hasKey("fuse")){
                                fuse = Integer.parseInt(compound.getString("fuse"));
                            }
                            final int finalFuse = fuse;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    TNTPrimed tnt = (TNTPrimed) hitEntity.getWorld().spawnEntity(hitEntity.getLocation(), EntityType.PRIMED_TNT);
                                    tnt.setFuseTicks(finalFuse);
                                }
                            }, delay);
                            break;
                        case "command":
                            String cmd = "";
                            if(compound.hasKey("cmd")){
                                cmd = compound.getString("cmd").replaceAll(">", " ");
                                cmd = cmd.replaceAll("@p", hitEntity.getName());
                            }
                            player.performCommand(cmd);
                        case "damage":
                            double damage = 0;
                            delay = 0;
                            if(compound.hasKey("damage")){
                                damage = Double.parseDouble(compound.getString("damage"));
                            }
                            if(compound.hasKey("delay")){
                                delay = Integer.parseInt(compound.getString("delay"));
                            }
                            final double dm = damage;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    if(hitEntity instanceof Damageable){
                                        Damageable entity = (Damageable) hitEntity;
                                        if(player.getHealth() < dm){
                                            entity.setHealth(0);
                                        } else {
                                            entity.setHealth(player.getHealth() - dm);
                                        }
                                    }
                                }
                            }, delay);
                            break;
                        case "knockback":
                            double kb = 0;
                            delay = 0;
                            if(compound.hasKey("delay")){
                                delay = Integer.parseInt(compound.getString("delay"));
                            }
                            if(compound.hasKey("knockback")){
                                kb = Double.parseDouble(compound.getString("knockback"));
                            }
                            final double kb_adder = kb;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    hitEntity.setVelocity(player.getLocation().getDirection().setY(0).normalize().multiply(kb_adder));

                                }
                            }, delay);
                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event){
        Action action = event.getAction();
        final Player player = event.getPlayer();

        if(!player.getItemInHand().getType().equals(Material.AIR)){
            NBTItem nbt = new NBTItem(player.getItemInHand());
            if((action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_BLOCK)) && nbt.hasKey("onClickBlock")){
                NBTCompound eventCompound = nbt.getCompound("onClickBlock");
                for(String modifier : eventCompound.getKeys()){
                    NBTCompound compound = eventCompound.getCompound(modifier);
                    switch (modifier){
                        case "explode":
                            int delay = 0;
                            int fuse = 0;
                            if(compound.hasKey("delay")){
                                delay = Integer.parseInt(compound.getString("delay"));
                            }
                            if(compound.hasKey("fuse")){
                                fuse = Integer.parseInt(compound.getString("fuse"));
                            }
                            final int finalFuse = fuse;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(event.getClickedBlock().getLocation(), EntityType.PRIMED_TNT);
                                    tnt.setFuseTicks(finalFuse);
                                }
                            }, delay);
                            break;
                        case "delete":
                            event.getClickedBlock().setType(Material.AIR);
                            break;
                    }
                }
            }
            if((action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) && nbt.hasKey("onRightClick")){
                NBTCompound eventCompound = nbt.getCompound("onRightClick");
                for(String modifier : eventCompound.getKeys()){
                    NBTCompound compound = eventCompound.getCompound(modifier);
                    switch (modifier){
                        case "explode":
                            int delay = 0;
                            int fuse = 0;
                            if(compound.hasKey("delay")){
                                delay = Integer.parseInt(compound.getString("delay"));
                            }
                            if(compound.hasKey("fuse")){
                                fuse = Integer.parseInt(compound.getString("fuse"));
                            }
                            final int finalFuse = fuse;
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
                                    tnt.setFuseTicks(finalFuse);
                                }
                            }, delay);
                            break;
                        case "teleport":
                            double distance = 1;
                            if(compound.hasKey("distance")){
                                distance = Double.parseDouble(compound.getString("distance"));
                            }
                            if(distance > 0){
                                Block block = player.getTargetBlock((Set<Material>) null, (int) Math.ceil(distance));
                                if(block.getType() == Material.AIR){
                                    player.teleport(player.getLocation().add(player.getLocation().getDirection().normalize().multiply(distance)));
                                }else{
                                    player.teleport(player.getLocation().add(player.getLocation().getDirection().normalize().multiply(block.getLocation().distance(player.getLocation())-1.5)));
                                }
                            }
                            break;
                        case "arrow":
                            int speed = 1;
                            float spread = 1;
                            if(compound.hasKey("speed")){
                                speed = Integer.parseInt(compound.getString("speed"));
                            }
                            if(compound.hasKey("spread")){
                                spread = Float.parseFloat(compound.getString("spread"));
                            }
                            Arrow arrow = player.getWorld().spawnArrow(player.getLocation().add(0, 1, 0).add(player.getLocation().getDirection().normalize()), player.getLocation().getDirection().normalize(), speed, spread);
                        case "knockback":
                            double kb = 0;
                            delay = 0;
                            if(compound.hasKey("delay")){
                                delay = Integer.parseInt(compound.getString("delay"));
                            }
                            if(compound.hasKey("knockback")){
                                kb = Double.parseDouble(compound.getString("knockback"));
                            }
                            final double knockback = kb;
                            player.setVelocity(player.getLocation().getDirection().normalize().multiply(0));
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    player.setVelocity(player.getLocation().getDirection().normalize().multiply(knockback));
                                }
                            }, delay);
                            break;
                    }
                }
            }
        }
    }
}
