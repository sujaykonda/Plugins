package me._xGQD.turtlegm.Shop;

import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemUtilities {
    public static ItemStack createItem(Material mat, String displayName, String[] newLore){
        ItemStack item = new ItemStack(mat);
        ItemMeta meta;
        meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        List<String> lore = new ArrayList<String>();
        for(int i = 0; i < newLore.length; i++){
            lore.add(newLore[i]);
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }
    public static CompoundTag getblockdata(BlockState tile) {
        World bukkitWorld = Bukkit.getWorld(tile.getWorld().getName());
        WorldEdit we = WorldEdit.getInstance();
        BukkitWorld world = new BukkitWorld(bukkitWorld);

        EditSession edit = we.getEditSessionFactory().getEditSession((com.sk89q.worldedit.world.World) world, -1);
        Vector pos = new Vector(tile.getX(), tile.getY(), tile.getZ());
        BaseBlock block = edit.getBlock(pos);
        System.out.println(pos.toString());
        if(block != null){
            CompoundTag nbt = block.getNbtData();
            edit.flushQueue();
            if(nbt == null){
                nbt = new CompoundTag(new HashMap<String, Tag>());
            }
            return nbt;
        }
        return new CompoundTag(new HashMap<String, Tag>());
    }
    public static void setblockdata(BlockState tile, CompoundTag data) {
        World bukkitWorld = Bukkit.getWorld(tile.getWorld().getName());
        WorldEdit we = WorldEdit.getInstance();
        BukkitWorld world = new BukkitWorld(bukkitWorld);
        EditSession edit = we.getEditSessionFactory().getEditSession((com.sk89q.worldedit.world.World) world, -1);
        Vector pos = new Vector(tile.getX(), tile.getY(), tile.getZ());
        BaseBlock block = edit.getBlock(pos);
        if(block != null){
            block.setNbtData(data);
            try {
                edit.setBlock(pos, block);
                Operations.complete(edit.commit());
                System.out.println(edit.getBlock(pos).getNbtData().getValue().toString());
                System.out.println(pos.toString());
            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        }

    }

}
