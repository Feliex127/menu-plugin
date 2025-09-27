package com.yourname.menuplugin.listeners;

import com.yourname.menuplugin.MenuPlugin;
import com.yourname.menuplugin.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class MenuClickListener implements Listener {
    
    private final MenuPlugin plugin;
    
    public MenuClickListener(MenuPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        Component title = event.getView().title();
        
        // 主選單點擊處理
        if (title.equals(Component.text("§6§l主選單"))) {
            event.setCancelled(true);
            
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
            
            int slot = event.getRawSlot();
            if (slot >= 54) return;
            
            // 獲取對應的指令並執行
            Map<Integer, ConfigManager.MenuItem> menuItems = plugin.getConfigManager().getMenuItems();
            ConfigManager.MenuItem menuItem = menuItems.get(slot);
            
            if (menuItem != null) {
                plugin.getMenuManager().executeCommands(player, menuItem.getCommands());
                player.closeInventory();
            }
        }
    }
}