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

import java.util.Arrays;
import java.util.Map;

public class MenuEditorGUI {
    
    private final MenuPlugin plugin;
    private final Inventory inventory;
    
    public MenuEditorGUI(MenuPlugin plugin) {
        this.plugin = plugin;
        this.inventory = createInventory();
    }
    
    private Inventory createInventory() {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text("§c§l選單編輯器"));
        
        // 填充背景
        ItemStack background = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        ItemStack emptySlot = createItem(Material.WHITE_STAINED_GLASS_PANE, "§a空槽位", 
            Arrays.asList("§7點擊此槽位添加新項目"));
        
        for (int i = 0; i < 54; i++) {
            if (i < 45) {
                inv.setItem(i, emptySlot);
            } else {
                inv.setItem(i, background);
            }
        }
        
        // 從配置載入現有項目
        Map<Integer, ConfigManager.MenuItem> menuItems = plugin.getConfigManager().getMenuItems();
        for (Map.Entry<Integer, ConfigManager.MenuItem> entry : menuItems.entrySet()) {
            int slot = entry.getKey();
            ConfigManager.MenuItem menuItem = entry.getValue();
            
            ItemStack item = createItem(menuItem.getMaterial(), 
                "§e" + menuItem.getName(), 
                Arrays.asList(
                    "§7指令: " + String.join(", ", menuItem.getCommands()),
                    "§6左鍵點擊編輯",
                    "§c右鍵點擊刪除"
                ));
            inv.setItem(slot, item);
        }
        
        // 控制按鈕
        ItemStack saveButton = createItem(Material.EMERALD, "§a§l保存選單", 
            Arrays.asList("§7點擊保存所有變更"));
        inv.setItem(49, saveButton);
        
        ItemStack helpButton = createItem(Material.BOOK, "§b§l使用說明", 
            Arrays.asList("§7左鍵空槽位: 添加項目",
                         "§7左鍵現有項目: 編輯",
                         "§7右鍵現有項目: 刪除",
                         "§7點擊保存: 應用變更"));
        inv.setItem(53, helpButton);
        
        return inv;
    }
    
    private ItemStack createItem(Material material, String name, java.util.List<String> lore) {
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