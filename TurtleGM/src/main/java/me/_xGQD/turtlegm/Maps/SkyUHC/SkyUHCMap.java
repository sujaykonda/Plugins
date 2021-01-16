package me._xGQD.turtlegm.Maps.SkyUHC;

import com.connorlinfoot.titleapi.TitleAPI;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BaseItemStack;
import com.sk89q.worldedit.blocks.ChestBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import me._xGQD.turtlegm.Maps.Map;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class SkyUHCMap extends Map {

    protected Random randomGen;
    protected Location[] island_spawns;
    protected List<UUID> playerUUIDs;

    public int buildLimit;

    public SkyUHCMap(String name, boolean load) {
        super(name, load);
        playerUUIDs = new ArrayList<>();
        randomGen = new Random();
    }

    public static String getType(){
        return "skyuhc";
    }

    @Override
    public String getMapType(){
        return "skyuhc";
    }

    @Override
    public void onJoin(Player player){
        if(opened && playerUUIDs.size() < 4){
            player.teleport(island_spawns[playerUUIDs.size()]);
            playerUUIDs.add(player.getUniqueId());
            playerData.put(player.getUniqueId(), new SkyUHCPlayerData(playerUUIDs.size()));
            player.setGameMode(GameMode.SURVIVAL);
            player.setLevel(0);
            player.setExp(0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 9999999, 4));
            player.setHealth(40.0D);
            player.setFoodLevel(20);
            player.getInventory().clear();
            plugin.shops.get("skyuhckits").open(player);
        }
    }

    @Override
    public void saveMap(Player player){
        try {
            YamlConfiguration config = getConfig();

            config.set("paste_location.world", plugin.wep.getSelection(player).getMinimumPoint().getWorld().getName());

            buildLimit = (int) plugin.wep.getSelection(player).getMaximumPoint().getY() - 2;

            config.set("buildLimit", buildLimit);

            config.set("type", getType());

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
    @Override
    public void loadAll(){
        try {
            island_spawns = new Location[]{null, null, null, null};

            YamlConfiguration config = getConfig();

            World bukkitWorld = Bukkit.getWorld(config.getString("paste_location.world"));
            com.sk89q.worldedit.world.World worldEditWorld = BukkitUtil.getLocalWorld(bukkitWorld);
            island_spawns[0] = new Location(bukkitWorld,
                    config.getDouble("loc1.x"),
                    config.getDouble("loc1.y"),
                    config.getDouble("loc1.z"));

            island_spawns[1] = new Location(bukkitWorld,
                    config.getDouble("loc2.x"),
                    config.getDouble("loc2.y"),
                    config.getDouble("loc2.z"));

            island_spawns[2] = new Location(bukkitWorld,
                    config.getDouble("loc3.x"),
                    config.getDouble("loc3.y"),
                    config.getDouble("loc3.z"));

            island_spawns[3] = new Location(bukkitWorld,
                    config.getDouble("loc4.x"),
                    config.getDouble("loc4.y"),
                    config.getDouble("loc4.z"));

            buildLimit = config.getInt("buildLimit");

            loadMap();

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void randomizeOres(EditSession editSession) throws MaxChangedBlocksException {
        final RandomPattern randomOre = new RandomPattern();
        randomOre.add(new Pattern() {
            @Override
            public BaseBlock apply(Vector vector) {
                return new BaseBlock(Material.IRON_ORE.ordinal());
            }
        }, 60);
        randomOre.add(new Pattern() {
            @Override
            public BaseBlock apply(Vector vector) {
                return new BaseBlock(Material.DIAMOND_ORE.ordinal());
            }
        }, 10);

        randomOre.add(new Pattern() {
            @Override
            public BaseBlock apply(Vector vector) {
                return new BaseBlock(Material.GOLD_ORE.ordinal());
            }
        }, 20);

        randomOre.add(new Pattern() {
            @Override
            public BaseBlock apply(Vector vector) {
                return new BaseBlock(Material.LAPIS_ORE.ordinal());
            }
        }, 10);

        com.sk89q.worldedit.patterns.Pattern orePattern = new com.sk89q.worldedit.patterns.Pattern(){

            @Override
            public BaseBlock next(Vector vector) {
                return randomOre.apply(vector);
            }

            @Override
            public BaseBlock next(int i, int i1, int i2) {
                return randomOre.apply(new Vector(i, i1, i2));
            }
        };
        Set<BaseBlock> blocksToReplace = new HashSet<>();
        blocksToReplace.add(new BaseBlock(Material.IRON_ORE.ordinal()));

        editSession.replaceBlocks(map.getRegion(), blocksToReplace, orePattern);
    }

    public void randomizeChest(EditSession editSession) throws MaxChangedBlocksException {
        final List<ItemStack[]> itemRolls = new ArrayList<>();
        itemRolls.add(new ItemStack[]{new ItemStack(Material.IRON_HELMET),
                new ItemStack(Material.IRON_SWORD),
                new ItemStack(Material.STICK, 4),
                new ItemStack(Material.COOKED_BEEF, 3)});
        itemRolls.add(new ItemStack[]{new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.STICK, 4),
                new ItemStack(Material.IRON_INGOT, 2),
                new ItemStack(Material.COOKED_BEEF, 3)});
        itemRolls.add(new ItemStack[]{new ItemStack(Material.IRON_SWORD),
                new ItemStack(Material.STICK, 4),
                new ItemStack(Material.IRON_INGOT, 2),
                new ItemStack(Material.FISHING_ROD)});
        itemRolls.add(new ItemStack[]{new ItemStack(Material.BOW),
                new ItemStack(Material.ARROW, 8),
                new ItemStack(Material.COOKED_BEEF, 3)});
        itemRolls.add(new ItemStack[]{new ItemStack(Material.DIAMOND, 2),
                new ItemStack(Material.STICK, 4),
                new ItemStack(Material.COOKED_BEEF, 3)});
        itemRolls.add(new ItemStack[]{new ItemStack(Material.DIAMOND_BOOTS),
                new ItemStack(Material.STONE, 16),
                new ItemStack(Material.FISHING_ROD),
                new ItemStack(Material.COOKED_BEEF, 3)});
        itemRolls.add(new ItemStack[]{new ItemStack(Material.STONE_PICKAXE),
                new ItemStack(Material.WOOD, 16),
                new ItemStack(Material.COOKED_BEEF, 3)});
        itemRolls.add(new ItemStack[]{new ItemStack(Material.COOKIE, 1),
                new ItemStack(Material.IRON_CHESTPLATE)});

        com.sk89q.worldedit.patterns.Pattern chestPattern = new com.sk89q.worldedit.patterns.Pattern(){

            @Override
            public BaseBlock next(Vector vector) {
                ChestBlock chest = new ChestBlock();
                for(int i = 0; i < (randomGen.nextInt(3) + 1); i++){
                    ItemStack[] roll = itemRolls.get(randomGen.nextInt(itemRolls.size()));
                    for(int j = 0; j < roll.length; j++){
                        BaseItemStack[] items = chest.getItems();
                        if(items == null){
                            items = new BaseItemStack[27];
                        }
                        BaseItemStack item = new BaseItemStack(roll[j].getTypeId(), roll[j].getAmount());
                        int itemIndexRoll = randomGen.nextInt(itemRolls.size());
                        if(items[itemIndexRoll] == null || item.getType() == 0){
                            items[itemIndexRoll] = item;
                        }else{
                            for(int newIndexRoll = 0; newIndexRoll < items.length; newIndexRoll++){
                                if(items[newIndexRoll] == null || item.getType() == 0){
                                    items[newIndexRoll] = item;
                                    break;
                                }
                            }
                        }
                        chest.setItems(items);
                    }
                }
                return chest;
            }

            @Override
            public BaseBlock next(int x, int y, int z) {
                return this.next(new Vector(x, y, z));
            }
        };

        Set<BaseBlock> chestsToReplace = new HashSet<>();
        chestsToReplace.add(new BaseBlock(Material.SPONGE.ordinal()));
        editSession.replaceBlocks(map.getRegion(), chestsToReplace, chestPattern);
    }

    @Override
    public void setupGame() {
        this.loadMap();

        playerUUIDs = new ArrayList<>();

        for(Location spawn : island_spawns){
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()-1, spawn.getBlockZ()).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()+2, spawn.getBlockZ()).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX()+1, spawn.getBlockY()+1, spawn.getBlockZ()).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX()-1, spawn.getBlockY()+1, spawn.getBlockZ()).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()+1, spawn.getBlockZ()+1).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()+1, spawn.getBlockZ()-1).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX()+1, spawn.getBlockY(), spawn.getBlockZ()).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX()-1, spawn.getBlockY(), spawn.getBlockZ()).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()+1).setType(Material.BARRIER);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()-1).setType(Material.BARRIER);
        }

        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitUtil.getLocalWorld(island_spawns[0].getWorld()), -1);

        try {
            randomizeOres(editSession);
            randomizeChest(editSession);
            Operations.complete(editSession.commit());
        } catch (WorldEditException e) {
            e.printStackTrace();
        }

        opened = true;
    }

    @Override
    public void startGame(){
        started = true;

        for(Location spawn : island_spawns){
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()-1, spawn.getBlockZ()).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()+2, spawn.getBlockZ()).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX()+1, spawn.getBlockY()+1, spawn.getBlockZ()).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX()-1, spawn.getBlockY()+1, spawn.getBlockZ()).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()+1, spawn.getBlockZ()+1).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY()+1, spawn.getBlockZ()-1).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX()+1, spawn.getBlockY(), spawn.getBlockZ()).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX()-1, spawn.getBlockY(), spawn.getBlockZ()).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()+1).setType(Material.AIR);
            spawn.getWorld().getBlockAt(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ()-1).setType(Material.AIR);
        }
    }

    @Override
    public void endGame() {
        this.loadMap();
        opened = false;
        started = false;
        for(UUID playerUUID: playerData.keySet()){
            plugin.manager.gotoLobby(playerUUID);
        }
        playerUUIDs.clear();
        playerData.clear();
    }

    @Override
    public void changeSettings(String[] args, CommandSender commandSender){
        if(!(commandSender instanceof Player)){
            return;
        }
        Player player = (Player) commandSender;
        try {
            YamlConfiguration config = getConfig();

            if(args[3].equals("setspawn")){
                if(island_spawns == null){
                    island_spawns = new Location[]{null, null, null, null};
                }
                if(args[4].equals("1")){
                    island_spawns[0] = player.getLocation();

                    config.set("loc1.x", island_spawns[0].getX());
                    config.set("loc1.y", island_spawns[0].getY());
                    config.set("loc1.z", island_spawns[0].getZ());
                }else if(args[4].equals("2")) {
                    island_spawns[1] = player.getLocation();

                    config.set("loc2.x", island_spawns[1].getX());
                    config.set("loc2.y", island_spawns[1].getY());
                    config.set("loc2.z", island_spawns[1].getZ());
                }else if(args[4].equals("3")) {
                    island_spawns[2] = player.getLocation();

                    config.set("loc3.x", island_spawns[2].getX());
                    config.set("loc3.y", island_spawns[2].getY());
                    config.set("loc3.z", island_spawns[2].getZ());
                }else {
                    island_spawns[3] = player.getLocation();

                    config.set("loc4.x", island_spawns[3].getX());
                    config.set("loc4.y", island_spawns[3].getY());
                    config.set("loc4.z", island_spawns[3].getZ());
                }
                player.sendMessage("You have set the spawn");
            }

            saveConfig(config);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayerDamage(EntityDamageEvent event, final Player player){
        if(event.getDamage() >= player.getHealth() || event.getCause().equals(EntityDamageEvent.DamageCause.VOID)){
            if(event instanceof EntityDamageByEntityEvent){
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                if(e.getDamager() instanceof Player){
                    Player damager = (Player) e.getDamager();
                    ExperienceOrb orb = (ExperienceOrb) damager.getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
                    orb.setExperience(10);
                }
            }
            event.setCancelled(true);
            for(PotionEffect effect : player.getActivePotionEffects()){
                player.removePotionEffect(effect.getType());
            }
            playerUUIDs.remove(player.getUniqueId());
            playerData.remove(player.getUniqueId());
            player.setGameMode(GameMode.SPECTATOR);
            if(event.getDamage() >= player.getHealth()){
                for (ItemStack itemStack : player.getInventory()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
                }
            }
            if(playerUUIDs.size() <= 1){
                if(playerUUIDs.size() == 1 && Bukkit.getPlayer(playerUUIDs.get(0)) != null){
                    TitleAPI.sendTitle(Bukkit.getPlayer(playerUUIDs.get(0)), 5, 20, 5, "Victory", "You were the last man standing");
                }
                endGame();
            }else{
                player.teleport(Bukkit.getPlayer(playerUUIDs.get(0)));
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    plugin.manager.gotoLobby(player.getUniqueId());
                }}, 40);
            for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                if(playerData.containsKey(onlinePlayer.getUniqueId())){
                    onlinePlayer.sendMessage(player.getName() + " has died");
                }
            }
        }

    }

    protected boolean checkIfDrops(ItemStack tool, Block block){
        int tool_level = 0;
        int block_level = 0;
        switch(tool.getType()){
            case DIAMOND_PICKAXE:
                tool_level = 4;
                break;
            case IRON_PICKAXE:
                tool_level = 3;
                break;
            case STONE_PICKAXE:
                tool_level = 2;
                break;
            case GOLD_PICKAXE:
            case WOOD_PICKAXE:
                tool_level = 1;
                break;
            default:
                break;
        }
        switch(block.getType()){
            case DIAMOND_BLOCK:
            case DIAMOND_ORE:
            case GOLD_BLOCK:
            case GOLD_ORE:
                block_level = 3;
                break;
            case IRON_BLOCK:
            case IRON_ORE:
            case LAPIS_BLOCK:
            case LAPIS_ORE:
                block_level = 2;
                break;
            case STONE:
                block_level = 1;
                break;
            default:
                break;
        }
        return tool_level >= block_level;
    }
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if(checkIfDrops(event.getPlayer().getItemInHand(), event.getBlock())){
            if(event.getBlock().getType().equals(Material.LEAVES)){
                event.getBlock().setType(Material.AIR);
                if(randomGen.nextInt(3) > 1){
                    event.getPlayer().getInventory().addItem(new ItemStack(Material.APPLE));
                }
            }
            if(event.getBlock().getType().equals(Material.IRON_ORE)){
                event.getBlock().setType(Material.AIR);
                ExperienceOrb orb = (ExperienceOrb) event.getPlayer().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.EXPERIENCE_ORB);
                orb.setExperience(1);
                event.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_INGOT, randomGen.nextInt(3) + 1));
            }
            if(event.getBlock().getType().equals(Material.DIAMOND_ORE)){
                event.getBlock().setType(Material.AIR);
                ExperienceOrb orb = (ExperienceOrb) event.getPlayer().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.EXPERIENCE_ORB);
                orb.setExperience(2);
                event.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND, randomGen.nextInt(3) + 1));
            }
            if(event.getBlock().getType().equals(Material.GOLD_ORE)){
                event.getBlock().setType(Material.AIR);
                ExperienceOrb orb = (ExperienceOrb) event.getPlayer().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.EXPERIENCE_ORB);
                orb.setExperience(2);
                event.getPlayer().getInventory().addItem(new ItemStack(Material.GOLD_INGOT, randomGen.nextInt(3) + 1));
            }
            if(event.getBlock().getType().equals(Material.LAPIS_ORE)){
                event.getBlock().setType(Material.AIR);
                ExperienceOrb orb = (ExperienceOrb) event.getPlayer().getWorld().spawnEntity(event.getBlock().getLocation(), EntityType.EXPERIENCE_ORB);
                orb.setExperience(3);
                event.getPlayer().getInventory().addItem(new ItemStack(351, (randomGen.nextInt(3) + 1) * 8, (short) 4));
            }
        }
    }
    @Override
    public void onBlockPlace(BlockPlaceEvent event){
        final Block block = event.getBlock();
        if(!(new Vector(block.getX(), block.getY(), block.getZ()).containedWithin(map.getMinimumPoint(), map.getMaximumPoint()))){
            event.getPlayer().sendMessage("Build limit reached");
            event.setCancelled(true);
        }
    }
    @Override
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            if(event.getPlayer().getItemInHand().getType().equals(Material.COOKIE)){
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));
                        event.getPlayer().setItemInHand(null);
                    }
                });
            }
        }
    }
}
