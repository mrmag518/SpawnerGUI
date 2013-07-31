package com.mrmag518.SpawnerGUI;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
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

public class SpawnerGUI extends JavaPlugin implements Listener {
    public boolean ecoEnabled = false;
    public Economy eco;
    
    @Override
    public void onDisable() {
        Logger.getLogger("Minecraft").log(Level.INFO, "[SpawnerGUI] Version {0} disabled.", getDescription().getVersion());
    }
    
    @Override
    public void onEnable() {
        loadConfig();
        ecoEnabled = getServer().getPluginManager().getPlugin("Vault") != null;
        if(ecoEnabled) {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (rsp == null) {
                eco = null;
            }
            eco = rsp.getProvider();
        }
        getServer().getPluginManager().registerEvents(this, this);
        Logger.getLogger("Minecraft").log(Level.INFO, "[SpawnerGUI] Version {0} enabled.", getDescription().getVersion());
    }
    
    private void loadConfig() {
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        
        for(EntityType e : EntityType.values()) {
            if(e.isAlive() && e.getTypeId() != -1) {
                getConfig().addDefault("Mobs." + e.getName(), 0.0);
            }
        }
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
    
    public double getPrice(EntityType type) {
        return getConfig().getDouble("Mobs." + type.getName());
    }
    
    public void openGUI(final CreatureSpawner spawner, final Player p) {
        final EntityType type = spawner.getSpawnedType();
        GUIHandler gui;
        
        gui = new GUIHandler("Spawner Type: " + type.getName(), 36, new GUIHandler.OptionClickEventHandler() {
            double cost;
            
            @Override
            public void onOptionClick(GUIHandler.OptionClickEvent event) {
                event.setWillClose(true);
                String clicked = event.getName().toLowerCase();
                clicked = ChatColor.stripColor(clicked);
                
                for(int i = 0; i < EntityType.values().length; i++) {
                    EntityType e = EntityType.values()[i];
                    
                    if(e.isAlive() && clicked.equalsIgnoreCase(e.getName().toLowerCase())) {
                        p.playSound(p.getLocation(), Sound.CLICK, 1, 1);

                        if(p.hasPermission("spawnergui.edit.*") || p.hasPermission("spawnergui.edit." + clicked)) {
                            if(ecoEnabled && !p.hasPermission("spawnergui.eco.bypass.*")) {
                                cost = (p.hasPermission("spawnergui.eco.bypass." + clicked)) ? 0 : getPrice(e);
                                
                                if(cost > 0.0) {
                                    if(!eco.has(p.getName(), cost)) {
                                        p.sendMessage("§cYou need at least §7" + cost + " §cin currency to do this!");
                                        return;
                                    }
                                    eco.withdrawPlayer(p.getName(), cost);
                                }
                            }
                            spawner.setSpawnedType(e);
                            spawner.update(true);
                            p.sendMessage("§9Spawner type changed from §7" + type.getName().toLowerCase() + " §9to §7" + clicked + "§9!");
                            return;
                        }
                        p.sendMessage(ChatColor.RED + "You are not allowed to change to that type!");
                        break;
                    }
                }
            }
        }, this, true);
        int j = 0;
        
        for(int i = 0; i < EntityType.values().length; i++) {
            EntityType e = EntityType.values()[i];
            
            if(e.isAlive() && j < 36) {
                if(e.getTypeId() == -1) continue; // Prevent random null egg from appearing in the GUI.
                
                gui.setOption(j, getSpawnEgg(e), "§6" + e.getName(), "§7Set spawner type to: §a" + e.getName());
                j++;
            }
        }
        gui.open(p);
    }
    
    private ItemStack getSpawnEgg(EntityType type) {
        return new ItemStack(383, 1, type.getTypeId());
    }
    
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block b = event.getClickedBlock();
            
            if(b != null && b.getTypeId() == 52) {
                Player p = event.getPlayer();
                
                if(p.hasPermission("spawnergui.open")) {
                    if(!p.isSneaking()) {
                        event.setCancelled(true);
                        openGUI((CreatureSpawner)b.getState(), p);
                    }
                }
            }
        }
    }
}
