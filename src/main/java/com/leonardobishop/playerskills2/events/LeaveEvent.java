package com.leonardobishop.playerskills2.events;

import com.leonardobishop.playerskills2.PlayerSkills;
import com.leonardobishop.playerskills2.player.SPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveEvent implements Listener {

    private PlayerSkills plugin;

    public LeaveEvent(PlayerSkills plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        SPlayer.save(plugin, SPlayer.get(event.getPlayer().getUniqueId()));
        SPlayer.unload(event.getPlayer().getUniqueId());
    }

}
