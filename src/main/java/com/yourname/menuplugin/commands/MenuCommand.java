package com.yourname.menuplugin.commands;

import com.yourname.menuplugin.MenuPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand implements CommandExecutor {
    
    private final MenuPlugin plugin;
    
    public MenuCommand(MenuPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("§c只有玩家才能使用此指令！"));
            return true;
        }
        
        if (args.length == 0) {
            // /menu - 開啟選單
            plugin.getMenuManager().openMainMenu(player);
            return true;
        }
        
        if (args.length == 1 && args[0].equalsIgnoreCase("setup")) {
            // /menu setup - 開啟編輯器
            if (!player.hasPermission("menuplugin.setup")) {
                player.sendMessage(Component.text("§c你沒有權限使用此指令！"));
                return true;
            }
            plugin.getMenuManager().openMenuEditor(player);
            return true;
        }
        
        player.sendMessage(Component.text("§c用法: /menu 或 /menu setup"));
        return true;
    }
}