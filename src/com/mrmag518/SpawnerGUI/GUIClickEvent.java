package com.mrmag518.spawnergui;

import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class GUIClickEvent extends Event {
    private final static HandlerList handlers = new HandlerList();
    private final int slot;
    private final Player player;
    private final CreatureSpawner spawner;
    private boolean willClose = true;
    
    public GUIClickEvent(int slot, Player player, CreatureSpawner spawner) {
        this.slot = slot;
        this.player = player;
        this.spawner = spawner;
    }

    public int getSlot() {
        return slot;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public CreatureSpawner getSpawner() {
        return spawner;
    }
    
    public ItemStack getItem() {
        return player.getOpenInventory().getTopInventory().getItem(slot);
    }
    
    public boolean willClose() {
        return willClose;
    }
    
    public void setWillClose(boolean state) {
        this.willClose = state;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
