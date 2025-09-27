package com.yourname.menuplugin.managers;

import com.yourname.menuplugin.MenuPlugin;
import com.yourname.menuplugin.gui.MainMenuGUI;
import com.yourname.menuplugin.gui.MenuEditorGUI;
import com.yourname.menuplugin.utils.ConfigManager;
import org.bukkit.entity.Player;

public class MenuManager {
    
    private final MenuPlugin plugin;
    private final ConfigManager configManager;
    
    public MenuManager(MenuPlugin plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }
    
    public void openMainMenu(Player player) {
        MainMenuGUI menu = new MainMenuGUI(plugin);
        menu.open(player);
    }
    
    public void openMenuEditor(Player player) {
        MenuEditorGUI editor = new MenuEditorGUI(plugin);
        editor.open(player);
    }
    
    public void executeCommands(Player player, java.util.List<String> commands) {
        for (String command : commands) {
            if (command.startsWith("console:")) {
                // 後台指令
                String consoleCommand = command.substring(8).replace("{player}", player.getName());
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), consoleCommand);
            } else if (command.startsWith("player:")) {
                // 玩家指令
                String playerCommand = command.substring(7).replace("{player}", player.getName());
                player.performCommand(playerCommand);
            } else if (command.startsWith("op:")) {
                // OP指令
                String opCommand = command.substring(3).replace("{player}", player.getName());
                boolean wasOp = player.isOp();
                try {
                    player.setOp(true);
                    player.performCommand(opCommand);
                } finally {
                    if (!wasOp) player.setOp(false);
                }
            } else {
                // 默認為玩家指令
                String formattedCommand = command.replace("{player}", player.getName());
                player.performCommand(formattedCommand);
            }
        }
    }
}