package me._xGQD.ultragm.gamemodes;

import me._xGQD.ultragm.FastBoard.FastBoard;
import me._xGQD.ultragm.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CapTheFlagMap extends Map {
    public int respawnTime = 0;
    public int spawnProt = 0;
    int points[];
    int buildLimit = -1;
    HashSet<UUID> spawnProtPlayers = new HashSet<>();
    HashMap<UUID, Player> lastHit = new HashMap<>();
    HashMap<UUID, List<String>> buffs = new HashMap<>();
    HashMap<UUID, Integer> gold = new HashMap<>();
    Color[] redblue;
    public CapTheFlagMap(Main plugin, String name) {
        super(plugin, name);
        super.type = "CTF";
        spawnProt = 40;
        points = new int[]{0, 0};
        redblue = new Color[]{Color.RED, Color.BLUE};
    }

    @Override
    public void spawn(Player player, int team){
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.teleport(spawn[team]);
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
        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, glass);
        player.getInventory().setItem(2, steak);
        player.getInventory().setItem(8, shop);

        readBuffs(player);
    }
    @Override
    public void death(Player player, int team){
        player.setGameMode(GameMode.SPECTATOR);
        spawnProtPlayers.add(player.getUniqueId());
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                spawnProtPlayers.remove(player.getUniqueId());
            }
        }, spawnProt);
    }
    public void join(Player player){
        buffs.put(player.getUniqueId(), new ArrayList<>());
        gold.put(player.getUniqueId(), 0);
        super.join(player);
    }
    public void readBuffs(Player player){
        List<String> playerbuffs = buffs.get(player.getUniqueId());
        for(String buff: playerbuffs){
            if(buff.equals("permarmor")){
                ItemStack chest = new ItemStack(Material.GOLD_CHESTPLATE);
                ItemStack leggings = new ItemStack(Material.GOLD_LEGGINGS);
                ItemStack boots = new ItemStack(Material.GOLD_BOOTS);
                chest.addEnchantment(Enchantment.DURABILITY, 3);
                leggings.addEnchantment(Enchantment.DURABILITY, 3);
                boots.addEnchantment(Enchantment.DURABILITY, 3);
                player.getInventory().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
                player.getInventory().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
                player.getInventory().setBoots(new ItemStack(Material.GOLD_BOOTS));
            }
            if(buff.equals("permsword")){
                ItemStack sword = new ItemStack(Material.GOLD_SWORD);
                sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);
                sword.addEnchantment(Enchantment.DURABILITY, 3);
                player.getInventory().addItem(sword);
            }
            if(buff.equals("permhaste")){
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 9999999, 2));
            }
        }
    }
    @Override
    public boolean buy(Player player, ItemStack item){
        boolean close = false;
        if(Material.GOLD_CHESTPLATE.equals(item.getType()) && gold.get(player.getUniqueId()) >= 100){
            close = true;
            List<String> playerbuffs = buffs.get(player.getUniqueId());
            playerbuffs.add("permarmor");
            buffs.put(player.getUniqueId(), playerbuffs);
            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) - 100);
        }
        if(Material.GOLD_SWORD.equals(item.getType()) && gold.get(player.getUniqueId()) >= 50){
            close = true;
            List<String> playerbuffs = buffs.get(player.getUniqueId());
            playerbuffs.add("permsword");
            buffs.put(player.getUniqueId(), playerbuffs);
            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) - 50);
        }
        if(Material.IRON_CHESTPLATE.equals(item.getType()) && gold.get(player.getUniqueId()) >= 50){
            close = true;
            player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) - 50);
        }
        if(Material.DIAMOND_SWORD.equals(item.getType()) && gold.get(player.getUniqueId()) >= 25){
            close = true;
            player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) - 25);
        }
        if(Material.GOLD_PICKAXE.equals(item.getType()) && gold.get(player.getUniqueId()) >= 50){
            close = true;
            List<String> playerbuffs = buffs.get(player.getUniqueId());
            playerbuffs.add("permhaste");
            buffs.put(player.getUniqueId(), playerbuffs);
            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) - 50);
        }
        if(Material.FISHING_ROD.equals(item.getType()) && gold.get(player.getUniqueId()) >= 30){
            close = true;
            player.getInventory().addItem(new ItemStack(Material.FISHING_ROD));
            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) - 30);
        }
        if(Material.SNOW_BLOCK.equals(item.getType()) && gold.get(player.getUniqueId()) >= 20){
            close = true;
            player.getInventory().addItem(new ItemStack(Material.SNOW_BLOCK, 4));
            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) - 20);
        }
        readBuffs(player);
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
            new BukkitRunnable() {
                @Override
                public void run() {
                    block.setType(type);
                    block.setData(data);
                }
            }.runTaskLater(plugin, 1);
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
        board.updateLine(5, "Team 1: " + points[0]);
        board.updateLine(6, "Team 2: " + points[1]);
    }
    public void killPerks(Player player){
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0));
        gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) + 10);
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
                    if(teams.get(damager.getUniqueId()) == teams.get(player.getUniqueId())){
                        event.setCancelled(true);
                    }
                    else if(spawnProtPlayers.contains(player)) {
                        event.setCancelled(true);
                    }else{
                        lastHit.put(player.getUniqueId(), damager);
                    }
                }
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
    public void save(Location pos_1, Location pos_2, Location[] newspawns){
        plugin.getServer().getConsoleSender().sendMessage("Creating a map");
        super.save(pos_1, pos_2, newspawns);
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
        World world = plugin.getServer().getWorld(world_name);
        spawn[0] = new Location(world, spawn_1_sec.getDouble("x"), spawn_1_sec.getDouble("y"), spawn_1_sec.getDouble("z"));
        spawn[1] = new Location(world, spawn_2_sec.getDouble("x"), spawn_2_sec.getDouble("y"), spawn_2_sec.getDouble("z"));
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
    public void onPlayerInteract(PlayerInteractEvent event){
        Action eventAction = event.getAction();
        Player player = event.getPlayer();
        if((eventAction == Action.RIGHT_CLICK_BLOCK || eventAction == Action.RIGHT_CLICK_AIR)) {
            Block block = event.getPlayer().getTargetBlock((Set<Material>) null, 3);
            if (block != null && block.getType() == Material.STANDING_BANNER) {
                if(block.getData() == (byte) 8) {
                    if(player.getInventory().getHelmet().getType() == Material.BANNER) {
                        if(teams.get(player.getUniqueId()) == 0) {

                            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) + 10);

                            ItemStack helm = new ItemStack(Material.LEATHER_HELMET);

                            LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
                            helmLeatherMeta.setColor(Color.RED);
                            helm.setItemMeta(helmLeatherMeta);

                            player.getInventory().setHelmet(helm);

                            points[0] += 1;

                            Bukkit.broadcastMessage("Team 1 has captured a flag");

                            if(points[0] == 2) {
                                Bukkit.broadcastMessage("Team 1 has won");

                                end();
                            }
                        }
                    }else {
                        if(teams.get(player.getUniqueId()) == 1) {
                            if(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            }
                            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 1));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0));
                            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) + 10);

                            Bukkit.broadcastMessage("Team 2 has picked up the flag");

                            ItemStack banner = new ItemStack(Material.BANNER, 1, (byte) 1);

                            player.getInventory().setHelmet(banner);
                        }

                    }
                }else {
                    if(player.getInventory().getHelmet().getType() == Material.BANNER) {
                        if(teams.get(player.getUniqueId()) == 1) {

                            ItemStack helm = new ItemStack(Material.LEATHER_HELMET);

                            LeatherArmorMeta helmLeatherMeta = (LeatherArmorMeta)helm.getItemMeta();
                            helmLeatherMeta.setColor(Color.BLUE);
                            helm.setItemMeta(helmLeatherMeta);

                            player.getInventory().setHelmet(helm);

                            points[1] += 1;

                            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) + 10);

                            Bukkit.broadcastMessage("Team 2 has captured a flag");

                            if(points[1] == 2) {
                                Bukkit.broadcastMessage("Team 2 has won");

                                end();
                            }
                        }

                    }else {
                        if(teams.get(player.getUniqueId()) == 0) {
                            if(player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            }
                            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 1));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0));
                            gold.put(player.getUniqueId(), gold.get(player.getUniqueId()) + 10);

                            Bukkit.broadcastMessage("Team 1 has picked up the flag");

                            ItemStack banner = new ItemStack(Material.BANNER, 1, (byte) 4);

                            player.getInventory().setHelmet(banner);
                        }
                    }
                }
            }
        }
    }
    @Override
    public void end(){
        super.end();
        spawnProtPlayers.clear();
        lastHit.clear();
        buffs.clear();
        gold.clear();
    }
}
