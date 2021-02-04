package com.leonardobishop.playerskills2.events;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.menu.ConfigSkillMenu;
import com.leonardobishop.playerskills2.menu.MenuController;
import com.leonardobishop.playerskills2.utils.ConfigEditWrapper;
import com.leonardobishop.playerskills2.utils.ConfigType;
import com.leonardobishop.playerskills2.utils.CreatorConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class ChatEvent implements Listener {

    private PlayerSkills plugin;
    private HashMap<Player, ConfigEditWrapper> creatorConfigValue = new HashMap<>();

    public ChatEvent(PlayerSkills plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (creatorConfigValue.containsKey(event.getPlayer())) {
            event.setCancelled(true);
            ConfigEditWrapper wrapper = creatorConfigValue.get(event.getPlayer());
            CreatorConfigValue conf = wrapper.getConfigValue();
            creatorConfigValue.remove(event.getPlayer());

            String message = event.getMessage();
            boolean success = false;

            if (conf.getType() == ConfigType.INTEGER) {
                try {
                    int m = Integer.parseInt(message);
                    Bukkit.getScheduler().runTask(plugin, () -> conf.setValue(m));
                    success = true;
                } catch (Exception ex) {
//                    event.getPlayer().sendMessage(ChatColor.RED + "The value must be an integer. This is a number which cannot contain a fractional component.");
                }
            } else if (conf.getType() == ConfigType.DOUBLE || conf.getType() == ConfigType.NUMBER) {
                try {
                    double m = Double.parseDouble(message);
                    Bukkit.getScheduler().runTask(plugin, () -> conf.setValue(m));
                    success = true;
                } catch (Exception ex) {
//                    event.getPlayer().sendMessage(ChatColor.RED + "The value must be an double. This is a number which can include fractional components.");
                }
            } else if (conf.getType() == ConfigType.BOOLEAN) {
                try {
                    boolean m = Boolean.parseBoolean(message);
                    Bukkit.getScheduler().runTask(plugin, () -> conf.setValue(m));
                    success = true;
                } catch (Exception ex) {
//                    event.getPlayer().sendMessage(ChatColor.RED + "The value must either be true or false.");
                }
            } else if (conf.getType() == ConfigType.STRING) {
                Bukkit.getScheduler().runTask(plugin, () -> conf.setValue(message));
                success = true;
            }

            final boolean trueSuccess = success;
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (trueSuccess) {
                    event.getPlayer().sendMessage(ChatColor.GREEN + "Value of " + ChatColor.WHITE + conf.getKey() + ChatColor.GREEN + " changed to " + ChatColor.WHITE +  conf.getValue() + ".");
                } else {
                    event.getPlayer().sendMessage(ChatColor.RED + "There was an error with your input. The value has not been changed.");
                }
                ConfigSkillMenu menu = new ConfigSkillMenu(plugin, event.getPlayer(), wrapper.getSkill());
                MenuController.open(event.getPlayer(), menu);
            });
        }
    }

    @EventHandler
    public void onChat(PlayerCommandPreprocessEvent event) {
        if (creatorConfigValue.containsKey(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    public HashMap<Player, ConfigEditWrapper> getCreatorConfigValue() {
        return creatorConfigValue;
    }
}
