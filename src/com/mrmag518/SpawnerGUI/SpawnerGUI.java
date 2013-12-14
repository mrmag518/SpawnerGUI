package com.mrmag518.SpawnerGUI;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnerGUI extends JavaPlugin {
    private Economy eco = null;
    public static final Set<String> openGUIs = new HashSet<String>();
    
    @Override
    public void onDisable() {
        for(String s : openGUIs) {
            if(Bukkit.getOfflinePlayer(s).isOnline()) {
                Bukkit.getPlayerExact(s).getOpenInventory().close();
            }
        }
        Logger.getLogger("Minecraft").log(Level.INFO, "[SpawnerGUI] Version {0} disabled.", getDescription().getVersion());
    }
    
    @Override
    public void onEnable() {
        loadConfig();
        
        if(getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if(rsp != null) {
                eco = rsp.getProvider();
            } else {
                Logger.getLogger("Minecraft").log(Level.WARNING, "[SpawnerGUI] Found no Vault supported economy plugin! Disabled economy support.");
            }
        }
        getServer().getPluginManager().registerEvents(new Handler(), this);
        Logger.getLogger("Minecraft").log(Level.INFO, "[SpawnerGUI] Version {0} enabled.", getDescription().getVersion());
    }
    
    private void loadConfig() {
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        
        getConfig().addDefault("Settings.SneakToOpen", false);
        getConfig().addDefault("Settings.RemoveNoAccessEggs", false);
        getConfig().addDefault("Settings.ShowAccessInLore", true);
        getConfig().addDefault("Settings.ShowCostInLore", true);
        getConfig().addDefault("Settings.ShowBalanceIcon", true);
        
        if(getConfig().getConfigurationSection("Mobs") != null) {
            for(Spawnable e : Spawnable.values()) {
                getConfig().set("MobPrices." + e.getName(), getConfig().getDouble("Mobs." + e.getName()));
            }
            getConfig().set("Mobs", null);
        }
        
        for(Spawnable e : Spawnable.values()) {
            getConfig().addDefault("MobPrices." + e.getName(), 0.0);
        }
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
    
    public void openGUI(CreatureSpawner spawner, Player p) {
        Spawnable type = toSpawnable(spawner.getSpawnedType());
        GUIHandler gui = new GUIHandler("Spawner Type: " + type.getName(), 36, spawner);
        int j = 0;
        
        for(Spawnable e : Spawnable.values()) {
            if(getConfig().getBoolean("Settings.RemoveNoAccessEggs") && noAccess(p, e)) continue;

            String defLore = "§7Set spawner type to: §a" + e.getName();
            String cost = (getPrice(e) > 0.0 && !p.hasPermission("spawnergui.eco.bypass." + e.getName().toLowerCase()) && !p.hasPermission("spawnergui.eco.bypass.*")) ? "§a" + getPrice(e) : "§aFree";
            String access = noAccess(p, e) ? "§7Access: §cNo" : "§7Access: §aYes";

            if(eco != null && getConfig().getBoolean("Settings.ShowCostInLore")) {
                if(getConfig().getBoolean("Settings.ShowAccessInLore")) {
                    gui.setItem(j, e.getSpawnEgg(), "§6" + e.getName(), defLore, "§7Cost: " + cost, access);
                } else {
                    gui.setItem(j, e.getSpawnEgg(), "§6" + e.getName(), defLore, "§7Cost: " + cost);
                }
            } else {
                if(getConfig().getBoolean("Settings.ShowAccessInLore")) {
                    gui.setItem(j, e.getSpawnEgg(), "§6" + e.getName(), defLore, access);
                } else {
                    gui.setItem(j, e.getSpawnEgg(), "§6" + e.getName(), defLore);
                }
            }
            j++;
        }
        
        if(getConfig().getBoolean("Settings.ShowBalanceIcon")) {
            String s = eco != null ? "§aYour Balance: §e" + Math.round(eco.getBalance(p.getName()) * 100.0) / 100.0 : "§cEconomy not enabled!";
            gui.setItem(35, new ItemStack(Material.SKULL_ITEM, 1, (byte)3), "§bBalance", s);
        }
        gui.open(p);
        openGUIs.add(p.getName());
    }
    
    public double getPrice(Spawnable type) {
        return getConfig().getDouble("MobPrices." + type.getName());
    }
    
    public boolean noAccess(Player p, Spawnable type) {
        return !p.hasPermission("spawnergui.edit.*") && !p.hasPermission("spawnergui.edit." + type.getName().toLowerCase());
    }
    
    public static Spawnable toSpawnable(EntityType type) {
        for(Spawnable e : Spawnable.values()) {
            if(e.getType() == type) {
                return e;
            }
        }
        return null;
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(cmd.getName().equalsIgnoreCase("spawnergui")) {
            if(sender instanceof Player) {
                if(sender.hasPermission("spawnergui.command.reload")) {
                    reloadConfig();
                    sender.sendMessage("§aConfiguration file reloaded.");
                } else {
                    sender.sendMessage("§cYou do not have permission to do this!");
                }
            } else {
                reloadConfig();
                sender.sendMessage("Configuration file reloaded.");
            }
            return true;
        }
        return false;
    }
    
    public class Handler implements Listener {
        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void handleInteract(PlayerInteractEvent event) {
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block b = event.getClickedBlock();
                Player p = event.getPlayer();
                
                if(b != null && b.getType() == Material.MOB_SPAWNER && p.hasPermission("spawnergui.open")) {
                    if(getConfig().getBoolean("Settings.SneakToOpen") && p.isSneaking()) {
                        event.setCancelled(true);
                        openGUI((CreatureSpawner)b.getState(), p);
                    } else if(getConfig().getBoolean("Settings.SneakToOpen") == false && !p.isSneaking()) {
                        event.setCancelled(true);
                        openGUI((CreatureSpawner)b.getState(), p);
                    }
                }
            }
        }
        
        @EventHandler
        public void handleClick(GUIClickEvent event) {
            Player p = event.getPlayer();
            CreatureSpawner spawner = event.getSpawner();
            
            if(spawner.getBlock().getType() != Material.MOB_SPAWNER) {
                p.sendMessage("§cThe spawner block is no longer valid! (§7" + spawner.getBlock().getType().name().toLowerCase() + "§c)");
                return;
            }
            String clicked = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName().toLowerCase());
            Spawnable current = toSpawnable(spawner.getSpawnedType());

            if(clicked.equalsIgnoreCase("balance")) {
                event.setWillClose(false);
            } else {
                for(Spawnable e : Spawnable.values()) {
                    if(clicked.equalsIgnoreCase(e.getName().toLowerCase())) {
                        p.playSound(p.getLocation(), Sound.CLICK, 1, 1);

                        if(!noAccess(p, e)) {
                            if(eco != null && !p.hasPermission("spawnergui.eco.bypass.*")) {
                                double cost = p.hasPermission("spawnergui.eco.bypass." + clicked) ? 0.0 : getPrice(e);

                                if(cost > 0.0) {
                                    if(eco.has(p.getName(), cost)) {
                                        p.sendMessage("§7Charged §f" + cost + " §7of your balance.");
                                        eco.withdrawPlayer(p.getName(), cost);
                                    } else {
                                        p.sendMessage("§cYou need at least §7" + cost + " §cin balance to do this!");
                                        return;
                                    }
                                }
                            }
                            spawner.setSpawnedType(e.getType());
                            spawner.update(true);
                            p.sendMessage("§9Spawner type changed from §7" + current.getName().toLowerCase() + " §9to §7" + clicked + "§9!");
                            return;
                        }
                        p.sendMessage("§cYou are not allowed to change to that type!");
                        break;
                    }
                }
            }
        }
    }
    
    public enum Spawnable {
        CREEPER(EntityType.CREEPER, "Creeper", (byte)50),
        SKELETON(EntityType.SKELETON, "Skeleton", (byte)51),
        SPIDER(EntityType.SPIDER, "Spider", (byte)52),
        GIANT(EntityType.GIANT, "Giant", (byte)53),
        ZOMBIE(EntityType.ZOMBIE, "Zombie", (byte)54),
        SLIME(EntityType.SLIME, "Slime", (byte)55),
        GHAST(EntityType.GHAST, "Ghast", (byte)56),
        PIG_ZOMBIE(EntityType.PIG_ZOMBIE, "PigZombie", (byte)57),
        ENDERMAN(EntityType.ENDERMAN, "Enderman", (byte)58),
        CAVE_SPIDER(EntityType.CAVE_SPIDER, "CaveSpider", (byte)59),
        SILVERFISH(EntityType.SILVERFISH, "Silverfish", (byte)60),
        BLAZE(EntityType.BLAZE, "Blaze", (byte)61),
        MAGMA_CUBE(EntityType.MAGMA_CUBE, "MagmaCube", (byte)62),
        ENDER_DRAGON(EntityType.ENDER_DRAGON, "EnderDragon", (byte)63),
        WITHER(EntityType.WITHER, "Wither", (byte)64),
        BAT(EntityType.BAT, "Bat", (byte)65),
        WITCH(EntityType.WITCH, "Witch", (byte)66),
        PIG(EntityType.PIG, "Pig", (byte)90),
        SHEEP(EntityType.SHEEP, "Sheep", (byte)91),
        COW(EntityType.COW, "Cow", (byte)92),
        CHICKEN(EntityType.CHICKEN, "Chicken", (byte)93),
        SQUID(EntityType.SQUID, "Squid", (byte)94),
        WOLF(EntityType.WOLF, "Wolf", (byte)95),
        MUSHROOM_COW(EntityType.MUSHROOM_COW, "Mooshroom", (byte)96),
        SNOWMAN(EntityType.SNOWMAN, "SnowGolem", (byte)97),
        OCELOT(EntityType.OCELOT, "Ocelot", (byte)98),
        IRON_GOLEM(EntityType.IRON_GOLEM, "IronGolem", (byte)99),
        HORSE(EntityType.HORSE, "Horse", (byte)100),
        VILLAGER(EntityType.VILLAGER, "Villager", (byte)120);
        
        private final EntityType type;
        private final String name;
        private final byte data;
        
        private Spawnable(EntityType type, String name, byte data) {
            this.type = type;
            this.name = name;
            this.data = data;
        }
        
        public String getName() {
            return name;
        }
        
        public byte getData() {
            return data;
        }
        
        public EntityType getType() {
            return type;
        }
        
        public ItemStack getSpawnEgg() {
            return new ItemStack(Material.MONSTER_EGG, 1, data);
        }
    }
}
