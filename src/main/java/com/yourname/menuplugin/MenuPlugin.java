package com.yourname.menuplugin;

import com.yourname.menuplugin.commands.MenuCommand;
import com.yourname.menuplugin.commands.TabCompleter;
import com.yourname.menuplugin.listeners.InventoryEditListener;
import com.yourname.menuplugin.listeners.MenuClickListener;
import com.yourname.menuplugin.listeners.PlayerToggleSneakListener;
import com.yourname.menuplugin.managers.MenuManager;
import com.yourname.menuplugin.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MenuPlugin extends JavaPlugin {
    
    private MenuManager menuManager;
    private ConfigManager configManager;
    
    @Override
    public void onEnable() {
        // 初始化管理器
        this.configManager = new ConfigManager(this);
        this.menuManager = new MenuManager(this);
        
        // 載入配置
        configManager.loadConfig();
        
        // 註冊事件監聽器
        getServer().getPluginManager().registerEvents(new PlayerToggleSneakListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuClickListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryEditListener(this), this);
        
        // 註冊指令
        getCommand("menu").setExecutor(new MenuCommand(this));
        getCommand("menu").setTabCompleter(new TabCompleter());
        
        getLogger().info("選單插件已啟用！");
    }
    
    @Override
    public void onDisable() {
        // 保存配置
        configManager.saveMenuData();
        getLogger().info("選單插件已禁用！");
    }
    
    public MenuManager getMenuManager() {
        return menuManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
}