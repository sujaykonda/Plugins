package me._xGQD.turtlegm.Maps.CTF;

import com.connorlinfoot.titleapi.TitleAPI;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.IntTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.Vector;
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
import me._xGQD.turtlegm.Maps.Map;
import me._xGQD.turtlegm.Maps.UltimateCTF.UltimateCTFPlayerData;
import me._xGQD.turtlegm.Maps.Utilities;
import me._xGQD.turtlegm.Shop.ItemUtilities;
import me._xGQD.turtlegm.scoreboard.common.EntryBuilder;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class CTFMap extends Map {

    final Color[] redblue = new Color[]{Color.RED, Color.BLUE};


    public int team_count_1;
    public int team_count_2;

    protected int[] points;
    protected int buildLimit;
    protected Location[] spawn;
    protected Clipboard map;
    protected Location paste_loc;
    protected boolean team_selector;

    public HashMap<UUID, CTFKit> kits;

    public CTFMap(String name, boolean load){
        super(name, load);
        team_selector = false;
        team_count_1 = 0;
        team_count_2 = 0;
        points = new int[]{0, 0};
        kits = new HashMap<>();
    }
    public static String getType(){
        return "ctf";
    }

    @Override
    public String getMapType(){
        return "ctf";
    }

    public static CTFKit loadKit(UUID uuid, ConfigurationSection config){
        if(config != null) {
            int sword = 0;
            int blocks = 1;
            int steak = 2;
            int shears = 3;
            int shop = 8;
            if(config.contains("sword")){
                sword = config.getInt("sword");
            }
            if(config.contains("blocks")){
                blocks = config.getInt("blocks");
            }
            if(config.contains("steak")){
                steak = config.getInt("steak");
            }
            if(config.contains("shears")){
                shears = config.getInt("shears");
            }
            if(config.contains("shop")){
                shop = config.getInt("shop");
            }

            return (new CTFKit(sword, blocks, steak, shears, shop));
        }else{
            return (new CTFKit(0, 1, 2, 3, 8));
        }
    }

    public static void openKitEdit(Player player){
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemStack glass = new ItemStack(Material.WOOL);
        ItemStack steak = new ItemStack(Material.COOKED_BEEF);
        ItemStack shears = new ItemStack(Material.SHEARS);
        ItemStack shop = new ItemStack(Material.NETHER_STAR);

        player.getInventory().clear();
        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, glass);
        player.getInventory().setItem(2, steak);
        player.getInventory().setItem(3, shears);
        player.getInventory().setItem(8, shop);
    }
    @Override
    public CTFPlayerData getPlayerData(UUID playerUUID){
        return (CTFPlayerData) playerData.get(playerUUID);
    }
    public static void saveKitEdit(Player player) {
        int sword = 0;
        int blocks = 1;
        int steak = 2;
        int shears = 3;
        int shop = 8;
        for(int i = 0; i < player.getInventory().getSize(); i++){
            if(player.getInventory().getItem(i) != null){
                if(player.getInventory().getItem(i).getType().equals(Material.IRON_SWORD)){
                    sword = i;
                }
                if(player.getInventory().getItem(i).getType().equals(Material.WOOL)){
                    blocks = i;
                }
                if(player.getInventory().getItem(i).getType().equals(Material.COOKED_BEEF)){
                    steak = i;
                }
                if(player.getInventory().getItem(i).getType().equals(Material.SHEARS)){
                    shears = i;
                }
                if(player.getInventory().getItem(i).getType().equals(Material.NETHER_STAR)){
                    shop = i;
                }
            }
        }
        File file = new File(plugin.getDataFolder(), "/kits/" + player.getUniqueId().toString() + ".yml");
        if(file.exists()){
            try{
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.createSection(getType());
                config.set(getType() + ".sword", sword);
                config.set(getType() + ".blocks", blocks);
                config.set(getType() + ".steak", steak);
                config.set(getType() + ".shears", shears);
                config.set(getType() + ".shop", shop);
                config.save(file);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            try{
                YamlConfiguration config = new YamlConfiguration();
                config.createSection(getType());
                config.set(getType() + ".sword", sword);
                config.set(getType() + ".blocks", blocks);
                config.set(getType() + ".steak", steak);
                config.set(getType() + ".shears", shears);
                config.set(getType() + ".shop", shop);
                config.save(file);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
    public void load(){
        try {
            spawn = new Location[]{null, null};

            File schematic_file = getSchematicFile();

            YamlConfiguration config = getConfig();

            World bukkitWorld = Bukkit.getWorld(config.getString("paste_location.world"));
            com.sk89q.worldedit.world.World worldEditWorld = BukkitUtil.getLocalWorld(bukkitWorld);

            spawn[0] = new Location(bukkitWorld,
                    config.getDouble("loc1.x"),
                    config.getDouble("loc1.y"),
                    config.getDouble("loc1.z"));

            spawn[1] = new Location(bukkitWorld,
                    config.getDouble("loc2.x"),
                    config.getDouble("loc2.y"),
                    config.getDouble("loc2.z"));

            buildLimit = config.getInt("buildLimit");

            if(config.contains("teamSelector")){
                team_selector = config.getBoolean("teamSelector");
            }else{
                team_selector = false;
            }

            ClipboardFormat format = ClipboardFormat.findByFile(schematic_file);
            ClipboardReader reader = format.getReader(new FileInputStream(schematic_file));
            map = reader.read(worldEditWorld.getWorldData());
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(worldEditWorld, -1);
            Operation operation = new ClipboardHolder(map, worldEditWorld.getWorldData())
                    .createPaste(editSession, worldEditWorld.getWorldData())
                    .to(map.getMinimumPoint())
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);

        } catch (IOException | InvalidConfigurationException | WorldEditException e) {
            e.printStackTrace();
        }
    }

    public void spawn(Player player, int team){
        player.teleport(Utilities.lookAt(spawn[team], spawn[(team + 1) % 2]));
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        ItemStack wool;
        if(team == 0)
            wool = new Wool(DyeColor.RED).toItemStack(64);
        else
            wool = new Wool(DyeColor.BLUE).toItemStack(64);
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemStack steak = new ItemStack(Material.COOKED_BEEF);
        ItemStack shop = new ItemStack(Material.NETHER_STAR);


        LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
        helmLeatherMeta.setColor(redblue[team]);
        helm.setItemMeta(helmLeatherMeta);

        LeatherArmorMeta chestplateLeatherMeta = (LeatherArmorMeta)chestplate.getItemMeta();
        chestplateLeatherMeta.setColor(redblue[team]);
        chestplate.setItemMeta(chestplateLeatherMeta);

        LeatherArmorMeta leggingsLeatherMeta = (LeatherArmorMeta)leggings.getItemMeta();
        leggingsLeatherMeta.setColor(redblue[team]);
        leggings.setItemMeta(leggingsLeatherMeta);

        LeatherArmorMeta bootsLeatherMeta = (LeatherArmorMeta)boots.getItemMeta();
        bootsLeatherMeta.setColor(redblue[team]);
        boots.setItemMeta(bootsLeatherMeta);

        ItemMeta shopmeta = shop.getItemMeta();
        shopmeta.setDisplayName("Shop");
        shop.setItemMeta(shopmeta);

        player.getInventory().clear();
        player.getInventory().setHelmet(helm);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        player.getInventory().setItem(kits.get(player.getUniqueId()).sword, sword);
        player.getInventory().setItem(kits.get(player.getUniqueId()).blocks, wool);
        player.getInventory().setItem(kits.get(player.getUniqueId()).steak, steak);
        player.getInventory().setItem(kits.get(player.getUniqueId()).shop, shop);

        readBuffs(player);
    }
    public void readBuffs(Player player){
        CTFPlayerData data = getPlayerData(player.getUniqueId());
        for(String buff : data.buffs){
            switch (buff){
                case "permarmor":
                    ItemStack chest = new ItemStack(Material.GOLD_CHESTPLATE);
                    ItemStack boots = new ItemStack(Material.GOLD_BOOTS);
                    chest.addEnchantment(Enchantment.DURABILITY, 3);
                    boots.addEnchantment(Enchantment.DURABILITY, 3);
                    player.getInventory().setChestplate(chest);
                    player.getInventory().setBoots(boots);
                    break;
                case "permsword":
                    ItemStack sword = new ItemStack(Material.GOLD_SWORD);
                    sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
                    sword.addEnchantment(Enchantment.DURABILITY, 3);
                    player.getInventory().setItem(kits.get(player.getUniqueId()).sword, sword);
                    break;
                case "shears":
                    player.getInventory().setItem(kits.get(player.getUniqueId()).shears, new ItemStack(Material.SHEARS));
                    break;
            }
        }
    }
    @Override
    public void onJoin(Player player){
        if(opened){
            File file = new File(plugin.getDataFolder(), "/kits/" + player.getUniqueId().toString() + ".yml");
            if(file.exists() && YamlConfiguration.loadConfiguration(file).getConfigurationSection(getType()) != null){
                System.out.println(YamlConfiguration.loadConfiguration(file).getConfigurationSection(getType()).toString());
                kits.put(player.getUniqueId(), loadKit(player.getUniqueId(), YamlConfiguration.loadConfiguration(file).getConfigurationSection(getType())));
            }else{
                kits.put(player.getUniqueId(), loadKit(player.getUniqueId(), null));
            }
            if(team_selector){
                plugin.shops.get("team_selector_ctf").open(player);
            }else{
                if(team_count_1 <= team_count_2){
                    playerData.put(player.getUniqueId(), new CTFPlayerData(0));
                    spawn(player, 0);
                    team_count_1 += 1;
                }else{
                    playerData.put(player.getUniqueId(), new CTFPlayerData(1));
                    spawn(player, 1);
                    team_count_2 += 1;
                }
            }
        }
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
                if(spawn == null){
                    spawn = new Location[]{null, null};
                }
                if(args[4].equals("1")){
                    spawn[0] = player.getLocation();

                    config.set("loc1.x", spawn[0].getX());
                    config.set("loc1.y", spawn[0].getY());
                    config.set("loc1.z", spawn[0].getZ());
                }else {
                    spawn[1] = player.getLocation();

                    config.set("loc2.x", spawn[1].getX());
                    config.set("loc2.y", spawn[1].getY());
                    config.set("loc2.z", spawn[1].getZ());
                }
                player.sendMessage("You have set the spawn");
            }
            if(args[3].equals("allow") && args[4].equals("team") && args[5].equals("selector")){
                team_selector = true;
                config.set("teamSelector", true);
                player.sendMessage("You have turned on the team selector");
            }
            if(args[3].equals("deny") && args[4].equals("team") && args[5].equals("selector")){
                team_selector = false;
                config.set("teamSelector", false);
                player.sendMessage("You have turned off the team selector");
            }
            saveConfig(config);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setupGame(){
        this.load();

        World world = spawn[0].getWorld();
        world.getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY() + 2, spawn[0].getBlockZ()).setType(Material.BARRIER);
        world.getBlockAt(spawn[0].getBlockX() + 1, spawn[0].getBlockY() + 1, spawn[0].getBlockZ()).setType(Material.BARRIER);
        world.getBlockAt(spawn[0].getBlockX() - 1, spawn[0].getBlockY() + 1, spawn[0].getBlockZ()).setType(Material.BARRIER);
        world.getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY() + 1, spawn[0].getBlockZ() + 1).setType(Material.BARRIER);
        world.getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY() + 1, spawn[0].getBlockZ() - 1).setType(Material.BARRIER);
        world.getBlockAt(spawn[0].getBlockX() + 1, spawn[0].getBlockY(), spawn[0].getBlockZ()).setType(Material.BARRIER);
        world.getBlockAt(spawn[0].getBlockX() - 1, spawn[0].getBlockY(), spawn[0].getBlockZ()).setType(Material.BARRIER);
        world.getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY(), spawn[0].getBlockZ() + 1).setType(Material.BARRIER);
        world.getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY(), spawn[0].getBlockZ() - 1).setType(Material.BARRIER);

        world.getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY() + 2, spawn[1].getBlockZ()).setType(Material.BARRIER);
        world.getBlockAt(spawn[1].getBlockX() + 1, spawn[1].getBlockY() + 1, spawn[1].getBlockZ()).setType(Material.BARRIER);
        world.getBlockAt(spawn[1].getBlockX() - 1, spawn[1].getBlockY() + 1, spawn[1].getBlockZ()).setType(Material.BARRIER);
        world.getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY() + 1, spawn[1].getBlockZ() + 1).setType(Material.BARRIER);
        world.getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY() + 1, spawn[1].getBlockZ() - 1).setType(Material.BARRIER);
        world.getBlockAt(spawn[1].getBlockX() + 1, spawn[1].getBlockY(), spawn[1].getBlockZ()).setType(Material.BARRIER);
        world.getBlockAt(spawn[1].getBlockX() - 1, spawn[1].getBlockY(), spawn[1].getBlockZ()).setType(Material.BARRIER);
        world.getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY(), spawn[1].getBlockZ() + 1).setType(Material.BARRIER);
        world.getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY(), spawn[1].getBlockZ() - 1).setType(Material.BARRIER);

        opened = true;
    }
    @Override
    public void startGame(){
        started = true;
        World world = spawn[0].getWorld();
        world.getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY() + 2, spawn[0].getBlockZ()).setType(Material.AIR);
        world.getBlockAt(spawn[0].getBlockX() + 1, spawn[0].getBlockY() + 1, spawn[0].getBlockZ()).setType(Material.AIR);
        world.getBlockAt(spawn[0].getBlockX() - 1, spawn[0].getBlockY() + 1, spawn[0].getBlockZ()).setType(Material.AIR);
        world.getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY() + 1, spawn[0].getBlockZ() + 1).setType(Material.AIR);
        world.getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY() + 1, spawn[0].getBlockZ() - 1).setType(Material.AIR);
        world.getBlockAt(spawn[0].getBlockX() + 1, spawn[0].getBlockY(), spawn[0].getBlockZ()).setType(Material.AIR);
        world.getBlockAt(spawn[0].getBlockX() - 1, spawn[0].getBlockY(), spawn[0].getBlockZ()).setType(Material.AIR);
        world.getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY(), spawn[0].getBlockZ() + 1).setType(Material.AIR);
        world.getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY(), spawn[0].getBlockZ() - 1).setType(Material.AIR);

        world.getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY() + 2, spawn[1].getBlockZ()).setType(Material.AIR);
        world.getBlockAt(spawn[1].getBlockX() + 1, spawn[1].getBlockY() + 1, spawn[1].getBlockZ()).setType(Material.AIR);
        world.getBlockAt(spawn[1].getBlockX() - 1, spawn[1].getBlockY() + 1, spawn[1].getBlockZ()).setType(Material.AIR);
        world.getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY() + 1, spawn[1].getBlockZ() + 1).setType(Material.AIR);
        world.getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY() + 1, spawn[1].getBlockZ() - 1).setType(Material.AIR);
        world.getBlockAt(spawn[1].getBlockX() + 1, spawn[1].getBlockY(), spawn[1].getBlockZ()).setType(Material.AIR);
        world.getBlockAt(spawn[1].getBlockX() - 1, spawn[1].getBlockY(), spawn[1].getBlockZ()).setType(Material.AIR);
        world.getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY(), spawn[1].getBlockZ() + 1).setType(Material.AIR);
        world.getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY(), spawn[1].getBlockZ() - 1).setType(Material.AIR);
    }
    public void openShop(Player player){
        plugin.shops.get("ctf").open(player);
    }
    @Override
    public void endGame(){
        points = new int[]{0, 0};
        opened = false;
        started = false;
        team_count_1 = 0;
        team_count_2 = 0;
        for(UUID playerUUID: playerData.keySet()){
            plugin.manager.gotoLobby(playerUUID);
        }
        playerData.clear();
        kits.clear();
        load();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event){
        Action action = event.getAction();
        Player player = event.getPlayer();
        CTFPlayerData data = getPlayerData(player.getUniqueId());
        if(player.getItemInHand().getType().equals(Material.NETHER_STAR)){
            openShop(player);
        }
        if(action.equals(Action.RIGHT_CLICK_BLOCK)){
            Block block = player.getTargetBlock((Set<Material>) null, 3);
            if(block != null && block.getType() == Material.STANDING_BANNER){
                 if(block.getData() == (byte) 8){
                    if(data.capturedFlag){
                        if(data.team == 0){
                            data.gold += 10;
                            ItemStack helm = new ItemStack(Material.LEATHER_HELMET);

                            LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
                            helmLeatherMeta.setColor(Color.RED);
                            helm.setItemMeta(helmLeatherMeta);

                            player.getInventory().setHelmet(helm);

                            points[0] += 1;
                            data.capturedFlag = false;

                            if(points[0] == 2) {
                                for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                                    if(playerData.containsKey(onlinePlayer.getUniqueId())){
                                        TitleAPI.sendTitle(onlinePlayer, 10, 40, 10, "Team 1 has won the game", null);
                                    }
                                }
                                endGame();
                            }else{
                                for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                                    if(playerData.containsKey(onlinePlayer.getUniqueId())){
                                        onlinePlayer.sendMessage("Team 1 has captured a flag");
                                    }
                                }
                            }
                        }
                    }else{
                        if(data.team == 1) {

                            if(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            }

                            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 0));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0));

                            data.gold += 10;
                            data.capturedFlag = true;
                            for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                                if(playerData.containsKey(onlinePlayer.getUniqueId())){
                                    onlinePlayer.sendMessage("Team 2 has picked up the flag");
                                }
                            }

                            ItemStack banner = new ItemStack(Material.BANNER, 1, (byte) 1);

                            player.getInventory().setHelmet(banner);
                        }
                    }
                 }else {

                     if(data.capturedFlag) {
                         if(data.team == 1) {
                             data.gold += 10;

                             ItemStack helm = new ItemStack(Material.LEATHER_HELMET);

                             LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
                             helmLeatherMeta.setColor(Color.BLUE);
                             helm.setItemMeta(helmLeatherMeta);

                             player.getInventory().setHelmet(helm);

                             points[1] += 1;
                             data.capturedFlag = false;

                             if(points[1] == 2) {
                                 for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                                     if(playerData.containsKey(onlinePlayer.getUniqueId())){
                                         TitleAPI.sendTitle(onlinePlayer, 10, 40, 10, "Team 2 has won the game", null);
                                     }
                                 }
                                 endGame();
                             }else{
                                 for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                                     if(playerData.containsKey(onlinePlayer.getUniqueId())){
                                         onlinePlayer.sendMessage("Team 2 has captured a flag");
                                     }
                                 }
                             }
                         }

                     }else {
                         if(data.team == 0) {
                             if(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                 player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                             }

                             player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 0));
                             player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0));

                             data.gold += 10;
                             data.capturedFlag = true;
                             for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                                 if(playerData.containsKey(onlinePlayer.getUniqueId())){
                                     onlinePlayer.sendMessage("Team 1 has picked up a flag");
                                 }
                             }

                             ItemStack banner = new ItemStack(Material.BANNER, 1, (byte) 4);

                             player.getInventory().setHelmet(banner);
                         }
                     }
                 }
            }
        }
    }
    @Override
    public EntryBuilder updateBoard(EntryBuilder builder, Player player){
        if(playerData.containsKey(player.getUniqueId())){
            builder.blank();
            builder.next(ChatColor.GOLD + "Gold: " + ChatColor.YELLOW + getPlayerData(player.getUniqueId()).gold);
            builder.blank();
            builder.next(ChatColor.RED + "Team 1 Points: " + ChatColor.YELLOW + points[0]);
            builder.blank();
            builder.next(ChatColor.BLUE + "Team 2 Points: " + ChatColor.YELLOW + points[1]);
            builder.blank();
        }
        return builder;
    }

    @Override
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent event, Player player, Player cause) {
        if(playerData.containsKey(cause.getUniqueId())){
            if (getPlayerData(player.getUniqueId()).team == getPlayerData(cause.getUniqueId()).team){
                event.setCancelled(true);
            } else if (getPlayerData(player.getUniqueId()).spawnProt){
                event.setCancelled(true);
            } else {
                getPlayerData(player.getUniqueId()).lastHit = cause.getUniqueId();
                if(getPlayerData(cause.getUniqueId()).spawnProt){
                    getPlayerData(cause.getUniqueId()).spawnProt = false;
                }
            }
        }
    }

    @Override
    public void onPlayerDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            final Player player = (Player) event.getEntity();
            CTFPlayerData data = getPlayerData(player.getUniqueId());
            if(event.getDamage() >= player.getHealth() || event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)){
                event.setCancelled(true);
                if(data.lastHit != null){
                    Player lastHitPlayer = Bukkit.getPlayer(data.lastHit);
                    lastHitPlayer.playEffect(EntityEffect.FIREWORK_EXPLODE);
                    lastHitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 3));
                    lastHitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0));
                    getPlayerData(data.lastHit).gold += 10;
                }
                for(PotionEffect effect : player.getActivePotionEffects()){
                    player.removePotionEffect(effect.getType());
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 255));
                (getPlayerData(player.getUniqueId())).capturedFlag = false;
                (getPlayerData(player.getUniqueId())).spawnProt = true;
                (getPlayerData(player.getUniqueId())).lastHit = null;
                player.setGameMode(GameMode.SPECTATOR);
                for(Player onlinePlayer : Bukkit.getOnlinePlayers()){
                    if(playerData.containsKey(onlinePlayer.getUniqueId())){
                        onlinePlayer.sendMessage(player.getName() + " has died");
                    }
                }
                TitleAPI.sendTitle(player, 0, 20, 0, "", "You will be respawned in 2 second");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        TitleAPI.sendTitle(player, 0, 20, 0, "", "You will be respawned in 1 second");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                CTFPlayerData data = getPlayerData(player.getUniqueId());
                                spawn(player, data.team);
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        getPlayerData(player.getUniqueId()).spawnProt = false;
                                    }
                                }, 40);
                            }
                        }, 20);
                    }
                }, 20);
            }
        }
    }
    @Override
    public void onBlockPlace(BlockPlaceEvent event){
        final Block block = event.getBlock();
        if(Math.abs(Math.floor(block.getLocation().getX()) - Math.floor(spawn[0].getX())) <= 2 && Math.abs(Math.floor(block.getLocation().getZ()) - Math.floor(spawn[0].getZ())) <= 2 && (Math.floor(block.getLocation().getY()) <= Math.floor(spawn[0].getY()) + 2 || Math.floor(block.getLocation().getY()) <= Math.floor(spawn[1].getY()) + 2)) {
            event.getPlayer().sendMessage("You cannot place a block near the spawn point");
            event.setCancelled(true);
        }
        else if(Math.abs(Math.floor(block.getLocation().getX()) - Math.floor(spawn[1].getX())) <= 2 && Math.abs(Math.floor(block.getLocation().getZ()) - Math.floor(spawn[1].getZ())) <= 2 && (Math.floor(block.getLocation().getY()) <= Math.floor(spawn[1].getY()) + 2 || Math.floor(block.getLocation().getY()) <= Math.floor(spawn[1].getY()) + 2)) {
            event.getPlayer().sendMessage("You cannot place a block near the spawn point");
            event.setCancelled(true);
        }
        else if(buildLimit != -1 && buildLimit < Math.floor(block.getLocation().getY())){
            event.getPlayer().sendMessage("Build limit reached");
            event.setCancelled(true);
        }
    }
    @Override
    public void onBlockBreak(BlockBreakEvent event){
        final Block block = event.getBlock();
        Player player = event.getPlayer();
        final Material type = block.getType();
        final Byte data = block.getData();
        System.out.println(map.getBlock(new Vector(block.getX(), block.getY(), block.getZ())).getType());
        if(map.getBlock(new Vector(block.getX(), block.getY(), block.getZ())).getType() != Material.AIR.ordinal()) {
            block.setType(Material.AIR);
            player.sendMessage("You cannot break this block");
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    block.setType(type);
                    block.setData(data);
                }
            },1);
        }
    }

}

