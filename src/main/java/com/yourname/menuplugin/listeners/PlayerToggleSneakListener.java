package com.yourname.menuplugin.listeners;

import com.yourname.menuplugin.MenuPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerToggleSneakListener implements Listener {
    
    private final MenuPlugin plugin;
    
    public PlayerToggleSneakListener(MenuPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        if (!event.isSneaking()) return;
        
        if (isSwappingHands(player)) {
            if (player.hasMetadata("menu_cooldown")) return;
            
            player.setMetadata("menu_cooldown", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                player.removeMetadata("menu_cooldown", plugin);
            }, 10L);
            
            plugin.getMenuManager().openMainMenu(player);
        }
    }
    
    private boolean isSwappingHands(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack mainHand = inventory.getItemInMainHand();
        ItemStack offHand = inventory.getItemInOffHand();
        
        boolean mainHandNotEmpty = mainHand.getType() != Material.AIR;
        boolean offHandNotEmpty = offHand.getType() != Material.AIR;
        
        return (mainHandNotEmpty && offHandNotEmpty) || (!mainHandNotEmpty && !offHandNotEmpty);
    }
}