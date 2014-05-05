package com.mrmag518.spawnergui;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Database {
    public static FileConfiguration database = null;
    public static File databaseFile = null;
    
    public static void init() {
        reload(); load(); reload();
    }
    
    public static void load() {
        getDB().options().copyDefaults(true);
        save();
    }
    
    public static void reload() {
        if(databaseFile == null) {
            databaseFile = new File("plugins/SpawnerGUI/db.yml");
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
            System.err.println("Could not save streamDB.yml to " + databaseFile.getAbsolutePath());
            ex.printStackTrace();
        }
    }
    
    public static void addSpawner(Location loc, UUID owner) {
        //database.set("Spawners." + loc.getWorld().getName() + ".", loc);
    }
    
    public static void removeSpawner(Location loc) {
        
    }
    
    public static void isProtected(Location loc) {
        
    }
    
    public static void canOpen(UUID player) {
        
    }
    
    public static void cleanup() {
        
    }
}
