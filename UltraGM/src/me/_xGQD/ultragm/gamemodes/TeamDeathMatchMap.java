package me._xGQD.ultragm.gamemodes;

import me._xGQD.ultragm.FastBoard.FastBoard;
import me._xGQD.ultragm.Main;
import me._xGQD.ultragm.npctraits.TDMTrait;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeamDeathMatchMap extends Map{
    Location[] npc_spawn_locs;
    Color[] redblue = {Color.RED, Color.BLUE};
    public int respawnTime = 40;
    public int spawnProt = 80;
    int points[];
    int buildLimit = -1;
    HashSet<UUID> spawnProtPlayers;
    HashMap<UUID, Player> lastHit;
    HashMap<UUID, Integer> gold;
    public List<NPC> npcs = new ArrayList<>();

    public TeamDeathMatchMap(Main plugin, String name) {
        super(plugin, name);
        super.type = "TDM";
        npc_spawn_locs = new Location[]{null, null};
        spawnProtPlayers = new HashSet<>();
        lastHit = new HashMap<>();
        gold = new HashMap<>();
    }
    @Override
    public void setspawn(Player player, String num){
        if(num.equals("1")){
            spawn[0] = player.getLocation();
        }else if(num.equals("2")){
            spawn[1] = player.getLocation();
        }else if(num.equals("3")){
            npc_spawn_locs[0] = player.getLocation();
            if(npcs.size() > 0){
                npcs.get(0).despawn();
                npcs.get(0).spawn(npc_spawn_locs[0]);
            }else{
                NPC npc1 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name + "_npc_1");
                npc1.addTrait(new TDMTrait());
                npc1.spawn(npc_spawn_locs[0]);
                npcs.clear();
                npcs.add(npc1);
            }
        }else if(num.equals("4")){
            npc_spawn_locs[1] = player.getLocation();
            if(npcs.size() > 1){
                npcs.get(1).despawn();
                npcs.get(1).spawn(npc_spawn_locs[1]);
            }else{
                NPC npc1 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name + "_npc_2");
                npc1.addTrait(new TDMTrait());
                npc1.spawn(npc_spawn_locs[1]);
                npcs.clear();
                npcs.add(npc1);
            }
        }
    }
    @Override
    public void open(){
        super.open();
        World world = spawn[0].getWorld();
        plugin.getServer().getWorld(world.getName()).getBlockAt(spawn[0].getBlockX()+1, spawn[0].getBlockY()+1, spawn[0].getBlockZ()).setType(Material.BARRIER);
        plugin.getServer().getWorld(world.getName()).getBlockAt(spawn[0].getBlockX()-1, spawn[0].getBlockY()+1, spawn[0].getBlockZ()).setType(Material.BARRIER);
        plugin.getServer().getWorld(world.getName()).getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY()+1, spawn[0].getBlockZ()+1).setType(Material.BARRIER);
        plugin.getServer().getWorld(world.getName()).getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY()+1, spawn[0].getBlockZ()-1).setType(Material.BARRIER);
        plugin.getServer().getWorld(world.getName()).getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY()+3, spawn[1].getBlockZ()).setType(Material.BARRIER);

        World world2 = spawn[1].getWorld();
        plugin.getServer().getWorld(world2.getName()).getBlockAt(spawn[1].getBlockX()+1, spawn[1].getBlockY()+1, spawn[1].getBlockZ()).setType(Material.BARRIER);
        plugin.getServer().getWorld(world2.getName()).getBlockAt(spawn[1].getBlockX()-1, spawn[1].getBlockY()+1, spawn[1].getBlockZ()).setType(Material.BARRIER);
        plugin.getServer().getWorld(world2.getName()).getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY()+1, spawn[1].getBlockZ()+1).setType(Material.BARRIER);
        plugin.getServer().getWorld(world2.getName()).getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY()+1, spawn[1].getBlockZ()-1).setType(Material.BARRIER);
        plugin.getServer().getWorld(world2.getName()).getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY()+3, spawn[1].getBlockZ()).setType(Material.BARRIER);
    }
    @Override
    public void start(){
        super.start();
        points = new int[]{0, 0};
        World world = spawn[0].getWorld();
        plugin.getServer().getWorld(world.getName()).getBlockAt(spawn[0].getBlockX()+1, spawn[0].getBlockY()+1, spawn[0].getBlockZ()).setType(Material.AIR);
        plugin.getServer().getWorld(world.getName()).getBlockAt(spawn[0].getBlockX()-1, spawn[0].getBlockY()+1, spawn[0].getBlockZ()).setType(Material.AIR);
        plugin.getServer().getWorld(world.getName()).getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY()+1, spawn[0].getBlockZ()+1).setType(Material.AIR);
        plugin.getServer().getWorld(world.getName()).getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY()+1, spawn[0].getBlockZ()-1).setType(Material.AIR);
        plugin.getServer().getWorld(world.getName()).getBlockAt(spawn[0].getBlockX(), spawn[0].getBlockY()+3, spawn[1].getBlockZ()).setType(Material.AIR);

        World world2 = spawn[1].getWorld();
        plugin.getServer().getWorld(world2.getName()).getBlockAt(spawn[1].getBlockX()+1, spawn[1].getBlockY()+1, spawn[1].getBlockZ()).setType(Material.AIR);
        plugin.getServer().getWorld(world2.getName()).getBlockAt(spawn[1].getBlockX()-1, spawn[1].getBlockY()+1, spawn[1].getBlockZ()).setType(Material.AIR);
        plugin.getServer().getWorld(world2.getName()).getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY()+1, spawn[1].getBlockZ()+1).setType(Material.AIR);
        plugin.getServer().getWorld(world2.getName()).getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY()+1, spawn[1].getBlockZ()-1).setType(Material.AIR);
        plugin.getServer().getWorld(world2.getName()).getBlockAt(spawn[1].getBlockX(), spawn[1].getBlockY()+3, spawn[1].getBlockZ()).setType(Material.AIR);
    }
    @Override
    public void spawn(Player player, int team){
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.teleport(spawn[team]);
        ItemStack helm = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.GOLD_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        ItemStack glass = new ItemStack(Material.STAINED_GLASS, 64);
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemStack steak = new ItemStack(Material.COOKED_BEEF, 64);


        LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
        helmLeatherMeta.setColor(redblue[team]);
        helm.setItemMeta(helmLeatherMeta);

        LeatherArmorMeta bootsLeatherMeta = (LeatherArmorMeta)boots.getItemMeta();
        bootsLeatherMeta.setColor(redblue[team]);
        boots.setItemMeta(bootsLeatherMeta);

        player.getInventory().clear();
        player.getInventory().setHelmet(helm);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, glass);
        player.getInventory().setItem(2, steak);
    }


    @Override
    public void death(Player player, int team){
        for(PotionEffect effect: player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 9999999, 255));
        player.setGameMode(GameMode.SPECTATOR);
        spawnProtPlayers.add(player.getUniqueId());
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                spawnProtPlayers.remove(player.getUniqueId());
            }
        }, spawnProt);
    }
    @Override
    public void join(Player player){
        gold.put(player.getUniqueId(), 0);
        super.join(player);
    }
    @Override
    public boolean buy(Player player, ItemStack item){
        boolean close = false;
        if(Material.DIAMOND_SWORD.equals(item.getType())){
            close = true;
            if(gold.get(player.getUniqueId()) >= 25) {
                player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
                gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) - 25);
            }
        }
        if(Material.DIAMOND_HELMET.equals(item.getType())){
            close = true;
            if(gold.get(player.getUniqueId()) >= 25) {
                player.getInventory().setBoots(new ItemStack(Material.DIAMOND_HELMET));
                gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) - 25);
            }
        }
        if(Material.DIAMOND_BOOTS.equals(item.getType())){
            close = true;
            if(gold.get(player.getUniqueId()) >= 25) {
                player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) - 25);
            }
        }
        if(Material.IRON_CHESTPLATE.equals(item.getType())){
            close = true;
            if(gold.get(player.getUniqueId()) >= 50) {

                ItemStack helm = player.getInventory().getHelmet();
                ItemStack chestplate = player.getInventory().getChestplate();
                ItemStack leggings = player.getInventory().getLeggings();
                ItemStack boots = player.getInventory().getBoots();

                helm.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

                player.getInventory().setHelmet(helm);
                player.getInventory().setChestplate(chestplate);
                player.getInventory().setLeggings(leggings);
                player.getInventory().setBoots(boots);

                gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) - 50);
            }
        }
        if(Material.FISHING_ROD.equals(item.getType())){
            close = true;
            if(gold.get(player.getUniqueId()) >= 30) {
                player.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
                gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) - 30);
            }
        }
        if(Material.STAINED_GLASS.equals(item.getType())){
            close = true;
            if(gold.get(player.getUniqueId()) >= 10) {
                player.getInventory().addItem(new ItemStack(Material.STAINED_GLASS, 10));
                gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) - 10);
            }
        }
        return close;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Material type = block.getType();
        Byte data = block.getData();
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

    @Override
    public void onBlockPlace(BlockPlaceEvent event){
        Block block = event.getBlock();
        if(Math.floor(block.getLocation().getX()) == Math.floor(spawn[0].getX()) && Math.floor(block.getLocation().getZ()) == Math.floor(spawn[0].getZ()) && Math.floor(block.getLocation().getY()) >= Math.floor(spawn[0].getY())) {
            event.getPlayer().sendMessage("You cannot place a block at the spawn point");
            event.setCancelled(true);
        }
        if(Math.floor(block.getLocation().getX()) == Math.floor(spawn[1].getX()) && Math.floor(block.getLocation().getZ()) == Math.floor(spawn[1].getZ()) && Math.floor(block.getLocation().getY()) >= Math.floor(spawn[1].getY())) {
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
    public void boardChange(Player player, FastBoard board){
        board.updateLines(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "");
        board.updateLine(3, "Gold: " + gold.get(player.getUniqueId()));
        board.updateLine(5, "Team 1 Kills: " + points[0]);
        board.updateLine(6, "Team 2 Kills: " + points[1]);
    }

    public void killPerks(Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0));
        gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) + 10);
        points[teams.get(player.getUniqueId())] += 1;
        if(points[teams.get(player.getUniqueId())] == 10){
            Bukkit.getServer().broadcastMessage("Team " + (teams.get(player.getUniqueId())+1) + " has won the game.");
            end();
        }
    }

    @Override
    public void onHit(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            int team = teams.get(player.getUniqueId());
            if(event instanceof EntityDamageByEntityEvent){
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                if(e.getDamager() instanceof Player){
                    Player damager = (Player) e.getDamager();
                    if(teams.get(damager.getUniqueId()).equals(teams.get(player.getUniqueId()))){
                        event.setCancelled(true);
                    }
                    else if(spawnProtPlayers.contains(player.getUniqueId())) {
                        event.setCancelled(true);
                    }else{
                        lastHit.put(player.getUniqueId(), damager);
                    }
                }
            }
            if(event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                event.setCancelled(true);
                if(lastHit.containsKey(player.getUniqueId())){
                    killPerks(lastHit.get(player.getUniqueId()));
                }
                death(player, team);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        spawn(player, team);
                    }
                }, respawnTime);

            }
            if(event.getDamage() >= player.getHealth()){
                event.setCancelled(true);
                if(lastHit.containsKey(player.getUniqueId())){
                    killPerks(lastHit.get(player.getUniqueId()));
                }
                death(player, team);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        spawn(player, team);
                    }
                }, respawnTime);
            }
        }
    }

    @Override
    public void save(Location pos_1, Location pos_2, Location[] newspawns) {
        File file = new File(plugin.getDataFolder(), name + ".yml");
        if(!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        YamlConfiguration config = new YamlConfiguration();
        try{
            config.load(file);
            config.set("World", pos_1.getWorld().getName());
            config.set("Type", type);
            int x1 = pos_1.getBlockX();
            int y1 = pos_1.getBlockY();
            int z1 = pos_1.getBlockZ();
            int x2 = pos_2.getBlockX();
            int y2 = pos_2.getBlockY();
            int z2 = pos_2.getBlockZ();
            int xl = x1;
            int xh = x2;
            int yl = y1;
            int yh = y2;
            int zl = z1;
            int zh = z2;
            if(x1 > x2){
                xl = x2;
                xh = x1;
            }
            if(y1 > y2){
                yl = y2;
                yh = y1;
            }
            if(z1 > z2){
                zl = z2;
                zh = z1;
            }

            ConfigurationSection spawn_1_sec = config.createSection("spawn_1");
            spawn_1_sec.set("x", newspawns[0].getX());
            spawn_1_sec.set("y", newspawns[0].getY());
            spawn_1_sec.set("z", newspawns[0].getZ());

            ConfigurationSection spawn_2_sec = config.createSection("spawn_2");
            spawn_2_sec.set("x", newspawns[1].getX());
            spawn_2_sec.set("y", newspawns[1].getY());
            spawn_2_sec.set("z", newspawns[1].getZ());

            ConfigurationSection map = config.createSection("Map");

            int counter = 0;
            for(int x = xl; x <= xh; x++){
                for(int y = yl; y <= yh; y++){
                    for(int z = zl; z <= zh; z++){
                        ConfigurationSection section = map.createSection(String.valueOf(counter));
                        section.set("x", x);
                        section.set("y", y);
                        section.set("z", z);
                        section.set("type", pos_1.getWorld().getBlockAt(x, y, z).getType().toString());
                        section.set("data", String.valueOf(pos_1.getWorld().getBlockAt(x, y, z).getData()));
                        counter++;
                    }
                }
            }
            ConfigurationSection npc_spawn_1_sec = config.createSection("npc_spawn_1");
            npc_spawn_1_sec.set("x", npc_spawn_locs[0].getX());
            npc_spawn_1_sec.set("y", npc_spawn_locs[0].getY());
            npc_spawn_1_sec.set("z", npc_spawn_locs[0].getZ());
            npc_spawn_1_sec.set("World", npc_spawn_locs[0].getWorld().getName());

            ConfigurationSection npc_spawn_2_sec = config.createSection("npc_spawn_2");
            npc_spawn_2_sec.set("x", npc_spawn_locs[1].getX());
            npc_spawn_2_sec.set("y", npc_spawn_locs[1].getY());
            npc_spawn_2_sec.set("z", npc_spawn_locs[1].getZ());
            npc_spawn_2_sec.set("World", npc_spawn_locs[1].getWorld().getName());
            config.save(file);
        }catch(IOException | InvalidConfigurationException e){
            e.printStackTrace();
        }
        if(pos_1.getY() > pos_2.getY()){
            buildLimit = pos_1.getBlockY() - 2;
        }else{
            buildLimit = pos_2.getBlockY() - 2;
        }

    }
    @Override
    public void load(FileConfiguration config){
        String world_name = config.getString("World");
        plugin.getServer().getConsoleSender().sendMessage(world_name);
        ConfigurationSection spawn_1_sec = config.getConfigurationSection("spawn_1");
        ConfigurationSection spawn_2_sec = config.getConfigurationSection("spawn_2");
        ConfigurationSection npc_spawn_1_sec = config.getConfigurationSection("npc_spawn_1");
        ConfigurationSection npc_spawn_2_sec = config.getConfigurationSection("npc_spawn_2");
        World world = plugin.getServer().getWorld(world_name);
        spawn[0] = new Location(world, spawn_1_sec.getDouble("x"), spawn_1_sec.getDouble("y"), spawn_1_sec.getDouble("z"));
        spawn[1] = new Location(world, spawn_2_sec.getDouble("x"), spawn_2_sec.getDouble("y"), spawn_2_sec.getDouble("z"));
        npc_spawn_locs[0] = new Location(world, npc_spawn_1_sec.getDouble("x"), npc_spawn_1_sec.getDouble("y"), npc_spawn_1_sec.getDouble("z"));
        npc_spawn_locs[1] = new Location(world, npc_spawn_2_sec.getDouble("x"), npc_spawn_2_sec.getDouble("y"), npc_spawn_2_sec.getDouble("z"));
        if(npcs.size() == 2){
            npcs.get(0).despawn();
            npcs.get(0).spawn(npc_spawn_locs[0]);
            npcs.get(1).despawn();
            npcs.get(1).spawn(npc_spawn_locs[1]);
        }else{
            for(NPC npc : npcs){
                npc.destroy();
            }
            NPC npc1 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name + "_npc_1");
            npc1.addTrait(new TDMTrait());
            NPC npc2 = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name + "_npc_2");
            npc2.addTrait(new TDMTrait());
            npc1.spawn(npc_spawn_locs[0]);
            npc2.spawn(npc_spawn_locs[1]);
            npcs.clear();
            npcs.add(npc1);
            npcs.add(npc2);
        }
        ConfigurationSection map = config.getConfigurationSection("Map");
        for (String key : map.getKeys(false)){
            ConfigurationSection block = map.getConfigurationSection(key);
            int x = block.getInt("x");
            int y = block.getInt("y");
            if(y - 2 > buildLimit){
                buildLimit = y - 2;
            }
            int z = block.getInt("z");
            Block bl = world.getBlockAt(x, y, z);
            bl.setType(Material.getMaterial(block.getString("type")));
            bl.setData((byte) Integer.parseInt(block.getString("data")));
        }
    }

    @Override
    public void end(){
        super.end();
        spawnProtPlayers.clear();
        lastHit.clear();
        points[0] = 0;
        points[1] = 0;
    }

}
