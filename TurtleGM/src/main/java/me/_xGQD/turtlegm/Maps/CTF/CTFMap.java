package me._xGQD.turtlegm.Maps.CTF;

import com.connorlinfoot.titleapi.TitleAPI;
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
import me._xGQD.turtlegm.scoreboard.common.EntryBuilder;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class CTFMap extends Map {

    final Color[] redblue = new Color[]{Color.RED, Color.BLUE};

    protected int team_count_1;
    protected int team_count_2;
    protected int[] points;
    protected boolean opened;
    protected boolean started;
    protected int buildLimit;
    protected Location[] spawn;

    public HashMap<UUID, CTFKit> kits;

    public CTFMap(String name, boolean load){
        super(name, load);
        map_type = "CTF";
        team_count_1 = 0;
        team_count_2 = 0;
        points = new int[]{0, 0};
        kits = new HashMap<>();
    }

    public static CTFKit loadKit(UUID uuid, ConfigurationSection config){
        if(config != null) {
            return (new CTFKit(config.getInt("sword"), config.getInt("blocks"), config.getInt("steak"), config.getInt("shop")));
        }else{
            return (new CTFKit(0, 1, 2, 8));
        }
    }

    public static void openKitEdit(Player player){
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemStack glass = new ItemStack(Material.STAINED_GLASS);
        ItemStack steak = new ItemStack(Material.COOKED_BEEF);
        ItemStack shop = new ItemStack(Material.NETHER_STAR);

        ItemMeta shopmeta = shop.getItemMeta();
        shopmeta.setDisplayName("Shop");

        player.getInventory().clear();
        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, glass);
        player.getInventory().setItem(2, steak);
        player.getInventory().setItem(8, shop);
    }

    public static void saveKitEdit(Player player) {
        int sword = 0;
        int blocks = 1;
        int steak = 2;
        int shop = 8;
        for(int i = 0; i < player.getInventory().getSize(); i++){
            if(player.getInventory().getItem(i) != null){
                if(player.getInventory().getItem(i).getType().equals(Material.IRON_SWORD)){
                    sword = i;
                }
                if(player.getInventory().getItem(i).getType().equals(Material.STAINED_GLASS)){
                    blocks = i;
                }
                if(player.getInventory().getItem(i).getType().equals(Material.COOKED_BEEF)){
                    steak = i;
                }
                if(player.getInventory().getItem(i).getType().equals(Material.NETHER_STAR)){
                    shop = i;
                }
            }
        }
        File file = new File(plugin.getDataFolder(), "/" + player.getUniqueId().toString() + ".yml");
        if(file.exists()){
            try{
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                config.createSection(map_type);
                config.set(map_type + ".sword", sword);
                config.set(map_type + ".blocks", blocks);
                config.set(map_type + ".steak", steak);
                config.set(map_type + ".shop", shop);
                config.save(file);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            try{
                YamlConfiguration config = new YamlConfiguration();
                config.createSection(map_type);
                config.set(map_type + ".sword", sword);
                config.set(map_type + ".blocks", blocks);
                config.set(map_type + ".steak", steak);
                config.set(map_type + ".shop", shop);
                config.save(file);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void save(Player player){
        try {
            File maps_dir = new File(plugin.getDataFolder(), "/maps");
            if (!maps_dir.exists())
                maps_dir.mkdirs();

            File map_file = new File(plugin.getDataFolder(), "/maps/" + name + ".yml");

            File schematics_dir = new File(plugin.getDataFolder(), "/schematics");
            if (!schematics_dir.exists())
                schematics_dir.mkdirs();

            File schematic_file = new File(plugin.getDataFolder(), "/schematics/" + name + ".schematic");

            YamlConfiguration config = new YamlConfiguration();

            config.set("paste_location.world", plugin.wep.getSelection(player).getMinimumPoint().getWorld().getName());
            config.set("paste_location.x", plugin.wep.getSelection(player).getMinimumPoint().getX());
            config.set("paste_location.y", plugin.wep.getSelection(player).getMinimumPoint().getY());
            config.set("paste_location.z", plugin.wep.getSelection(player).getMinimumPoint().getZ());

            config.set("loc1.x", spawn[0].getX());
            config.set("loc1.y", spawn[0].getY());
            config.set("loc1.z", spawn[0].getZ());

            config.set("loc2.x", spawn[1].getX());
            config.set("loc2.y", spawn[1].getY());
            config.set("loc2.z", spawn[1].getZ());

            buildLimit = (int) plugin.wep.getSelection(player).getMaximumPoint().getY() - 2;

            config.set("buildLimit", buildLimit);

            config.set("type", map_type);

            config.save(map_file);

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

        } catch (IOException | WorldEditException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load(){
        try {
            spawn = new Location[]{null, null};
            File maps_dir = new File(plugin.getDataFolder(), "/maps");
            if (!maps_dir.exists())
                maps_dir.mkdirs();

            File map_file = new File(plugin.getDataFolder(), "/maps/" + name + ".yml");

            File schematics_dir = new File(plugin.getDataFolder(), "/schematics");
            if (!schematics_dir.exists())
                schematics_dir.mkdirs();

            File schematic_file = new File(plugin.getDataFolder(), "/schematics/" + name + ".schematic");

            YamlConfiguration config = new YamlConfiguration();

            config.load(map_file);
            World bukkitWorld = Bukkit.getWorld(config.getString("paste_location.world"));
            com.sk89q.worldedit.world.World worldEditWorld = BukkitUtil.getLocalWorld(bukkitWorld);
            Location paste_loc = new org.bukkit.Location(bukkitWorld,
                    config.getDouble("paste_location.x"),
                    config.getDouble("paste_location.y"),
                    config.getDouble("paste_location.z"));

            spawn[0] = new Location(bukkitWorld,
                    config.getDouble("loc1.x"),
                    config.getDouble("loc1.y"),
                    config.getDouble("loc1.z"));

            spawn[1] = new Location(bukkitWorld,
                    config.getDouble("loc2.x"),
                    config.getDouble("loc2.y"),
                    config.getDouble("loc2.z"));

            buildLimit = config.getInt("buildLimit");
            ClipboardFormat format = ClipboardFormat.findByFile(schematic_file);
            ClipboardReader reader = format.getReader(new FileInputStream(schematic_file));
            Clipboard clipboard = reader.read(worldEditWorld.getWorldData());
            EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(worldEditWorld, -1);
            System.out.println(clipboard.getDimensions().getX());
            System.out.println(clipboard.getDimensions().getY());
            System.out.println(clipboard.getDimensions().getZ());
            Operation operation = new ClipboardHolder(clipboard, worldEditWorld.getWorldData())
                    .createPaste(editSession, worldEditWorld.getWorldData())
                    .to(new Vector(paste_loc.getBlockX(), paste_loc.getBlockY(), paste_loc.getBlockZ()))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);


        } catch (IOException | InvalidConfigurationException | WorldEditException e) {
            e.printStackTrace();
        }
    }

    public void spawn(Player player, int team){

        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.teleport(Utilities.lookAt(spawn[team], spawn[(team + 1) % 2]));
        ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        ItemStack glass = new ItemStack(Material.STAINED_GLASS, 64);
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

        player.getInventory().clear();
        player.getInventory().setHelmet(helm);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        player.getInventory().setItem(kits.get(player.getUniqueId()).sword, sword);
        player.getInventory().setItem(kits.get(player.getUniqueId()).blocks, glass);
        player.getInventory().setItem(kits.get(player.getUniqueId()).steak, steak);
        player.getInventory().setItem(kits.get(player.getUniqueId()).shop, shop);

        readBuffs(player);
    }
    public void readBuffs(Player player){
        CTFPlayerData data = (CTFPlayerData) playerData.get(player.getUniqueId());
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
                case "haste":
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 9999999, 2));
                    break;
            }
        }
    }
    @Override
    public void onJoin(Player player){
        if(opened){
            File file = new File(plugin.getDataFolder(), "/" + player.getUniqueId().toString() + ".yml");
            if(file.exists() && YamlConfiguration.loadConfiguration(file).getConfigurationSection(map_type) != null){
                System.out.println(YamlConfiguration.loadConfiguration(file).getConfigurationSection(map_type).toString());
                kits.put(player.getUniqueId(), loadKit(player.getUniqueId(), YamlConfiguration.loadConfiguration(file).getConfigurationSection(map_type)));
            }else{
                kits.put(player.getUniqueId(), loadKit(player.getUniqueId(), null));
            }
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

    @Override
    public void changeSettings(String[] args, CommandSender commandSender){
        if(!(commandSender instanceof Player)){
            return;
        }
        Player player = (Player) commandSender;
        if(args[2].equals("setspawn")){
            if(spawn == null){
                spawn = new Location[]{null, null};
            }
            if(args[3].equals("1")){
                spawn[0] = player.getLocation();
            }else {
                spawn[1] = player.getLocation();
            }
            player.sendMessage("You have set the spawn");
        }
    }
    @Override
    public void onOpen(){
        opened = true;
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
    }
    @Override
    public void onClose(){
        opened = false;
    }
    @Override
    public void onStart(){
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
    @Override
    public void onEnd(){
        points = new int[]{0, 0};
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
        CTFPlayerData data = (CTFPlayerData) playerData.get(player.getUniqueId());
        if(player.getItemInHand().getType().equals(Material.NETHER_STAR)){
            plugin.shops.get("ctf").open(player);
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
                                onEnd();
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
                                 onEnd();
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
        builder.blank();
        builder.next(ChatColor.DARK_GREEN + "Gold: " + ChatColor.YELLOW + ((CTFPlayerData) playerData.get(player.getUniqueId())).gold);
        builder.blank();
        return builder;
    }
    @Override
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            final Player player = (Player) event.getEntity();
            CTFPlayerData data = (CTFPlayerData) playerData.get(player.getUniqueId());
            if(event instanceof EntityDamageByEntityEvent){
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                if(e.getDamager() instanceof Player){
                    Player damager = (Player) e.getDamager();
                    CTFPlayerData damagerData = (CTFPlayerData) playerData.get(damager.getUniqueId());
                    if (data.team == damagerData.team){
                        event.setCancelled(true);
                    } else if (data.spawnProt){
                        event.setCancelled(true);
                    } else {
                        data.lastHit = damager.getUniqueId();
                        playerData.put(player.getUniqueId(), data);
                    }
                }
            }
            if(event.getDamage() >= player.getHealth() || event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)){
                event.setCancelled(true);
                if(data.lastHit != null){
                    Player lastHitPlayer = Bukkit.getPlayer(data.lastHit);
                    lastHitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 3));
                    lastHitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0));
                    ((CTFPlayerData) playerData.get(data.lastHit)).gold += 10;
                }
                for(PotionEffect effect : player.getActivePotionEffects()){
                    player.removePotionEffect(effect.getType());
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 255));
                ((CTFPlayerData) playerData.get(player.getUniqueId())).capturedFlag = false;
                ((CTFPlayerData) playerData.get(player.getUniqueId())).spawnProt = true;
                ((CTFPlayerData) playerData.get(player.getUniqueId())).lastHit = null;
                player.setGameMode(GameMode.SPECTATOR);
                TitleAPI.sendTitle(player, 0, 20, 0, "", "You will be respawned in 2 second");
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        TitleAPI.sendTitle(player, 0, 20, 0, "", "You will be respawned in 1 second");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                CTFPlayerData data = (CTFPlayerData) playerData.get(player.getUniqueId());
                                spawn(player, data.team);
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        ((CTFPlayerData) playerData.get(player.getUniqueId())).spawnProt = false;
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
        Block block = event.getBlock();
        if(Math.floor(block.getLocation().getX()) == Math.floor(spawn[0].getX()) && Math.floor(block.getLocation().getZ()) == Math.floor(spawn[0].getZ()) && (Math.floor(block.getLocation().getY()) <= Math.floor(spawn[0].getY()) + 2 || Math.floor(block.getLocation().getY()) <= Math.floor(spawn[1].getY()) + 2)) {
            event.getPlayer().sendMessage("You cannot place a block at the spawn point");
            event.setCancelled(true);
        }
        if(Math.floor(block.getLocation().getX()) == Math.floor(spawn[1].getX()) && Math.floor(block.getLocation().getZ()) == Math.floor(spawn[1].getZ()) && (Math.floor(block.getLocation().getY()) <= Math.floor(spawn[1].getY()) + 2 || Math.floor(block.getLocation().getY()) <= Math.floor(spawn[1].getY()) + 2)) {
            event.getPlayer().sendMessage("You cannot place a block at the spawn point");
            event.setCancelled(true);
        }
        if(buildLimit != -1){
            if(buildLimit < Math.floor(block.getLocation().getY())){
                event.getPlayer().sendMessage("Build limit reached");
                event.setCancelled(true);
            }
        }
    }
    @Override
    public void onBlockBreak(BlockBreakEvent event){
        final Block block = event.getBlock();
        Player player = event.getPlayer();
        final Material type = block.getType();
        final Byte data = block.getData();
        if(block.getType() != Material.STAINED_GLASS && block.getType() != Material.SNOW_BLOCK) {
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

