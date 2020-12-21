package me._xGQD.turtlegm;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
*         @author BlahBerrys (eballer48) - ericballard7@gmail.com
*
*         An easy-to-use API for saving, loading, and pasting WorldEdit/MCEdit
*         schematics. (Built against WorldEdit 6.1)
*
*/

@SuppressWarnings("deprecation")
public class Schematic {
    private static Main plugin = JavaPlugin.getPlugin(Main.class);
    public static void save(Player player, String schematicName) {
        try {
            File schematic = new File(plugin.getDataFolder(), "/schematics/" + schematicName);
            File dir = new File(plugin.getDataFolder(), "/schematics/");
            if (!dir.exists())
                dir.mkdirs();

            WorldEditPlugin wep = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
            WorldEdit we = wep.getWorldEdit();

            LocalPlayer localPlayer = wep.wrapPlayer(player);
            LocalSession localSession = we.getSession(localPlayer);
            ClipboardHolder selection = localSession.getClipboard();
            EditSession editSession = localSession.createEditSession(localPlayer);

            Vector min = selection.getClipboard().getMinimumPoint();
            Vector max = selection.getClipboard().getMaximumPoint();

            editSession.enableQueue();
            CuboidClipboard clipboard = new CuboidClipboard(max.subtract(min).add(new Vector(1, 1, 1)), min);
            clipboard.copy(editSession);
            SchematicFormat.MCEDIT.save(clipboard, schematic);
            editSession.flushQueue();

            player.sendMessage("Saved schematic!");
        } catch (IOException | DataException | EmptyClipboardException ex) {
            ex.printStackTrace();
        }
    }


    public static void paste(String schematicName, org.bukkit.Location pasteLoc) {
        try {
            File dir = new File(plugin.getDataFolder(), "/schematics/" + schematicName);

            EditSession editSession = new EditSession(new BukkitWorld(pasteLoc.getWorld()), 999999999);
            editSession.enableQueue();

            SchematicFormat schematic = SchematicFormat.getFormat(dir);
            CuboidClipboard clipboard = schematic.load(dir);

            clipboard.paste(editSession, BukkitUtil.toVector(pasteLoc), true);
            editSession.flushQueue();
        } catch (DataException | IOException | MaxChangedBlocksException ex) {
            ex.printStackTrace();
        }
    }

}
