package com.yourname.menuplugin.listeners;

import com.yourname.menuplugin.MenuPlugin;
import com.yourname.menuplugin.gui.MenuEditorGUI;
import com.yourname.menuplugin.utils.ConfigManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryEditListener implements Listener {
    
    private final MenuPlugin plugin;
    private final Map<UUID, Integer> editingPlayers = new HashMap<>();
    private final Map<UUID, ConfigManager.MenuItem> pendingEdits = new HashMap<>();
    
    public InventoryEditListener(MenuPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        Component title = event.getView().title();
        
        // 編輯器點擊處理
        if (title.equals(Component.text("§c§l選單編輯器"))) {
            event.setCancelled(true);
            
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
            
            int slot = event.getRawSlot();
            
            if (slot == 49) { // 保存按鈕
                plugin.getConfigManager().saveMenuData();
                player.sendMessage(Component.text("§a選單配置已保存！"));
                player.closeInventory();
                return;
            }
            
            if (slot >= 45 && slot < 54) return; // 控制欄位
            
            Map<Integer, ConfigManager.MenuItem> menuItems = plugin.getConfigManager().getMenuItems();
            
            if (event.isRightClick()) {
                // 右鍵刪除
                if (menuItems.containsKey(slot)) {
                    plugin.getConfigManager().removeMenuItem(slot);
                    player.sendMessage(Component.text("§c已刪除槽位 " + slot + " 的項目"));
                    new MenuEditorGUI(plugin).open(player);
                }
            } else {
                // 左鍵編輯/添加
                editingPlayers.put(player.getUniqueId(), slot);
                
                if (menuItems.containsKey(slot)) {
                    // 編輯現有項目
                    pendingEdits.put(player.getUniqueId(), menuItems.get(slot));
                    player.sendMessage(Component.text("§e請在聊天欄輸入新的物品材質（如: STONE、DIAMOND）"));
                } else {
                    // 添加新項目
                    pendingEdits.put(player.getUniqueId(), new ConfigManager.MenuItem(
                        Material.STONE, "新項目", 
                        java.util.Arrays.asList("點擊執行指令"), 
                        java.util.Arrays.asList("say Hello {player}")
                    ));
                    player.sendMessage(Component.text("§a請在聊天欄輸入物品材質（如: STONE、DIAMOND）"));
                }
                player.closeInventory();
            }
        }
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        if (!editingPlayers.containsKey(playerId)) return;
        
        event.setCancelled(true);
        int slot = editingPlayers.get(playerId);
        ConfigManager.MenuItem menuItem = pendingEdits.get(playerId);
        
        String message = event.getMessage();
        
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (menuItem.getMaterial() == Material.STONE) {
                // 設置材質
                try {
                    Material material = Material.valueOf(message.toUpperCase());
                    menuItem.setMaterial(material);
                    player.sendMessage(Component.text("§a材質設置為: " + material.name()));
                    player.sendMessage(Component.text("§e請輸入物品顯示名稱"));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(Component.text("§c無效的材質！請輸入有效的材質名稱"));
                }
            } else if (menuItem.getName().equals("新項目")) {
                // 設置名稱
                menuItem.setName(message);
                player.sendMessage(Component.text("§a名稱設置為: " + message));
                player.sendMessage(Component.text("§e請輸入要執行的指令（多個指令用分號 ; 分隔）"));
                player.sendMessage(Component.text("§6可用變量: {player} - 玩家名稱"));
                player.sendMessage(Component.text("§6格式: console:指令 - 後台執行; player:指令 - 玩家執行"));
            } else {
                // 設置指令
                String[] commands = message.split(";");
                menuItem.setCommands(java.util.Arrays.asList(commands));
                plugin.getConfigManager().setMenuItem(slot, menuItem);
                
                player.sendMessage(Component.text("§a指令設置完成！"));
                player.sendMessage(Component.text("§a項目已添加到槽位 " + slot));
                
                editingPlayers.remove(playerId);
                pendingEdits.remove(playerId);
                
                new MenuEditorGUI(plugin).open(player);
            }
        });
    }
}