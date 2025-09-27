package com.yourname.menuplugin.gui;

import com.yourname.menuplugin.MenuPlugin;
import com.yourname.menuplugin.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class MainMenuGUI {
    
    private final MenuPlugin plugin;
    private final Inventory inventory;
    
    public MainMenuGUI(MenuPlugin plugin) {
        this.plugin = plugin;
        this.inventory = createInventory();
    }
    
    private Inventory createInventory() {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("§6§l主選單"));
        
        // 填充背景
        ItemStack background = createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, background);
        }
        
        // 從配置載入選單項目
        Map<Integer, ConfigManager.MenuItem> menuItems = plugin.getConfigManager().getMenuItems();
        for (Map.Entry<Integer, ConfigManager.MenuItem> entry : menuItems.entrySet()) {
            int slot = entry.getKey();
            ConfigManager.MenuItem menuItem = entry.getValue();
            
            ItemStack item = createItem(menuItem.getMaterial(), menuItem.getName(), menuItem.getLore());
            inv.setItem(slot, item);
        }
        
        return inv;
    }
    
    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.displayName(Component.text(name));
            if (lore != null) {
                meta.lore(lore.stream().map(Component::text).toList());
            }
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public void open(Player player) {
        player.openInventory(inventory);
    }
    
    public Inventory getInventory() {
        return inventory;
    }
}