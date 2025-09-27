package com.yourname.menuplugin.utils;

import com.yourname.menuplugin.MenuPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConfigManager {
    
    private final MenuPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration menuData;
    private File menuDataFile;
    
    public ConfigManager(MenuPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        // 載入主配置
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        // 載入選單數據
        menuDataFile = new File(plugin.getDataFolder(), "menu_data.yml");
        if (!menuDataFile.exists()) {
            plugin.saveResource("menu_data.yml", false);
        }
        menuData = YamlConfiguration.loadConfiguration(menuDataFile);
    }
    
    public void saveMenuData() {
        try {
            menuData.save(menuDataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("無法保存選單數據: " + e.getMessage());
        }
    }
    
    public Map<Integer, MenuItem> getMenuItems() {
        Map<Integer, MenuItem> items = new HashMap<>();
        if (menuData.contains("menu.items")) {
            for (String key : menuData.getConfigurationSection("menu.items").getKeys(false)) {
                int slot = Integer.parseInt(key);
                String path = "menu.items." + key;
                
                Material material = Material.valueOf(menuData.getString(path + ".material", "STONE"));
                String name = menuData.getString(path + ".name", "未命名");
                List<String> lore = menuData.getStringList(path + ".lore");
                List<String> commands = menuData.getStringList(path + ".commands");
                
                items.put(slot, new MenuItem(material, name, lore, commands));
            }
        }
        return items;
    }
    
    public void setMenuItem(int slot, MenuItem menuItem) {
        String path = "menu.items." + slot;
        menuData.set(path + ".material", menuItem.getMaterial().name());
        menuData.set(path + ".name", menuItem.getName());
        menuData.set(path + ".lore", menuItem.getLore());
        menuData.set(path + ".commands", menuItem.getCommands());
    }
    
    public void removeMenuItem(int slot) {
        menuData.set("menu.items." + slot, null);
    }
    
    public static class MenuItem {
        private Material material;
        private String name;
        private List<String> lore;
        private List<String> commands;
        
        public MenuItem(Material material, String name, List<String> lore, List<String> commands) {
            this.material = material;
            this.name = name;
            this.lore = lore != null ? lore : new ArrayList<>();
            this.commands = commands != null ? commands : new ArrayList<>();
        }
        
        // Getters and Setters
        public Material getMaterial() { return material; }
        public void setMaterial(Material material) { this.material = material; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public List<String> getLore() { return lore; }
        public void setLore(List<String> lore) { this.lore = lore; }
        
        public List<String> getCommands() { return commands; }
        public void setCommands(List<String> commands) { this.commands = commands; }
    }
}