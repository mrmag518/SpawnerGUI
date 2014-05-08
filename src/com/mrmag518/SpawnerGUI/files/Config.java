package com.mrmag518.spawnergui.files;

import com.mrmag518.spawnergui.Log;
import com.mrmag518.spawnergui.Main;
import com.mrmag518.spawnergui.Spawnable;

import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.World;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
    private static FileConfiguration config = null;
    private static File configFile = null;
    
    public static void init() {
        reload(); load(); reload();
    }
    
    private static void load() {
        getConfig().options().header("You can use this configuration to configure features of SpawnerGUI."
                + "\nIf you need help, visit http://dev.bukkit.org/bukkit-plugins/spawnergui/pages/configuration/");
        
        getConfig().addDefault("Settings.SneakToOpen", false);
        getConfig().addDefault("Settings.RemoveNoAccessEggs", false);
        getConfig().addDefault("Settings.ShowAccessInLore", true);
        getConfig().addDefault("Settings.ShowCostInLore", true);
        getConfig().addDefault("Settings.ShowBalanceIcon", true);
        
        getConfig().addDefault("Protection.WorldGuard", Bukkit.getPluginManager().getPlugin("WorldGuard") != null);
        
        getConfig().addDefault("PersonalSpawners.Enabled", false);
        getConfig().addDefault("PersonalSpawners.MaxProtections", 0);
        getConfig().addDefault("PersonalSpawners.ProtectWhen.SpawnerPlaced", true);
        getConfig().addDefault("PersonalSpawners.ProtectWhen.FirstOpen", false);
        getConfig().addDefault("PersonalSpawners.ProtectWhen.FirstChange", true);
        for(World world : Bukkit.getWorlds()) {
            getConfig().addDefault("PersonalSpawners.Worlds." + world.getName(), true);
        }
        getConfig().addDefault("PersonalSpawners.Database.RemoveInactivePlayers", true);
        getConfig().addDefault("PersonalSpawners.Database.RemoveSpawner", false);
        getConfig().addDefault("PersonalSpawners.Database.InactiveDays", 60);
        
        getConfig().addDefault("Economy.Enabled", false);
        if(getConfig().getConfigurationSection("Mobs") != null) {
            for(Spawnable s : Spawnable.values()) {
                getConfig().set("Economy.MobPrices." + s.getName(), getConfig().getDouble("Mobs." + s.getName()));
            }
            getConfig().set("Mobs", null);
        }
        
        if(getConfig().getConfigurationSection("MobPrices") != null) {
            for(Spawnable s : Spawnable.values()) {
                getConfig().set("Economy.MobPrices." + s.getName(), getConfig().getDouble("MobPrices." + s.getName()));
            }
            getConfig().set("MobPrices", null);
        }
        for(Spawnable e : Spawnable.values()) {
            getConfig().addDefault("Economy.MobPrices." + e.getName(), 0.0);
        }
        
        getConfig().options().copyDefaults(true);
        save();
    }
    
    public static void reload() {
        if(configFile == null) {
            configFile = new File(Main.instance.getDataFolder(), "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    
    public static FileConfiguration getConfig() {
        if(config == null) reload();
        return config;
    }
    
    public static void save() {
        if(config == null || configFile == null) {
            return;
        }
        
        try {
            config.save(configFile);
        } catch (IOException ex) {
            Log.severe("Could not save config.yml to " + configFile.getAbsolutePath());
            ex.printStackTrace();
        }
    }
}
