package com.leonardobishop.playerskills2.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;

public class MenuController implements Listener {

    private static HashMap<Player, Menu> tracker = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (tracker.containsKey((Player) event.getWhoClicked())) {
            if ((event.getWhoClicked().getOpenInventory() != null)
                    && (event.getClickedInventory() == event.getWhoClicked().getOpenInventory().getTopInventory())) {
                event.setCancelled(true);
                Menu menu = tracker.get((Player) event.getWhoClicked());
                menu.onClick(event.getSlot());
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (tracker.containsKey((Player) event.getPlayer())) {
            Menu menu = tracker.get((Player) event.getPlayer());
            tracker.remove((Player) event.getPlayer());
            menu.onClose();
        }
    }

    public static void open(Player player, Menu menu) {
        player.openInventory(menu.toInventory());
        tracker.put(player, menu);
    }

    public static boolean isMenuOpenElsewhere(Class<?> type) {
        for (Menu menu : tracker.values()) {
            if (menu.getClass() == type) {
                return true;
            }
        }
        return false;
    }

}
