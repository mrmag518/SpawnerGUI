package com.mrmag518.SpawnerGUI;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnerGUI extends JavaPlugin {
    @Override
    public void onDisable() {
        Logger.getLogger("Minecraft").log(Level.INFO, "[SpawnerGUI] Version {0} disabled.", getVersion());
    }
    
    @Override
    public void onEnable() {
        EventListener listener = new EventListener(this);
        Logger.getLogger("Minecraft").log(Level.INFO, "[SpawnerGUI] Version {0} enabled.", getVersion());
    }
    
    public String getVersion() {
        PluginDescriptionFile pdffile = getDescription();
        return pdffile.getVersion().replace("v", "");
    }
    
    public void openGUI(final CreatureSpawner spawner, final Player p) {
        final EntityType type = spawner.getSpawnedType();
        GUIHandler gui;
        
        gui = new GUIHandler("Spawner Type: " + type.getName(), 36, new GUIHandler.OptionClickEventHandler() {
            @Override
            public void onOptionClick(GUIHandler.OptionClickEvent event) {
                event.setWillClose(true);
                String clicked = event.getName().toLowerCase();
                clicked = ChatColor.stripColor(clicked);
                
                for(int i = 0; i < EntityType.values().length; i++) {
                    EntityType e = EntityType.values()[i];
                    
                    if(e.isAlive()) {
                        if(clicked.equalsIgnoreCase(e.getName().toLowerCase())) {
                            p.playSound(p.getLocation(), Sound.CLICK, 1, 1);
                            
                            if(p.hasPermission("spawnergui.edit.*") || p.hasPermission("spawner.edit." + clicked)) {
                                spawner.setSpawnedType(e);
                                spawner.update();
                                break;
                            } else {
                                p.sendMessage(ChatColor.RED + "You are not allowed to change to that type!");
                                return;
                            }
                        }
                    }
                }
                p.sendMessage("§9Spawner type changed from §7" + type.getName().toLowerCase() + " §9to §7" + clicked + "§9!");
            }
        }, this, true);
        int j = 0;
        
        for(int i = 0; i < EntityType.values().length; i++) {
            EntityType e = EntityType.values()[i];
            
            if(e.isAlive()) {
                if(j < 36) {
                    // Random null egg appearing in GUI for some reason.
                    if(e.getTypeId() == -1) {
                        continue;
                    }
                    gui.setOption(j, getSpawnEgg(e), "§6" + e.getName(), "§7Set spawner type to: ", "§a" + e.getName());
                    j++;
                }
            }
        }
        gui.open(p);
    }
    
    private ItemStack getSpawnEgg(EntityType type) {
        return new ItemStack(383, 1, type.getTypeId());
    }
    
    public class EventListener implements Listener {
        public SpawnerGUI plugin;
        public EventListener(SpawnerGUI instance) {
            plugin = instance;
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
        
        @EventHandler
        public void handleInteract(PlayerInteractEvent event) {
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Block b = event.getClickedBlock();
                
                if(b.getTypeId() == 52) {
                    Player p = event.getPlayer();
                    
                    if(p.hasPermission("spawnergui.open")) {
                        if(!p.isSneaking()) {
                            event.setCancelled(true);
                            CreatureSpawner spawner = (CreatureSpawner)b.getState();
                            plugin.openGUI(spawner, p);
                        }
                    }
                }
            }
        }
    }
}
