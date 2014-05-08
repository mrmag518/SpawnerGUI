package com.mrmag518.spawnergui.files;

import com.mrmag518.spawnergui.Log;
import com.mrmag518.spawnergui.Main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.metadata.FixedMetadataValue;

public class Database {
    public static FileConfiguration database = null;
    public static File databaseFile = null;
    
    public static void init() {
        reload(); load(); reload();
    }
    
    public static void load() {
        for(World world : Bukkit.getWorlds()) {
            getDB().addDefault("Spawners." + world.getName(), Arrays.asList(new String[]{}));
        }
        getDB().options().copyDefaults(true);
        save();
        
        if(Config.getConfig().getBoolean("PersonalSpawners.Enabled")) {
            loadMeta();
        }
    }
    
    public static void reload() {
        if(databaseFile == null) {
            databaseFile = new File(Main.instance.getDataFolder(), "db.yml");
        }
        database = YamlConfiguration.loadConfiguration(databaseFile);
    }
    
    public static FileConfiguration getDB() {
        if(database == null) reload();
        return database;
    }
    
    public static void save() {
        if(database == null || databaseFile == null) {
            return;
        }
        
        try {
            database.save(databaseFile);
        } catch (IOException ex) {
            Log.severe("Could not save db.yml to " + databaseFile.getAbsolutePath());
            ex.printStackTrace();
        }
    }
    
    public static void addSpawner(Block block, UUID owner) {
        block.setMetadata("ProtectedSpawner", new FixedMetadataValue(Main.instance, owner));
    }
    
    public static void removeSpawner(Block block) {
        if(block.hasMetadata("ProtectedSpawner")) {
            block.removeMetadata("ProtectedSpawner", Main.instance);
        }
    }
    
    public static boolean isProtected(Block block) {
        return block.hasMetadata("ProtectedSpawner");
    }
    
    public static boolean canOpen(UUID player, Block block) {
        if(isProtected(block)) {
            UUID owner = (UUID) block.getMetadata("ProtectedSpawner").get(0).value();
            return player.toString().equals(owner.toString());
        }
        return true;
    }
    
    public static void saveMeta() {
        
    }
    
    public static void loadMeta() {
        Log.info("Loading spawners from database ..");
        int i = 0;
        
        for(World world : Bukkit.getWorlds()) {
            List<String> list = database.getStringList("Spawners." + world.getName());
            
            if(!list.isEmpty()) {
                i += list.size();
                
                for(String s : list) {
                    Location loc = getLocation(s, world);
                    
                    if(loc.getBlock().getType() == Material.MOB_SPAWNER) {
                        
                    } else {
                        removeSpawner(loc.getBlock());
                        i--;
                    }
                }
            }
        }
        Log.info(i + " spawners loaded!");
    }
    
    public static void cleanup() {
        
    }
    
    public static UUID getOwner(Block block) {
        if(block.hasMetadata("ProtectedSpawner")) {
            return (UUID) block.getMetadata("ProtectedSpawner").get(0).value();
        }
        return null;
    }
    
    public static String getStringLoc(Location loc, UUID owner) {
        return loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + owner.toString();
    }
    
    public static Location getLocation(String s, World world) {
        String[] split = s.split(",");
        return new Location(world, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }
}
