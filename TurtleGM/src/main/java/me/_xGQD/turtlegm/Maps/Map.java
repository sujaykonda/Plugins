package me._xGQD.turtlegm.Maps;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import me._xGQD.turtlegm.Main;
import me._xGQD.turtlegm.scoreboard.common.EntryBuilder;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.List;

public class Map {
    protected List<NPC> npcs;
    public HashMap<UUID, PlayerData> playerData;
    public boolean opened;
    public boolean started;
    protected static Main plugin = JavaPlugin.getPlugin(Main.class);
    protected String name;
    protected String type;
    protected Clipboard map;
    public Map(String name, boolean load){
        this.name = name;
        if(load){
            loadAll();
        }
        opened = false;
        npcs = new ArrayList<>();
        playerData = new HashMap<>();
    }

    public boolean onJoin(Player player) { return false; }
    public static String getType(){
        return "map";
    }
    public String getMapType(){
        return "map";
    }

    public YamlConfiguration getConfig() throws IOException, InvalidConfigurationException {
        File maps_dir = new File(plugin.getDataFolder(), "/maps");
        if (!maps_dir.exists())
            maps_dir.mkdir();

        File map_file = new File(plugin.getDataFolder(), "/maps/" + getMapType() + name + ".yml");

        if(!map_file.exists()){
            map_file.createNewFile();
        }

        YamlConfiguration config = new YamlConfiguration();

        config.load(map_file);

        return config;
    }

    public void saveConfig(YamlConfiguration config) throws IOException {
        File maps_dir = new File(plugin.getDataFolder(), "/maps");
        if (!maps_dir.exists())
            maps_dir.mkdirs();

        File map_file = new File(plugin.getDataFolder(), "/maps/" + getMapType() + name + ".yml");

        config.save(map_file);
    }

    public File getSchematicFile() throws IOException {
        File schematics_dir = new File(plugin.getDataFolder(), "/schematics");
        if (!schematics_dir.exists())
            schematics_dir.mkdirs();
        File schem_file = new File(plugin.getDataFolder(), "/schematics/" + getMapType() + name + ".schematic");

        if(schem_file.exists()){
            schem_file.createNewFile();
        }
        return schem_file;
    }

    public void changeSettings(String[] args, CommandSender commandSender){ }
    public void setupGame(){ }
    public void startGame(){ }
    public void endGame(){ }
    public static void openKitEdit(Player player){ }
    public static void saveKitEdit(Player player) { }
    public static Object loadKit(UUID uuid, ConfigurationSection config){ return null; }

    public void onDisable(){
        for(NPC npc: npcs){
            npc.destroy();
        }
    }
    public PlayerData getPlayerData(UUID playerUUID){
        return playerData.get(playerUUID);
    }
    public void onPlayerDrop(PlayerDropItemEvent event){ }
    public void onPlayerInteract(PlayerInteractEvent event){ }
    public void onPlayerDamage(EntityDamageEvent event, Player player){ }
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event, Player player, Player cause){ }
    public void onBlockPlace(BlockPlaceEvent event){ }
    public void onBlockBreak(BlockBreakEvent event){ }
    public void onPlayerMove(PlayerMoveEvent event){ }

    public void loadMap(){
        try {
            File schematic_file = getSchematicFile();

            YamlConfiguration config = getConfig();

            World bukkitWorld = Bukkit.getWorld(config.getString("paste_location.world"));
            com.sk89q.worldedit.world.World worldEditWorld = BukkitUtil.getLocalWorld(bukkitWorld);

            ClipboardFormat format = ClipboardFormat.findByFile(schematic_file);
            assert format != null;
            ClipboardReader reader = format.getReader(new FileInputStream(schematic_file));
            map = reader.read(worldEditWorld.getWorldData());
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(worldEditWorld, -1);
            Operation operation = new ClipboardHolder(map, worldEditWorld.getWorldData())
                    .createPaste(editSession, worldEditWorld.getWorldData())
                    .to(map.getMinimumPoint())
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void loadAll(){ }
    public EntryBuilder updateBoard(EntryBuilder builder, Player player){
        return builder;
    }
    public void saveMap(Player player) {
        try {
            YamlConfiguration config = getConfig();

            config.set("paste_location.world", plugin.wep.getSelection(player).getMinimumPoint().getWorld().getName());

            config.set("type", getMapType());

            saveConfig(config);

            File schematic_file = getSchematicFile();

            LocalPlayer localPlayer = plugin.wep.wrapPlayer(player);
            LocalSession localSession = plugin.we.getSession(localPlayer);

            com.sk89q.worldedit.world.World world = plugin.wep.getSelection(player).getRegionSelector().getWorld();
            Vector max = plugin.wep.getSelection(player).getNativeMaximumPoint();
            Vector min = plugin.wep.getSelection(player).getNativeMinimumPoint();

            CuboidRegion region = new CuboidRegion(world, min, max);
            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                    editSession, region, clipboard, region.getMinimumPoint()
            );
            // configure here
            Operations.complete(forwardExtentCopy);
            ClipboardWriter writer = ClipboardFormat.SCHEMATIC.getWriter(new FileOutputStream(schematic_file));
            writer.write(clipboard, world.getWorldData());
            writer.close();

        } catch (IOException | WorldEditException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}